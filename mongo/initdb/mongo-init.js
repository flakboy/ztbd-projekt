const database = "yelp";
const collection = "businesses";

// Create a new database.
use(database);

// Create a new collection.
db.createCollection(collection);

// Set logging to "all"
db.setProfilingLevel(2);
