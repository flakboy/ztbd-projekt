import math
import pymongo
import json
import time

from pymongo import InsertOne

client = pymongo.MongoClient("mongodb://mongo:zaq1%40WSX@localhost:27017/")

db = client["yelp"]

# test different batch sizes to compare insert speed
# MAX_BATCH_SIZE = 10000
MAX_BATCH_SIZE = 1

# Internally, InsertMany uses BulkWrite so it should make no difference which one we use
# https://stackoverflow.com/questions/35758690/mongodb-insertmany-vs-bulkwrite
def import_json_file(path: str, collection: str, doc_transform, max_doc_count = math.inf):
    operations = []
    print(f"LOADING FILE: {path}")
    with open(path, "rt", encoding="utf-8") as data:
        start = time.time()
        for index, line in enumerate(data):
            if len(operations) >= MAX_BATCH_SIZE or index >= max_doc_count:
                result = client.bulk_write(operations, verbose_results=True)
                # print("batch inserted!", len(result.insert_results))
                operations = []

            if index >= max_doc_count:
                break

            if index % 10_000 == 0:
                print(f"Inserted {index} records!")

            doc = json.loads(line)
            doc_transform(doc)

            operations.append(
                InsertOne(
                    document=doc,
                    namespace=collection
                )
            )
        end = time.time()

        print(end - start)
    if len(operations) > 0:
        client.bulk_write(operations)


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



datasets = [
    ("../data/yelp_dataset/yelp_academic_dataset_business.json", "yelp.businesses", transform_business),
    ("../data/yelp_dataset/yelp_academic_dataset_user.json", "yelp.users", transform_user),
    ("../data/yelp_dataset/yelp_academic_dataset_review.json", "yelp.reviews", transform_review)
]

max_count = 100_000

for dataset, collection, transform in datasets:
    import_json_file(dataset, collection, transform, max_count)


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