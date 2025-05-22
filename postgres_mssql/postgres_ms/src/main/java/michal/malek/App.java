package michal.malek;


import org.apache.commons.lang3.time.StopWatch;

public class App {



    public static void main(String[] args) throws Exception {
        int maxAmount = 200_000;
        msSql(maxAmount);
        System.out.println();
        postgres(maxAmount);
    }

    private static void msSql(int maxAmount) throws Exception {
        StopWatch stopWatch = new StopWatch();
        long elapsedMs = 0;
        stopWatch.reset();
        stopWatch.start();
        InitService.initDb(false, maxAmount);
        stopWatch.stop();
        elapsedMs = stopWatch.getTime();
        System.out.println("MsSql [Insert Records - Max amount per table: " + maxAmount + " ] -> Czas wykonania: " + elapsedMs + " ms");

        stopWatch.reset();
        stopWatch.start();
        QueryService.getAllReviews(InitService.getConnection(false), 1);
        stopWatch.stop();
        elapsedMs = stopWatch.getTime();
        System.out.println("MsSql [All Reviews] -> Czas wykonania: " + elapsedMs + " ms");

        stopWatch.reset();
        stopWatch.start();
        QueryService.getAverageRating(InitService.getConnection(false), 1);
        stopWatch.stop();
        elapsedMs = stopWatch.getTime();
        System.out.println("MsSql [Get Average Rating] -> Czas wykonania: " + elapsedMs + " ms");

        stopWatch.reset();
        stopWatch.start();
        QueryService.getReviewCount(InitService.getConnection(false), 1);
        stopWatch.stop();
        elapsedMs = stopWatch.getTime();
        System.out.println("MsSql [Review Cunt] -> Czas wykonania: " + elapsedMs + " ms");

        stopWatch.reset();
        stopWatch.start();
        QueryService.insertRecord(InitService.getConnection(false), 1, 1, 1, 1);
        stopWatch.stop();
        elapsedMs = stopWatch.getTime();
        System.out.println("MsSql [Insert Record] -> Czas wykonania: " + elapsedMs + " ms");
    }

    private static void postgres(int maxAmount) throws Exception {
        StopWatch stopWatch = new StopWatch();
        long elapsedMs = 0;
        stopWatch.reset();
        stopWatch.start();
        InitService.initDb(true, maxAmount);
        stopWatch.stop();
        elapsedMs = stopWatch.getTime();
        System.out.println("Postgres [Insert Records - Max amount per table: " + maxAmount + " ] -> Czas wykonania: " + elapsedMs + " ms");

        stopWatch.reset();
        stopWatch.start();
        QueryService.getAllReviews(InitService.getConnection(true), 1);
        stopWatch.stop();
        elapsedMs = stopWatch.getTime();
        System.out.println("Postgres [All Reviews] -> Czas wykonania: " + elapsedMs + " ms");

        stopWatch.reset();
        stopWatch.start();
        QueryService.getAverageRating(InitService.getConnection(true), 1);
        stopWatch.stop();
        elapsedMs = stopWatch.getTime();
        System.out.println("Postgres [All Reviews] -> Czas wykonania: " + elapsedMs + " ms");

        stopWatch.reset();
        stopWatch.start();
        QueryService.getReviewCount(InitService.getConnection(true), 1);
        stopWatch.stop();
        elapsedMs = stopWatch.getTime();
        System.out.println("Postgres [Review Cunt] -> Czas wykonania: " + elapsedMs + " ms");

        stopWatch.reset();
        stopWatch.start();
        QueryService.insertRecord(InitService.getConnection(true), 1, 1, 1, 1);
        stopWatch.stop();
        elapsedMs = stopWatch.getTime();
        System.out.println("Postgres [Insert Record: " + maxAmount + "] -> Czas wykonania: " + elapsedMs + " ms");
    }
}
