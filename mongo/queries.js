// The current database to use.
use("yelp");

// Search for documents in the current collection.
db.getCollection("system.profile")
    .find(
        {
            op: "bulkWrite",
        },
        {
            millis: 1,
            ninserted: 1,
        }
    )
    .sort({
        /*
         * fieldA: 1 // ascending
         * fieldB: -1 // descending
         */
    });

use("yelp");
db.getCollection("system.profile").aggregate(
    [
        {
            $match: {
                op: "bulkWrite", //: "update"
            },
        },
        {
            $group: {
                _id: "$ns",
                millis: { $sum: "$millis" },
                ninserted: { $sum: "$ninserted" },
            },
        },
    ],
    {
        $project: {
            _id: 0,
            millis: "$millis",
            ninserted: "$ninserted",
        },
    }
);

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
