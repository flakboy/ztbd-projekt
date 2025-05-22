package michal.malek.models;

import java.sql.Date;

public record Review(int reviewId, int userId, String userName, double rating, String text, Date date) {
}