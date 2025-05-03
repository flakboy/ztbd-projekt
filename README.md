# Requirements
- OS: Windows with WSL2 active OR Linux 
- Docker

# How to run
**Before running any of the DBMSs create an `.env` file (the template is provided in the `.env.example` file)!**

## PostgreSQL 
1. Run the container with 
```
docker compose --profile postgres up
```
2. Connect to the database on `localhost:5432`
3. Log in using credentials passed to `POSTGRES_NAME` and `POSTGRES_PASSWORD` variables in the `.env` file.


## MS SQL
**NOTE: the `sa` password for MS SQL must satisfy given requirements or otherwise the container creation will fail:**

_At least 8 characters including uppercase, lowercase letters, base-10 digits and/or non-alphanumeric symbol_


1. Run the container with 
```
docker compose --profile mssql up
```
2. Connect to the database on `localhost:1433`
3. Log in to the `sa` account, using the password specified in the `MSSQL_SA_PASSWORD` variable.


## Cassandra
1. Initialize the database with command below. This should be done once, only order to create required keyspaces.
```
docker compose --profile cassandra-init up
```
2. Every next start-up should be performed with 
```
docker compose --profile cassandra up
```

## Mongo DB
1. Run the container with
```
docker compose --profile mongo up
```
2. Connect to the database using connection string below
```
mongodb://${MONGO_USERNAME}:${MONGO_PASSWORD}@localhost:27017/
```