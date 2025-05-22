package michal.malek;

import michal.malek.models.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryService {

    /**
     * Pobiera liczbę recenzji dla danej firmy.
     */
    public static void getReviewCount(Connection conn, int businessId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reviews WHERE business_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, businessId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rs.getInt(1);
                }
            }
        }
    }

    /**
     * Pobiera średnią ocen recenzji dla danej firmy.
     */
    public static void getAverageRating(Connection conn, int businessId) throws SQLException {
        String sql = "SELECT AVG(rating) FROM reviews WHERE business_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, businessId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rs.getDouble(1);
                }
            }
        }
    }

    /**
     * Zwraca wszystkie recenzje (ze szczegółami użytkownika) dla danej firmy.
     */
    public static void getAllReviews(Connection conn, int businessId) throws SQLException {
        String sql = "SELECT r.review_id, r.user_id, u.name AS user_name, "
                + "r.rating, r.review_text, r.review_date "
                + "FROM reviews r JOIN users u ON r.user_id = u.user_id "
                + "WHERE r.business_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, businessId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Review> reviews = new ArrayList<>();
                while (rs.next()) {
                    reviews.add(new Review(
                            rs.getInt("review_id"),
                            rs.getInt("user_id"),
                            rs.getString("user_name"),
                            rs.getDouble("rating"),
                            rs.getString("review_text"),
                            rs.getDate("review_date")
                    ));
                }
            }
        }
    }

    /**
     * Sprawdza, czy istnieje typ głosu o danym ID w tabeli vote_types.
     */
    public static boolean voteTypeExists(Connection conn, int typeId) throws SQLException {
        String sql = "SELECT 1 FROM vote_types WHERE type_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Dodanie nowego głosu do recenzji.
     */
    public static void insertRecord(Connection conn, int typeId, int userId, int reviewId, int runs) throws SQLException {
        if (!voteTypeExists(conn, typeId)) {
            throw new IllegalArgumentException("Vote type ID " + typeId + " does not exist.");
        }
        String sql = "INSERT INTO votes(type_id, user_id, review_id, vote_date) VALUES (?, ?, ?, ?)";
        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            for (int i = 0; i < runs; i++) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, typeId);
                    ps.setInt(2, userId);
                    ps.setInt(3, reviewId);
                    ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                    ps.executeUpdate();
                }
                conn.rollback();
            }
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }
}
