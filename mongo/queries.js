//Get time statistics of bulkWrite operations
use("yelp");
db.getCollection("system.profile").aggregate([
    {
        $match: {
            op: "bulkWrite",
        },
    },
    {
        $group: {
            _id: "$ns",
            millis: { $sum: "$millis" },
            nInserted: { $sum: "$ninserted" },
            // nModified: { $sum: "$nModified" },
            //The total CPU time spent by a query operation in nanoseconds.
            //This field is only available on Linux systems.
            totalCpuNanos: { $sum: "$cpuNanos" },
            avgCpu: { $avg: "$cpuNanos" },
        },
    },
    {
        $project: {
            millis: 1,
            nInserted: 1,
            avgCpuSeconds: { $divide: ["$avgCpu", 1_000_000_000] },
            cpuSeconds: { $divide: ["$totalCpuNanos", 1_000_000_000] },
        },
    },
]);

//GET VOTE COUNT
db.getCollection("reviews").aggregate([
    {
        $match: {
            _id: "IULD0OBwjh3Eoc0IotK1rA", //: "update"
        },
    },

    {
        $project: {
            count: { $size: "$votes" },
        },
    },
]);

//get average stars rating of business
db.getCollection("reviews").aggregate([
    {
        $match: {
            business_id: "AOdw-xjhc-ZRVhJzAgJxSw",
        },
    },
    {
        $group: {
            _id: "$business_id",
            avgStars: { $avg: "$stars" },
            voteCount: { $sum: 1 },
        },
    },
]);

//get all reviews for specific business
db.getCollection("reviews").find(
    {
        business_id: "AOdw-xjhc-ZRVhJzAgJxSw",
    },
    {
        business_id: 0,
    }
);
