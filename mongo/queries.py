import pymongo
import time


client = pymongo.MongoClient("mongodb://mongo:zaq1%40WSX@localhost:27017/")

db = client["yelp"]


start_time = time.time()

ids = ["gmjsEdUsKpj9Xxu6pdjH0g","e4Vwtrqf-wpJfwesgvdgxQ", "04UD14gamNjLY0IDYVhHJg"]

reviews = db.reviews

samples_count = 1000
for i in range(0, samples_count):
    reviews.find({
        "business_id": ids[i % len(ids)]
    })


total = time.time() - start_time
print("Fetch all reviews of bussiness")
print(f"Total: {total}, average: {total / samples_count}")

start_time = time.time()
for i in range(0, samples_count):
    reviews.aggregate([{
            "$match": {
                "business_id": ids[i % len(ids)],
            },
        },
        {
            "$group": {
                "_id": "$business_id",
                "avgStars": { "$avg": "$stars" },
                # "voteCount": { "$sum": 1 },
            },
        }
    ])

total = time.time() - start_time
print("Fetch average rating of bussiness")
print(f"Total: {total}, average: {total / samples_count}")



start_time = time.time()
for i in range(0, samples_count):
    reviews.count_documents(
        {"business_id": ids[i % len(ids)]}
    )


total = time.time() - start_time
print("Fetch review count of bussiness")
print(f"Total: {total}, average: {total / samples_count}")