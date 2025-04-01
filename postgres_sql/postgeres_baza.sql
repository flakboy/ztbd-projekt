DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS votes CASCADE;
DROP TABLE IF EXISTS vote_types CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS hours CASCADE;
DROP TABLE IF EXISTS businesses CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    registration_date DATE
);

CREATE TABLE businesses (
    business_id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    categories VARCHAR(300)
);

CREATE TABLE reviews (
    review_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    business_id INT NOT NULL,
    rating DECIMAL(3,1) NOT NULL,
    review_text VARCHAR(300),
    review_date DATE,
    CONSTRAINT fk_reviews_users
        FOREIGN KEY (user_id)
        REFERENCES users (user_id),
    CONSTRAINT fk_reviews_businesses
        FOREIGN KEY (business_id)
        REFERENCES businesses (business_id)
);

CREATE TABLE vote_types (
    type_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE votes (
    vote_id SERIAL PRIMARY KEY,
    type_id INT NOT NULL,
    user_id INT NOT NULL,
    review_id INT NOT NULL,
    vote_date DATE,
    CONSTRAINT fk_votes_vote_types
        FOREIGN KEY (type_id)
        REFERENCES vote_types (type_id),
    CONSTRAINT fk_votes_users
        FOREIGN KEY (user_id)
        REFERENCES users (user_id),
    CONSTRAINT fk_votes_reviews
        FOREIGN KEY (review_id)
        REFERENCES reviews (review_id)
);

CREATE TABLE friends (
    friends_id SERIAL PRIMARY KEY,
    user1_id INT NOT NULL,
    user2_id INT NOT NULL,
    confirmed BOOLEAN,
    CONSTRAINT fk_friends_user1
        FOREIGN KEY (user1_id)
        REFERENCES users (user_id),
    CONSTRAINT fk_friends_user2
        FOREIGN KEY (user2_id)
        REFERENCES users (user_id)
);

CREATE TABLE hours (
    hours_id SERIAL PRIMARY KEY,
    business_id INT NOT NULL,
    monday VARCHAR(50),
    tuesday VARCHAR(50),
    wednesday VARCHAR(50),
    thursday VARCHAR(50),
    friday VARCHAR(50),
    saturday VARCHAR(50),
    sunday VARCHAR(50),
    CONSTRAINT fk_hours_businesses
        FOREIGN KEY (business_id)
        REFERENCES businesses (business_id)
);

