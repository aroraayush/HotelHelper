package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Blueprint of a hotel review
 */
public class HotelReview {
    private String hotelId;
    private String reviewId;
    private int ratingOverall;
    private String title;
    private String reviewText;
    private boolean isRecommended;
    private String reviewSubmissionTime;
    private String user;

    /**
     * Constructor of HotelReview Class
     *
     * @param hotelId
     *            - the id of the hotel reviewed
     * @param reviewId
     *            - the id of the review
     * @param ratingOverall
     *            - integer rating 1-5.
     * @param title
     *            - the title of the review
     * @param reviewText
     *            - text of the review
     * @param isRecommended
     *            - whether the user recommends it or not
     * @param reviewSubmissionTime
     *            - date of the review
     * @param user
     *            - the nickname of the user writing the review.
     */
    public HotelReview(String hotelId, String reviewId, int ratingOverall, String title, String reviewText, boolean isRecommended, String reviewSubmissionTime, String user) {
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.ratingOverall = ratingOverall;
        this.title = title;
        this.reviewText = reviewText;
        this.isRecommended = isRecommended;
        this.reviewSubmissionTime = reviewSubmissionTime;
        this.user = user;
    }

    /**
     * This returns the rating of review
     * @return ratingOverall
     */
    public int getRatingOverall() {
        return ratingOverall;
    }

    /**
     * This returns the title of review
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * This returns the text of review
     * @return reviewText
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * This returns the where hotel is recommended by a user
     * @return isRecommended
     */
    public boolean isRecommended() {
        return isRecommended;
    }

    /**
     * This returns the hotelId of a particular HotelReview Object
     * @return hotelId  Id of hotel
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * This returns the reviewId of a particular HotelReview Object
     * @return reviewId  Id of hotel
     */
    public String getReviewId() {
        return reviewId;
    }

    /**
     * This returns the user name of a person who have the review
     * @return user {String} user name of a person who have the review
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns the submission datetime
     * @return reviewSubmissionTime submission datetime
     */
    public String getReviewSubmissionTime() {
        return reviewSubmissionTime;
    }

    /**
     * Prints the hotel review information
     * @return String containing hotelData
     */
    @Override
    public String toString() {
        Date date = new Date();
        SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdfo.parse(reviewSubmissionTime.substring(0,10) + " " + reviewSubmissionTime.substring(11,19));
        } catch (ParseException e) {
            System.out.println("ParseException :  "+ e);
        }
        return "--------------------" + System.lineSeparator() + "Review by " + user + " on " + date.toString()  + System.lineSeparator() +
                "Rating: "+ ratingOverall + System.lineSeparator() + title + System.lineSeparator() +
                reviewText + System.lineSeparator() ;
    }
}
