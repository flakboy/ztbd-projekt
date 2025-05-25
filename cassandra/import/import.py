from cassandra.cluster import Cluster
from cassandra.io.asyncioreactor import AsyncioConnection
import time
import math
import json


Cluster.connection_class = AsyncioConnection
#This will attempt to connection to a Cassandra instance
# on your local machine (127.0.0.1)
cluster = Cluster()
session = cluster.connect("yelp")

# user_lookup_stmt = session.prepare("SELECT * FROM users WHERE user_id=?")

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
    ) values (?, ?, ?)
""")


# test different batch sizes to compare insert speed
MAX_BATCH_SIZE = 1

def import_json_file(
        path: str,
        collection: str,
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

            if index % 10_000 == 0:
                print(f"Inserted {index} records!")

            doc = json.loads(line)
            row = doc_transform(doc)
            print(row)

            session.execute(insert_stmt, row)
        end = time.time()

        print(end - start)



def transform_review(doc):
    id_key = "review_id"
    id = doc.pop(id_key, None)
    doc["_id"] = id
    doc["votes"] = []

    del doc["useful"]
    del doc["funny"]
    del doc["cool"]



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



def transform_business(doc):
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
    return (
        doc["_id"],
        doc["name"],
        doc["address"],
        doc["city"],
        doc["state"],
        doc["postal_code"],
        doc["latitude"],
        doc["hours"],
    )

datasets = [
    ("../data/yelp_dataset/yelp_academic_dataset_business.json", "yelp.businesses", transform_business, insert_business_stmt),
    # ("../data/yelp_dataset/yelp_academic_dataset_user.json", "yelp.users", transform_user),
    # ("../data/yelp_dataset/yelp_academic_dataset_review.json", "yelp.reviews", transform_review)
]

max_count = 100_000

for dataset, collection, transform, statement in datasets:
    import_json_file(dataset, collection, transform, max_count, statement, max_count)


# # add votes to reviews
# vote_file_path = "../data/gen/votes.json"
# with open(vote_file_path, "rt", encoding="utf-8") as data:
#     operations = []
#     print(f"LOADING FILE: {vote_file_path}")
#     for line in data:
#
#         doc = json.loads(line)
#         review_id = doc.pop("review_id", None)
#
#         print(review_id)
#         print(db["reviews"].update_one(
#                 {"_id": review_id},
#                 {"$push": {"votes": doc}}
#         ))
#         # sleep(10000)
#
#
# client.close()