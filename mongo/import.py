import pymongo
import json

from pymongo import InsertOne

client = pymongo.MongoClient("mongodb://mongo:zaq1%40WSX@localhost:27017/")

db = client["yelp"]

# test different batch sizes to compare insert speed
MAX_BATCH_SIZE = 10000

# Internally, InsertMany uses BulkWrite so it should make no difference which one we use
# https://stackoverflow.com/questions/35758690/mongodb-insertmany-vs-bulkwrite
def import_json_file(path: str, collection: str, doc_transform):
    operations = []
    print(f"LOADING FILE: {path}")
    with open(path, "rt", encoding="utf-8") as data:
        for line in data:
            if len(operations) >= MAX_BATCH_SIZE:
                result = client.bulk_write(operations, verbose_results=True)
                print("batch inserted!", len(result.insert_results))
                operations = []

            doc = json.loads(line)
            doc_transform(doc)

            operations.append(
                InsertOne(
                    document=doc,
                    namespace=collection
                )
            )

    if len(operations) > 0:
        client.bulk_write(operations)


def transform_review(doc):
    id_key = "review_id"
    id = doc.pop(id_key, None)
    doc["_id"] = id
    doc["votes"] = []

def transform_user(doc):
    id_key = "user_id"
    id = doc.pop(id_key, None)
    doc["_id"] = id

def transform_business(doc):
    id_key = "business_id"
    id = doc.pop(id_key, None)
    doc["_id"] = id


datasets = [
    ("../data/yelp_dataset/yelp_academic_dataset_business.json", "yelp.businesses", transform_business),
    ("../data/yelp_dataset/yelp_academic_dataset_user.json", "yelp.users", transform_user),
    ("../data/yelp_dataset/yelp_academic_dataset_review.json", "yelp.reviews", transform_review)
]

for dataset, collection, transform in datasets:
    import_json_file(dataset, collection, transform)


# add votes to reviews
vote_file_path = "../data/gen/votes.json"
with open(vote_file_path, "rt", encoding="utf-8") as data:
    operations = []
    print(f"LOADING FILE: {vote_file_path}")
    for line in data:

        doc = json.loads(line)
        review_id = doc.pop("review_id", None)

        print(review_id)
        print(db["reviews"].update_one(
                {"_id": review_id},
                {"$push": {"votes": doc}}
        ))
        # sleep(10000)


client.close()