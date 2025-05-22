package michal.malek;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import michal.malek.models.json.UserJson;
import michal.malek.models.json.BusinessJson;
import michal.malek.models.json.ReviewJson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

public class InitService {
    private static final DateTimeFormatter DATE_FMT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .optionalStart()
            .appendPattern(" HH:mm:ss")
            .optionalEnd()
            .toFormatter();

    public static Connection getConnection(boolean isPostgres) throws SQLException {
        String url = isPostgres
                ? "jdbc:postgresql://localhost:5432/postgres"
                : "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=true;trustServerCertificate=true";
        String user = isPostgres ? "admin" : "sa";
        String pass = isPostgres ? "123" : "!Password123";

        return DriverManager.getConnection(url, user, pass);
    }

    public static void initDb(boolean isPostgres, int maxAmount) throws Exception {
        // 1. Configure connection URL and credentials
        String url = isPostgres
                ? "jdbc:postgresql://localhost:5432/postgres"
                : "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=true;trustServerCertificate=true";
        String user = isPostgres ? "admin" : "sa";
        String pass = isPostgres ? "123" : "!Password123";

        // 2. Define INSERT statements depending on the database engine
        final String sqlUser = isPostgres
                ? "INSERT INTO users(name, registration_date) VALUES(?,?) RETURNING user_id"
                : "INSERT INTO users(name, registration_date) OUTPUT inserted.user_id VALUES(?,?)";

        final String sqlBiz = isPostgres
                ? "INSERT INTO businesses(name,address,city,state,postal_code,latitude,longitude,categories) VALUES(?,?,?,?,?,?,?,?) RETURNING business_id"
                : "INSERT INTO businesses(name,address,city,state,postal_code,latitude,longitude,categories) OUTPUT inserted.business_id VALUES(?,?,?,?,?,?,?,?)";

        final String sqlRev = isPostgres
                ? "INSERT INTO reviews(user_id,business_id,rating,review_text,review_date) VALUES(?,?,?,?,?) RETURNING review_id"
                : "INSERT INTO reviews(user_id,business_id,rating,review_text,review_date) OUTPUT inserted.review_id VALUES(?,?,?,?,?)";

        // JSON mapper setup
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // ID maps
        Map<String,Integer> userIdMap = new HashMap<>();
        Map<String,Integer> businessIdMap = new HashMap<>();
        List<UserJson> usersList = new ArrayList<>();

        // 3. Establish connection
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            conn.setAutoCommit(false);
            //System.out.println("Connected to database");

            // 3.1 Seed vote_types
            try (Statement st = conn.createStatement()) {
                st.execute("INSERT INTO vote_types(name) VALUES('useful'),('funny'),('cool')");
            }

            // 3.2 Import Users
            // System.out.println("Import users");
            try (BufferedReader br = new BufferedReader(new FileReader("user.json"));
                 PreparedStatement psUser = conn.prepareStatement(sqlUser)) {
                String line;
                int count = 0;
                while ((line = br.readLine()) != null && count < maxAmount) {
                    count++;
                    UserJson uj = mapper.readValue(line, UserJson.class);
                    usersList.add(uj);

                    psUser.setString(1, uj.name);
                    LocalDate regDate = LocalDate.parse(uj.yelping_since, DATE_FMT);
                    psUser.setDate(2, Date.valueOf(regDate));

                    try (ResultSet rs = psUser.executeQuery()) {
                        rs.next();
                        userIdMap.put(uj.user_id, rs.getInt(1));
                    }
                }
            }

            // 3.3 Import Businesses and Hours
            // System.out.println("Import businesses + hours");
            try (BufferedReader br = new BufferedReader(new FileReader("business.json"));
                 PreparedStatement psBiz = conn.prepareStatement(sqlBiz);
                 PreparedStatement psHrs = conn.prepareStatement(
                         "INSERT INTO hours(business_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday) VALUES(?,?,?,?,?,?,?,?)")) {
                String line;
                int count = 0;
                while ((line = br.readLine()) != null && count < maxAmount) {
                    count++;
                    BusinessJson bj = mapper.readValue(line, BusinessJson.class);

                    // Business insert
                    psBiz.setString(1, bj.name);
                    psBiz.setString(2, bj.address);
                    psBiz.setString(3, bj.city);
                    psBiz.setString(4, bj.state);
                    psBiz.setString(5, bj.postal_code);
                    psBiz.setDouble(6, bj.latitude);
                    psBiz.setDouble(7, bj.longitude);
                    psBiz.setString(8, String.join(",", bj.categories));
                    try (ResultSet rs = psBiz.executeQuery()) {
                        rs.next();
                        int dbBizId = rs.getInt(1);
                        businessIdMap.put(bj.business_id, dbBizId);
                    }

                    // Hours insert
                    psHrs.setInt(1, businessIdMap.get(bj.business_id));
                    psHrs.setString(2, bj.hours != null ? bj.hours.get("Monday") : null);
                    psHrs.setString(3, bj.hours != null ? bj.hours.get("Tuesday") : null);
                    psHrs.setString(4, bj.hours != null ? bj.hours.get("Wednesday") : null);
                    psHrs.setString(5, bj.hours != null ? bj.hours.get("Thursday") : null);
                    psHrs.setString(6, bj.hours != null ? bj.hours.get("Friday") : null);
                    psHrs.setString(7, bj.hours != null ? bj.hours.get("Saturday") : null);
                    psHrs.setString(8, bj.hours != null ? bj.hours.get("Sunday") : null);
                    psHrs.executeUpdate();
                }
            }

            // 3.4 Import Reviews and Votes
            // System.out.println("Import Reviews + votes");
            Map<String,Integer> vtId = new HashMap<>();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT type_id,name FROM vote_types")) {
                while (rs.next()) {
                    vtId.put(rs.getString("name"), rs.getInt("type_id"));
                }
            }

            try (BufferedReader br = new BufferedReader(new FileReader("review.json"));
                 PreparedStatement psRev = conn.prepareStatement(sqlRev);
                 PreparedStatement psVote = conn.prepareStatement(
                         "INSERT INTO votes(type_id,user_id,review_id,vote_date) VALUES(?,?,?,?)")) {
                String line;
                int count = 0;
                while ((line = br.readLine()) != null && count < maxAmount) {
                    count++;
                    ReviewJson rj = mapper.readValue(line, ReviewJson.class);
                    Integer uId = userIdMap.get(rj.user_id);
                    Integer bId = businessIdMap.get(rj.business_id);
                    if (uId == null || bId == null) continue;

                    LocalDate reviewDate = rj.date.contains(" ")
                            ? LocalDateTime.parse(rj.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate()
                            : LocalDate.parse(rj.date, DATE_FMT);

                    String text = Optional.ofNullable(rj.text).orElse("");
                    if (text.length() > 300) text = text.substring(0, 300);

                    // Review insert
                    psRev.setInt(1, uId);
                    psRev.setInt(2, bId);
                    psRev.setBigDecimal(3, new java.math.BigDecimal(rj.stars));
                    psRev.setString(4, text);
                    psRev.setDate(5, Date.valueOf(reviewDate));
                    try (ResultSet rs2 = psRev.executeQuery()) {
                        rs2.next();
                        int revId = rs2.getInt(1);

                        // Votes batching
                        for (int i = 0; i < rj.useful; i++) {
                            psVote.setInt(1, vtId.get("useful"));
                            psVote.setInt(2, uId);
                            psVote.setInt(3, revId);
                            psVote.setDate(4, Date.valueOf(reviewDate));
                            psVote.addBatch();
                        }
                        for (int i = 0; i < rj.funny; i++) {
                            psVote.setInt(1, vtId.get("funny"));
                            psVote.setInt(2, uId);
                            psVote.setInt(3, revId);
                            psVote.setDate(4, Date.valueOf(reviewDate));
                            psVote.addBatch();
                        }
                        for (int i = 0; i < rj.cool; i++) {
                            psVote.setInt(1, vtId.get("cool"));
                            psVote.setInt(2, uId);
                            psVote.setInt(3, revId);
                            psVote.setDate(4, Date.valueOf(reviewDate));
                            psVote.addBatch();
                        }
                    }
                }
                psVote.executeBatch();
            }

            // 3.5 Import Friends
            // System.out.println("Import friends");
            String sqlFr = "INSERT INTO friends(user1_id,user2_id,confirmed) VALUES(?,?,?)";
            try (PreparedStatement psFr = conn.prepareStatement(sqlFr)) {
                for (UserJson uj : usersList) {
                    Integer u1 = userIdMap.get(uj.user_id);
                    if (u1 == null || uj.ids == null) continue;
                    for (String fId : uj.ids) {
                        Integer u2 = userIdMap.get(fId);
                        if (u2 != null && !u2.equals(u1)) {
                            psFr.setInt(1, u1);
                            psFr.setInt(2, u2);
                            psFr.setBoolean(3, true);
                            psFr.addBatch();
                        }
                    }
                }
                psFr.executeBatch();
            }

            // 4. Commit transaction
            conn.commit();
            // System.out.println("Import zakończony pomyślnie!");
        }
    }

}
