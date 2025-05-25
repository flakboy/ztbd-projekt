from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from cassandra.io.asyncioreactor import AsyncioConnection
import time
import math
import json
import os 
from datetime import datetime

print("Connecting to Cassandra node...")
node_url = os.environ['NODE_URL']
user = os.environ['CASSANDRA_USER']
password = os.environ['CASSANDRA_PASSWORD']

auth_provider = PlainTextAuthProvider(
        username=user, password=password)

#This will attempt to connection to a Cassandra instance
# on your local machine (127.0.0.1)
cluster = Cluster([node_url], auth_provider=auth_provider)
session = cluster.connect()

session.set_keyspace('yelp')


insert_business_stmt = session.prepare("""
    INSERT INTO businesses (
        business_id,
        name,
        address,
        city,
        state,
        postal_code,
        latitude,
        longitude,
        hours
    ) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
""")

insert_vote_stmt = session.prepare("""
    INSERT INTO votes_per_review (
        vote_id,
        review_id,
        voter_id,
        date_reviewed
    ) VALUES (?, ?, ?, ?)
""")

insert_user_stmt = session.prepare("""
    INSERT INTO users (
        user_id,
        user_name,
        yelping_since
    ) VALUES (?, ?, ?)
""")

insert_review_per_business_stmt = session.prepare("""
    INSERT INTO reviews_per_business (
        review_id,
        user_id,
        business_id,
        stars,
        review_text,
        date_reviewed
    ) VALUES (?, ?, ?, ?, ?, ?)
""")

insert_review_per_user_stmt = session.prepare("""
    INSERT INTO reviews_per_users (
        review_id,
        user_id,
        business_id,
        stars,
        review_text,
        date_reviewed
    ) VALUES (?, ?, ?, ?, ?, ?)
""")


PATH_PREFIX = os.environ["PATH_PREFIX"] if "PATH_PREFIX" in os.environ else "../data/yelp_dataset/"
# test different batch sizes to compare insert speed
MAX_BATCH_SIZE = 1


def import_json_file(
        path: str,
        # TODO: remove collection parameter, it does nothing in the context of Cassandra
        _collection: str,
        doc_transform,
        insert_stmt,
        max_doc_count = math.inf
):
    print(f"LOADING FILE: {path}")
    with open(path, "rt", encoding="utf-8") as data:
        start = time.time()
        for index, line in enumerate(data):
            if index >= max_doc_count:
                break

            if index % 10000 == 0:
                print(f"Inserted {index} records!")

            doc = json.loads(line)
            row = doc_transform(doc)

            session.execute(insert_stmt, row)
        end = time.time()

        print(end - start)


def transform_review(doc):
    doc = doc.copy()

    doc["votes"] = []

    del doc["useful"]
    del doc["funny"]
    del doc["cool"]

    return (
        doc["review_id"],
        doc["user_id"],
        doc["business_id"],
        doc["stars"],
        doc["text"],
        datetime.strptime(doc["date"], "%Y-%m-%d %H:%M:%S")
    )


def transform_user(doc):
    id_key = "user_id"
    id = doc.pop(id_key, None)
    doc["_id"] = id

    del doc["review_count"]
    del doc["useful"]
    del doc["funny"]
    del doc["cool"]
    del doc["elite"]
    del doc["friends"]
    del doc["fans"]
    del doc["average_stars"]
    del doc["compliment_hot"]
    del doc["compliment_more"]
    del doc["compliment_profile"]
    del doc["compliment_cute"]
    del doc["compliment_list"]
    del doc["compliment_note"]
    del doc["compliment_plain"]
    del doc["compliment_cool"]
    del doc["compliment_funny"]
    del doc["compliment_writer"]
    del doc["compliment_photos"]


    return (
        doc["_id"],
        doc["name"],
        datetime.strptime(doc["yelping_since"], "%Y-%m-%d %H:%M:%S")
    )


def transform_business(doc):
    doc = doc.copy()
    id_key = "business_id"
    id = doc.pop(id_key, None)
    doc["_id"] = id

    del doc["stars"]
    del doc["review_count"]
    del doc["is_open"]

    # business_id,
    # name,
    # address,
    # city,
    # state,
    # postal_code,
    # latitude,
    # longitude,
    # hours

    # if "hours" in doc and doc["hours"] is not None:
    #     hours = {}
    #     for key in doc["hours"]:
    #         hours[key] = tuple(doc["hours"][key].split("-"))
    # else:
    #     hours = None

    return (
        doc["_id"],
        doc["name"],
        doc["address"],
        doc["city"],
        doc["state"],
        doc["postal_code"],
        doc["latitude"],
        doc["longitude"],
        # FIXME: fix hours to be parsed properly
        # hours,
        None
    )

datasets = [
    (os.path.join(PATH_PREFIX, "yelp_academic_dataset_business.json"), "yelp.businesses", transform_business, insert_business_stmt),
    (os.path.join(PATH_PREFIX, "yelp_academic_dataset_user.json"), "yelp.users", transform_user, insert_user_stmt),
]

max_count = 100_000

print("Starting importing the files...")
for dataset, collection, transform, statement in datasets:
    import_json_file(dataset, collection, transform, statement, max_count)


review_path = os.path.join(PATH_PREFIX, "yelp_academic_dataset_review.json")
coll = "yelp.reviews"
transform_per_user = transform_review
transform_per_business = transform_review

# Handling business separately because it requires inserting into multiple tables, which could be done in only one file iteration
print(f"LOADING FILE: {review_path}")
with open(review_path, "rt", encoding="utf-8") as data:
    start = time.time()
    for index, line in enumerate(data):
        if index >= max_count:
            break

        if index % 10000 == 0:
            print(f"Inserted {index} records!")

        doc = json.loads(line)
        row_per_business = transform_per_user(doc)
        row_per_user = transform_per_business(doc)

        # print(row_per_business)
        # print(row_per_user)
    
        session.execute(insert_review_per_business_stmt, row_per_business)
        session.execute(insert_review_per_user_stmt, row_per_user)
    end = time.time()

    print(end - start)



print("Imported records sucessfully.")