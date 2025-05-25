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







reviews = db.reviews
ids = ["gmjsEdUsKpj9Xxu6pdjH0g","e4Vwtrqf-wpJfwesgvdgxQ", "04UD14gamNjLY0IDYVhHJg"]
samples_count = 1000

select_business_reviews_stmt = session.prepare(
    "SELECT * FROM reviews_per_business WHERE business_id = ?"
)

select_avg_stars_stmt = session.prepare(
    "SELECT AVG(stars) FROM reviews_per_business WHERE business_id = ?"
)

select_review_count_stmt = session.prepare(
    "SELECT COUNT(*) FROM reviews_per_business WHERE business_id = ?"
)



start_time = time.time()
for i in range(0, samples_count):
    session.execute(select_business_reviews_stmt, ids[i % len(ids)])


total = time.time() - start_time
print("Fetch all reviews of bussiness")
print(f"Total: {total}, average: {total / samples_count}")

start_time = time.time()
for i in range(0, samples_count):
    session.execute(select_avg_stars_stmt, ids[i % len(ids)])

total = time.time() - start_time
print("Fetch average rating of bussiness")
print(f"Total: {total}, average: {total / samples_count}")



start_time = time.time()
for i in range(0, samples_count):
    session.execute(select_review_count_stmt, ids[i % len(ids)])


total = time.time() - start_time
print("Fetch review count of bussiness")
print(f"Total: {total}, average: {total / samples_count}")