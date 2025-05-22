-- Drop tables if they exist (SQL Server 2016+)
DROP TABLE IF EXISTS dbo.friends;
DROP TABLE IF EXISTS dbo.votes;
DROP TABLE IF EXISTS dbo.vote_types;
DROP TABLE IF EXISTS dbo.reviews;
DROP TABLE IF EXISTS dbo.hours;
DROP TABLE IF EXISTS dbo.businesses;
DROP TABLE IF EXISTS dbo.users;
GO

-- Users table
CREATE TABLE dbo.users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    registration_date DATE NULL
);
GO

-- Businesses table
CREATE TABLE dbo.businesses (
    business_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(200) NULL,
    city VARCHAR(100) NULL,
    state VARCHAR(100) NULL,
    postal_code VARCHAR(20) NULL,
    latitude DECIMAL(9,6) NULL,
    longitude DECIMAL(9,6) NULL,
    categories VARCHAR(MAX) NULL
);
GO

-- Reviews table
CREATE TABLE dbo.reviews (
    review_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    business_id INT NOT NULL,
    rating DECIMAL(3,1) NOT NULL,
    review_text VARCHAR(MAX) NULL,
    review_date DATE NULL,
    CONSTRAINT FK_reviews_users
        FOREIGN KEY (user_id)
        REFERENCES dbo.users (user_id),
    CONSTRAINT FK_reviews_businesses
        FOREIGN KEY (business_id)
        REFERENCES dbo.businesses (business_id)
);
GO

-- Vote types table
CREATE TABLE dbo.vote_types (
    type_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
GO

-- Votes table
CREATE TABLE dbo.votes (
    vote_id INT IDENTITY(1,1) PRIMARY KEY,
    type_id INT NOT NULL,
    user_id INT NOT NULL,
    review_id INT NOT NULL,
    vote_date DATE NULL,
    CONSTRAINT FK_votes_vote_types
        FOREIGN KEY (type_id)
        REFERENCES dbo.vote_types (type_id),
    CONSTRAINT FK_votes_users
        FOREIGN KEY (user_id)
        REFERENCES dbo.users (user_id),
    CONSTRAINT FK_votes_reviews
        FOREIGN KEY (review_id)
        REFERENCES dbo.reviews (review_id)
);
GO

-- Friends table
CREATE TABLE dbo.friends (
    friends_id INT IDENTITY(1,1) PRIMARY KEY,
    user1_id INT NOT NULL,
    user2_id INT NOT NULL,
    confirmed BIT NULL,
    CONSTRAINT FK_friends_user1
        FOREIGN KEY (user1_id)
        REFERENCES dbo.users (user_id),
    CONSTRAINT FK_friends_user2
        FOREIGN KEY (user2_id)
        REFERENCES dbo.users (user_id)
);
GO

-- Hours table
CREATE TABLE dbo.hours (
    hours_id INT IDENTITY(1,1) PRIMARY KEY,
    business_id INT NOT NULL,
    monday VARCHAR(50) NULL,
    tuesday VARCHAR(50) NULL,
    wednesday VARCHAR(50) NULL,
    thursday VARCHAR(50) NULL,
    friday VARCHAR(50) NULL,
    saturday VARCHAR(50) NULL,
    sunday VARCHAR(50) NULL,
    CONSTRAINT FK_hours_businesses
        FOREIGN KEY (business_id)
        REFERENCES dbo.businesses (business_id)
);
GO
