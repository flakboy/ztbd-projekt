import pymongo
import json

from pymongo import InsertOne
client = pymongo.MongoClient("mongodb://mongo:zaq1%40WSX@localhost:27017/")

# test different batch sizes to compare insert speed
MAX_BATCH_SIZE = 10000

def import_json_file(path: str):
    operations = []
    with open(path, "rt", encoding="utf-8") as data:
        for line in data:
            if len(operations) >= MAX_BATCH_SIZE:
                result = client.bulk_write(operations, verbose_results=True)
                print("batch inserted!", result)
                operations = []

            operations.append(
                InsertOne(
                    document=json.loads(line),
                    namespace="yelp.businesses"
                )
            )

    #         client.get_database("local").get_collection("bussinesses").insert_many()

    if len(operations) > 0:
        client.bulk_write(operations)


businessPath = "../data/yelp_dataset/yelp_academic_dataset_business.json"

import_json_file(businessPath)

client.close()