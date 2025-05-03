const database = "yelp";
const collection = "businesses";

// Create a new database.
use(database);

// Create a new collection.
db.createCollection(collection);

//create "system.profile" collection for profiling
//which isn't capped to 1MB (1024 * 1024), but 1GB (1024 ^ 3)
db.setProfilingLevel(0);
db.system.profile.drop();
db.createCollection("system.profile", {
    capped: true,
    size: 10 * 1024 * 1024 * 1024,
});

// Set logging to "all"
db.setProfilingLevel(2);
