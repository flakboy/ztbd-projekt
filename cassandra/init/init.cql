TRACING ON;

-- **** EXAMPLE SCRIPT         **** 
-- **** WILL BE CHANGED LATER  **** 

-- Create a keyspace
-- CREATE KEYSPACE IF NOT EXISTS store WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

CREATE KEYSPACE IF NOT EXISTS yelp WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

USE yelp;

DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
    user_id text,
    user_name varchar,
    yelping_since timestamp,
    PRIMARY KEY (user_id)
);

DROP TABLE IF EXISTS reviews;
CREATE TABLE IF NOT EXISTS reviews (
    review_id text,
    user_id text,
    business_id text,
    review_text varchar,
    date_reviewed timestamp,
    PRIMARY KEY ((business_id), review_id, date_reviewed)
);

-- COPY reviews (
--     review_id,
--     user_id,
--     business_id,
--     review_text,
--     date_reviewed
-- ) FROM '/var/cql/reviews.csv'
-- WITH HEADER=TRUE
-- AND DELIMITER='|'
-- AND MAXATTEMPTS=15;

-- -- Insert some data
-- INSERT INTO store.shopping_cart
-- (userid, item_count, last_update_timestamp)
-- VALUES ('9876', 2, toTimeStamp(now()));
-- INSERT INTO store.shopping_cart
-- (userid, item_count, last_update_timestamp)
-- VALUES ('1234', 5, toTimeStamp(now()));