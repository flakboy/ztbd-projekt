TRACING ON;

CREATE KEYSPACE IF NOT EXISTS yelp WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

USE yelp;


DROP TABLE IF EXISTS votes_per_review;
CREATE TABLE IF NOT EXISTS votes_per_review (
    -- Cassandra doesn't support fixed length strings
    vote_id varchar,
    review_id varchar,
    voter_id varchar,
    date_reviewed timestamp,
    PRIMARY KEY ((review_id), vote_id, voter_id, date_reviewed)
);


DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
    user_id text,
    user_name varchar,
    yelping_since timestamp,
    PRIMARY KEY (user_id)
);

DROP TABLE IF EXISTS reviews_per_business;
CREATE TABLE IF NOT EXISTS reviews_per_business (
    review_id text,
    user_id text,
    business_id text,
    review_text varchar,
    date_reviewed timestamp,
    PRIMARY KEY ((business_id), review_id, date_reviewed)
);

DROP TABLE IF EXISTS reviews_per_users;
CREATE TABLE IF NOT EXISTS reviews_per_users (
    review_id text,
    user_id text,
    business_id text,
    review_text varchar,
    date_reviewed timestamp,
    PRIMARY KEY ((user_id), review_id, date_reviewed)
);


-- INSERT INTO user (name, addresses)
--    VALUES ('z3 Pr3z1den7', {
--      'home' : {
--         street: '1600 Pennsylvania Ave NW',
--         city: 'Washington',
--         zip: '20500',
--         phones: { 'cell' : { country_code: 1, number: '202 456-1111' },
--                   'landline' : { country_code: 1, number: '...' } }
--      },
--   }
-- );

CREATE TYPE IF NOT EXISTS opening_hours(
    monday tuple<time, time>,
    tuesday tuple<time, time>,
    wednesday tuple<time, time>,
    thursday tuple<time, time>,
    friday tuple<time, time>,
    saturday tuple<time, time>,
    sunday tuple<time, time>
);

DROP TABLE IF EXISTS businessess;
CREATE TABLE IF NOT EXISTS businessess (
    business_id text,
    name varchar,
    address varchar,
    city varchar,
    state varchar,
    postal_code varchar,
    latitude decimal,
    longitude decimal,
    hours opening_hours,
    PRIMARY KEY ((state), name, business_id, city)
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