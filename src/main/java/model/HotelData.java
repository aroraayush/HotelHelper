package model;

import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * This represents the basic structure of Hotel Data. It also have some methods that does some operations
 */
public class HotelData {

    private Map<String,Hotel> hotelDataMap;
    private Map<String,Set<HotelReview>> hotelUserReviewMap;
    private Map<String,Set<HotelReview>> hotelAnonymousReviewMap;
    protected Map<String,Set<HotelReview>> hotelReviewTreeSetMap;

    /**
     * Default constructor for HotelData
     */
    public HotelData() {
        hotelDataMap = new HashMap<>();
        hotelUserReviewMap = new HashMap<>();
        hotelAnonymousReviewMap = new HashMap<>();
        hotelReviewTreeSetMap = new HashMap<>();
    }

    protected Map<String, Set<HotelReview>> getHotelUserReviewMap() {
        Map<String, Set<HotelReview>> unmodifiableMap = Collections.unmodifiableMap(hotelUserReviewMap);
        return unmodifiableMap;
    }

    protected Map<String, Set<HotelReview>> gethotelAnonymousReviewMap() {
        Map<String, Set<HotelReview>> unmodifiableMap = Collections.unmodifiableMap(hotelAnonymousReviewMap);
        return unmodifiableMap;
    }


    /**
     * Return the latitude of a specific hotel
     * @param hotelId Id of Hotel
     * @return latitude of a specific hotel
     */
    public double getHotelLat(String hotelId) {
        double lat = hotelDataMap.get(hotelId).getLat();
        return lat;
    }

    /**
     * Return the longitude of a specific hotel
     * @param hotelId Id of Hotel
     * @return longitude of a specific hotel
     */
    public double getHotelLon(String hotelId) {
        double lon = hotelDataMap.get(hotelId).getLon();
        return lon;
    }

    /**
     * Return the city of a specific hotel
     * @param hotelId Id of Hotel
     * @return City of a hotel
     */
    public String getHotelCity(String hotelId) {
        String city = hotelDataMap.get(hotelId).getCity();
        return city;
    }

    /**
     * Return the address of a specific hotel
     * @param hotelId Id of Hotel
     * @return Address of a hotel
     */
    public String getHotelAddress(String hotelId) {
        String address = hotelDataMap.get(hotelId).getAddress();
        return address;
    }

    /**
     * Return the name of a specific hotel
     * @param hotelId Id of Hotel
     * @return Name of a hotel
     */
    public String getHotelName(String hotelId) {
        String name = hotelDataMap.get(hotelId).getName();
        return name;
    }

    /**
     * Return the state of a specific hotel
     * @param hotelId Id of Hotel
     * @return State of a hotel
     */
    public String getHotelState(String hotelId) {
        String state = hotelDataMap.get(hotelId).getState();
        return state;
    }

    /**
     * Create a Hotel given the parameters, and add it to the appropriate data
     * structure(s).
     *
     * @param hotelId
     *            - the id of the hotel
     * @param hotelName
     *            - the name of the hotel
     * @param city
     *            - the city where the hotel is located
     * @param state
     *            - the state where the hotel is located.
     * @param streetAddress
     *            - the building number and the street
     * @param lat
     *            - Latitude of the Hotel
     * @param lon
     *            - Longitude of the Hotel
     */
    protected void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress,
                            double lat, double lon) {
        hotelDataMap.put(hotelId, new Hotel(hotelId,hotelName,city,state,streetAddress,lat,lon));
    }

    /**
     * Returns a string representing information about the hotel
     * @param hotelId The Id of a hotel
     * @return - output string. If no hotel exist, return ""
     */
    protected String toString(String hotelId){
        if(hotelDataMap.containsKey(hotelId)){
            Hotel hotel = hotelDataMap.get(hotelId);
            String hotelStr = hotel.toString();
            return hotelStr;
        }
        return "";
    }

    /**
     * Return an alphabetized list of the ids of all hotels
     *
     * @return A list of Strings that contains Hotel data
     */
    public List<String> getHotels(){
        List<String> hotelList = new ArrayList<>();
        if(hotelDataMap == null){
            System.out.println("Trying to fetch hotel data, but no data found");
        }
        else {
            hotelDataMap.forEach((hotelIdKey,hotelObj) ->{
                Hotel hotel = hotelDataMap.get(hotelIdKey);
                hotelList.add(hotel.getHotelId());
            });
        }
        return hotelList;
    }

    /**
     * Return an alphabetized list of the ids of all hotels
     *
     * @return A list of Strings that contains Hotel data
     */
    public Set<String> getUserReviewHotelKeys(){

        Set<String> hotelList = new HashSet<>();
        if(hotelUserReviewMap == null){
            System.out.println("Trying to fetch review map, but no data found");
        }
        else {
            hotelList = hotelUserReviewMap.keySet();
        }
        return hotelList;
    }

    /**
     * Return an alphabetized list of the ids of all hotels
     *
     * @return A list of Strings that contains Hotel data
     */
    public Set<String> getAnnReviewKeys(){

        Set<String> hotelList = new HashSet<>();
        if(hotelAnonymousReviewMap == null){
            System.out.println("Trying to fetch review map, but no data found");
        }
        else {
            hotelList = hotelAnonymousReviewMap.keySet();
        }
        return hotelList;
    }

    /**
     * Return an alphabetized list of the ids of all hotels
     *
     * @return A list of Strings that contains Hotel data
     */
    public Set<HotelReview> getUserReviews(String hotelId){

        Set<HotelReview> reviewList = new HashSet<>();
        if(hotelUserReviewMap == null){
            System.out.println("Trying to fetch review map, but no data found");
        }
        else {
            reviewList = hotelUserReviewMap.get(hotelId);
        }
        return reviewList;
    }

    public Set<HotelReview> getAnnUserReviews(String hotelId){

        Set<HotelReview> reviewList = new HashSet<>();
        if(hotelUserReviewMap == null){
            System.out.println("Trying to fetch review map, but no data found");
        }
        else {
            reviewList = hotelAnonymousReviewMap.get(hotelId);
        }
        return reviewList;
    }

    /**
     * This function parses and processes the reviews of hotel and adds them to the HashMap
     * @param reviewsPath  The path of the review json file
     * @return HotelData instance
     */
    protected HotelData parseReviews(Path reviewsPath){

        try (JsonReader reader = new JsonReader(new FileReader(reviewsPath.toString()))) {
            reader.beginObject();

            while (reader.hasNext()) {

                String keyName = reader.nextName();

                if (keyName.equals("reviewDetails")) {

                    reader.beginObject();

                    while (reader.hasNext()) {

                        String reviewDetailsKeyName = reader.nextName();

                        if (reviewDetailsKeyName.equals("reviewCollection")) {

                            reader.beginObject();

                            while (reader.hasNext()) {

                                String reviewCollectionKeyName = reader.nextName();

                                if (reviewCollectionKeyName.equals("review")) {

                                    reader.beginArray();

                                    while (reader.hasNext()) {

                                        String hotelId = "";
                                        int ratingOverall = 0;
                                        String userNickname = "";
                                        String title = "";
                                        String reviewSubmissionTime = "";
                                        String reviewId = "";
                                        String reviewText = "";
                                        String isRecommended = "";
                                        boolean isRecom = false;

                                        reader.beginObject();

                                        while (reader.hasNext()) {

                                            String reviewObjKeyName = reader.nextName();

                                            if (reviewObjKeyName.equals("hotelId")) {
                                                hotelId = reader.nextString();
                                            }
                                            else if (reviewObjKeyName.equals("ratingOverall")) {
                                                ratingOverall = reader.nextInt();
                                            }
                                            else if (reviewObjKeyName.equals("userNickname")) {
                                                userNickname = reader.nextString();
                                            }
                                            else if (reviewObjKeyName.equals("title")) {
                                                title = reader.nextString();
                                            }
                                            else if (reviewObjKeyName.equals("reviewText")) {
                                                reviewText = reader.nextString();
                                            }
                                            else if (reviewObjKeyName.equals("reviewSubmissionTime")) {
                                                reviewSubmissionTime = reader.nextString();
                                            }
                                            else if (reviewObjKeyName.equals("reviewId")) {
                                                reviewId = reader.nextString();
                                            }
                                            else if (reviewObjKeyName.equals("isRecommended")) {
                                                isRecommended = reader.nextString();
                                                isRecom = isRecommended.toUpperCase().equals("YES");

                                            } else {
                                                reader.skipValue();
                                            }
                                        }
                                        reader.endObject();
                                        this.addReview(hotelId,reviewId,ratingOverall, title, reviewText, isRecom, reviewSubmissionTime,userNickname);
                                    }
                                    reader.endArray();
                                }
                                else {
                                    reader.skipValue();
                                }
                            }

                            reader.endObject();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found at location:" + reviewsPath + " - " + e);
        } catch (IOException e) {
            System.out.println("IOException : " + e);
        }
        return this;
    }

    /**
     * Add review to different review treemaps in HotelData class object
     *
     * @param hotelId
     *            - the id of the hotel reviewed
     * @param reviewId
     *            - the id of the review
     * @param rating
     *            - integer rating 1-5.
     * @param reviewTitle
     *            - the title of the review
     * @param review
     *            - text of the review
     * @param isRecom
     *            - whether the user recommends it or not
     * @param date
     *            - date of the review
     * @param username
     *            - the nickname of the user writing the review.
     * @return true if successful, false if unsuccessful because of invalid date
     *         or rating. Needs to catch and handle the following exceptions:
     * ParseException if the date is invalid
     * InvalidRatingException if the rating is out of range
     */
    protected boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle, String review, boolean isRecom, String date, String username) {

        boolean isAddedReview = false;
        try {
            if(rating < 1 || rating > 5){
                System.out.println("InvalidRatingException : rating is out of range for review id : "+reviewId + " for hotelId "+ hotelId);
            }
            else{
                HotelReview hotelReview = new HotelReview(hotelId,reviewId,rating,reviewTitle,review,isRecom,date,username);

                if(username.trim().length() == 0){
                    if (hotelAnonymousReviewMap.containsKey(hotelId)) {
                        //TreeSet of Reviews already exists
                        Set<HotelReview> hotelReviewSet = hotelAnonymousReviewMap.get(hotelId);
                        hotelReviewSet.add(hotelReview);
                    } else {
                        // HashSet doesn't exist for key hotelId in hotelReviewHashSetMap
                        // creating a new key with new HashSet with current review
                        Set<HotelReview> hotelReviewSet = new HashSet<>();
                        hotelReviewSet.add(hotelReview);
                        hotelAnonymousReviewMap.put(hotelId, hotelReviewSet);
                    }
                }
                else {
                    if (hotelUserReviewMap.containsKey(hotelId)) {
                        //HashSet of Reviews already exists
                        Set<HotelReview> hotelReviewSet = hotelUserReviewMap.get(hotelId);
                        hotelReviewSet.add(hotelReview);
                    } else {
                        // HashSet doesn't exist for key hotelId in hotelReviewHashSetMap
                        // creating a new key with new HashSet with current review
                        Set<HotelReview> hotelReviewSet = new HashSet<>();
                        hotelReviewSet.add(hotelReview);
                        hotelUserReviewMap.put(hotelId, hotelReviewSet);
                    }
                }
                isAddedReview = true;
            }
        }
        catch (Exception e){
            System.err.println("Exception : : "+e);
        }

        return isAddedReview;
    }

    private class Hotel implements Comparable<Hotel> {

        private String hotelId; // id
        private String name; // f
        private String city; // ci
        private String state; // pr
        private String addr; // ad
        private double lat; // lat
        private double lon; // lng

        /**
         * Constructor of the Hotel class
         *
         * @param hotelId
         *            - the id of the hotel
         * @param name
         *            - the name of the hotel
         * @param city
         *            - the city where the hotel is located
         * @param state
         *            - the state where the hotel is located.
         * @param addr
         *            - the building number and the street
         * @param lat
         *            - Latitude of the Hotel
         * @param lon
         *            - Longitude of the Hotel
         */
        public Hotel(String hotelId, String name, String city, String state, String addr, double lat, double lon) {
            this.hotelId = hotelId;
            this.name = name;
            this.city = city;
            this.state = state;
            this.addr = addr;
            this.lat = lat;
            this.lon = lon;
        }

        /**
         * Return the current hotel's Id
         * @return Id of hotel
         */
        protected String getHotelId() {
            return hotelId;
        }

        /**
         * Return the current hotel's latitude
         * @return latitude of hotel
         */
        protected double getLat() {
            return lat;
        }

        /**
         * Return the current hotel's longitude
         * @return Longitude of hotel
         */
        protected double getLon() {
            return lon;
        }

        /**
         * Return the current hotel's city
         * @return City of hotel
         */
        protected String getCity() {
            return city;
        }

        /**
         * Return the current hotel's Name
         * @return Name of hotel
         */
        protected String getName() {
            return name;
        }

        /**
         * Return the current hotel's state
         * @return Name of hotel
         */
        protected String getState() {
            return state;
        }

        /**
         * Return the current hotel's streetAddress
         * @return Name of hotel
         */
        protected String getAddress() {
            return addr;
        }

        /**
         * Prints the hotel information
         * @return String containing hotelData
         */
        @Override
        public String toString(){
            return name + ": " +  hotelId + System.lineSeparator()
                    + addr+ System.lineSeparator()  +
                    city + ", " + state + System.lineSeparator();
        }

        /**
         * Compares this Hotel object to another object of type Hotel
         * @param hotel the object of type Hotel to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it
         *                              from being compared to this object.
         */
        @Override
        public int compareTo(Hotel hotel) {
            int result = 0;
            try {
                if(hotel == null){
                    System.out.println("Hotel object provided is null");
                }
                else{
                    result = hotelId.compareTo(hotel.getHotelId());
                }
            }
            catch (NullPointerException e){
                System.out.println("NullPointerException : " + e);
            }
            catch (ClassCastException e){
                System.out.println("ClassCastException : " + e);
            }
            catch (Exception e){
                System.err.println("Exception : : " + e);
            }
            return result;
        }
    }
}