package utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.HotelData;
import model.HotelReview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles all database-related actions. Uses singleton design pattern.
 */
public class DatabaseHandler {

    /**
     * Used to determine if necessary tables are provided.
     */
    private static final String TABLES_SQL =
            "SHOW TABLES LIKE 'users';";
    /**
     * Used to create necessary tables for this example.
     */
    private static final String CREATE_SQL_USR =
            "CREATE TABLE `users` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `username` varchar(125) NOT NULL,\n" +
                    "  `password` varchar(255) DEFAULT NULL,\n" +
                    "  `usersalt` char(64) DEFAULT NULL,\n" +
                    "  `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "  `last_login_time` varchar(25) DEFAULT NULL,\n" +
                    "  `current_login_time` varchar(25) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`),\n" +
                    "  KEY `ix_user_created_on` (`created_on`),\n" +
                    "  KEY `ix_user_id` (`id`)\n" +
                    ") ENGINE=InnoDB;";
    /**
     * Used to create necessary tables for this example.
     */
    private static final String CREATE_SQL_HOTEL = "CREATE TABLE `hotels` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `name` varchar(245) DEFAULT NULL,\n" +
            "  `address` text,\n" +
            "  `city` varchar(145) DEFAULT NULL,\n" +
            "  `state` varchar(45) DEFAULT NULL,\n" +
            "  `latitude` varchar(45) DEFAULT NULL,\n" +
            "  `longitude` varchar(45) DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ") ENGINE=InnoDB;";
    /**
    /**
     * Used to create necessary tables for this example.
     */
    private static final String CREATE_SQL_HOTEL_WISHLIST = "CREATE TABLE `hotel_wishlist` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `hotel_id` int(11) NOT NULL,\n" +
            "  `user_id` int(11) NOT NULL,\n" +
            "  `status` tinyint(1) NOT NULL,\n" +
            "  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `uq_hotel_id_user_id` (`hotel_id`,`user_id`),\n" +
            "  KEY `fk_hotel_id_idx` (`hotel_id`)\n" +
            ") ENGINE=InnoDB ;";
    /**
     * Used to create necessary tables for this example.
     */
    private static final String CREATE_SQL_REVIEW_LIKES = "CREATE TABLE `review_likes` (\n" +
            "  `user_id` int(11) NOT NULL,\n" +
            "  `review_id` int(11) NOT NULL,\n" +
            "  `status` tinyint(1) NOT NULL DEFAULT '1',\n" +
            "  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "  `hotel_id` int(11) NOT NULL,\n" +
            "  PRIMARY KEY (`user_id`,`review_id`),\n" +
            "  UNIQUE KEY `uq_review_id_user_id_hotel_id` (`user_id`,`review_id`,`hotel_id`)\n" +
            ") ENGINE=InnoDB;";
    /**
     * Used to create necessary tables for this example.
     */
    private static final String CREATE_VISITED_LINKS = "CREATE TABLE `visited_links` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `user_id` int(11) NOT NULL,\n" +
            "  `link` varchar(500) NOT NULL,\n" +
            "  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "  `hotel_id` int(11) DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ") ENGINE=InnoDB ;";
    /**
    /**
     * Used to create necessary tables for this example.
     */
    private static final String CREATE_SQL_REVIEWS = "CREATE TABLE `reviews` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `hotel_id` int(11) NOT NULL,\n" +
            "  `user_id` int(11) NOT NULL,\n" +
            "  `rating` int(11) DEFAULT NULL,\n" +
            "  `title` varchar(299) DEFAULT NULL,\n" +
            "  `review_text` text,\n" +
            "  `review_submission_time` varchar(45) DEFAULT NULL,\n" +
            "  `is_recommended` tinyint(1) DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `uq_user_id_hotel_id` (`hotel_id`,`user_id`)\n" +
            ") ENGINE=InnoDB ;";
    /**
     * Used to insert a new user into the database.
     */
    private static final String REGISTER_SQL =
            "INSERT INTO users (username, password, usersalt) VALUES (?, ?, ?);";
    /**
     * Used to insert a new user into the database.
     */
    private static final String ADD_REVIEW_SQL =
            "INSERT INTO reviews(hotel_id,user_id,rating,title,review_text,is_recommended,review_submission_time) VALUES (?,?,?,?,?,?,?) ;";

    /**
     * Used to insert a new user into the database.
     */
    private static final String UPDATE_REVIEW_SQL =
            "UPDATE reviews SET rating =? , title =? , review_text =? , is_recommended =? , review_submission_time =?  WHERE user_id = ? and id = ?;";
    /**
     * Used to insert a new user into the database.
     */
    private static final String DELETE_REVIEW_SQL =
            "DELETE FROM reviews WHERE user_id = ? and id = ?;";
    /**
     * Used to insert a new hotel into the database.
     */
    private static final String ADD_HOTEL_SQL =
            "INSERT INTO hotels (id,name,address,city,state,latitude,longitude) VALUES (?, ?, ?, ?, ?, ?, ?);" ;
    /**
     * Used to insert a new user into the database.
     */
    private static final String ADD_REVIEWS_USER_SQL =
            "INSERT INTO reviews (hotel_id,user_id,rating,title,review_text,review_submission_time,is_recommended) VALUES(?,?,?,?,?,?,?);" ;
    /**
     * Used to insert a new user into the database.
     */
    private static final String ADD_REVIEWS_ANN_USER_SQL =
            "INSERT INTO reviews (id,name,address,city,state,latitude,longitude) VALUES (?, ?, ?, ?, ?, ?, ?);" ;
    /**
     * Used to determine if a username already exists.
     */
    private static final String USERNAME_SQL =
            "SELECT username FROM users WHERE username = ?;";
    /**
     * Insert or update hotel likes
     */
    private static final String HOTEL_WISHLIST_SQL =
            "INSERT INTO hotel_wishlist (user_id, hotel_id, status) VALUES (? , ?, ?) ON DUPLICATE KEY UPDATE status = ? ;";

    /**
     * Insert or update hotel likes
     */
    private static final String VISITED_LINK_SQL =
            "INSERT INTO visited_links (user_id, link, hotel_id) VALUES (? , ?, ?);";
    /**
     * Delete hotel likes
     */
    private static final String DELETE_VISITED_LINK_SQL =
            "DELETE from visited_links  where user_id = ?;";

    /**
     * Insert or update hotel likes
     */
    private static final String REVIEW_LIKES_SQL =
            "INSERT INTO review_likes (user_id, review_id, hotel_id, status) VALUES (? , ?,?, ?) ON DUPLICATE KEY UPDATE status = ? ;";

    /**
     * Used to determine if a username already exists.
     */
    private static final String USER_SQL =
            "SELECT * FROM users WHERE username = ?;";
    /**
     * Used to retrieve the salt associated with a specific user.
     */
    private static final String SALT_SQL =
            "SELECT usersalt FROM users WHERE username = ?;";
    /**
     * Used to all cities where hotels are located.
     */
    private static final String CITIES_SQL = "SELECT distinct city FROM hotels;";

    /**
     * Used to all cities where hotels are located.
     */
    private static final String USR_HOTEL_WISH_LIST_SQL = "SELECT ht.name, ht.id hotel_id, wl.date FROM hotel_wishlist wl," +
            "hotels ht where wl.user_id = ? and wl.hotel_id = ht.id and wl.status = 1 AND wl.hotel_id = ?;";

    /**
     * Used to all cities where hotels are located.
     */
    private static final String USR_WISH_LIST_SQL = "SELECT ht.name, ht.id hotel_id, wl.date FROM hotel_wishlist wl, " +
            "hotels ht where wl.user_id = ? and wl.hotel_id = ht.id and wl.status = 1;";

    /**
     * Used to all cities where hotels are located.
     */
    private static final String USR_VISITED_LINKS_SQL = "SELECT vl.link, vl.date, ht.name FROM visited_links vl, hotels ht where vl.hotel_id = ht.id and vl.user_id = ?;";
    /**
     * Used to all cities where hotels are located.
     */
    private static final String USR_REVIEW_LIKES_SQL = "SELECT review_id, count(*) count FROM review_likes where hotel_id = ? and status =1 and user_id != ? group by review_id;";

    /**
     * Used to get users likes
     */
    private static final String USR_LIKES_SQL = "SELECT review_id,status FROM review_likes where hotel_id = ? and user_id = ? ;";

    /**
     * Used to all cities where hotels are located.
     */
    private static final String HOTELS_SQL = "SELECT ht.city, ht.state, ht.id, ht.name, ht.address, ht.latitude, ht.longitude,meta.review_count, meta.avg_rating FROM hotels ht,\n" +
            "( SELECT hotel_id, count(hotel_id) review_count ,avg(rating) avg_rating FROM reviews rw group by hotel_id order by count(hotel_id) ) meta\n" +
            "where meta.hotel_id = ht.id and ht.name like ? and ht.city LIKE ? order by ht.name;";

    /**
     * Used to all cities where hotels are located.
     */
    private static final String HOTELS_DESC_SQL = "SELECT ht.city, ht.state, ht.id, ht.name, ht.address, ht.latitude, ht.longitude,meta.review_count, meta.avg_rating FROM hotels ht,\n" +
            "( SELECT hotel_id, count(hotel_id) review_count ,avg(rating) avg_rating FROM reviews rw group by hotel_id order by count(hotel_id) ) meta\n" +
            "where meta.hotel_id = ht.id and ht.name like ? and ht.city LIKE ? order by ht.name desc;";

    /**
     * Used to all cities where hotels are located.
     */
    private static final String HOTEL_SQL = "SELECT * FROM hotels where id = ? ;";


    /**
     * Used to all cities where hotels are located.
     */
    private static final String HOTELS_MAP_SQL = "SELECT name, state, city, latitude, longitude FROM hotels ;";


    /**
     * Used to all cities where hotels are located.
     */
    private static final String HOTELS_REVIEWS_SQL = "SELECT usr.username,\n" +
            "                rev.id review_id,\n" +
            "                rev.rating,\n" +
            "                rev.title,\n" +
            "                rev.is_recommended,\n" +
            "                rev.review_text,\n" +
            "                rev.review_submission_time\n" +
            "        FROM   hotels ht,\n" +
            "                reviews rev,\n" +
            "                users usr\n" +
            "        WHERE  ht.id = rev.hotel_id\n" +
            "        AND usr.id = rev.user_id\n" +
            "        AND ht.id = ?\n" +
            "        ORDER  BY rev.review_submission_time DESC;";



    /**
     * Used to all cities where hotels are located.
     */
    private static final String HOTELS_REVIEW_SQL =
            "SELECT id review_id FROM reviews WHERE  user_id = ?  AND hotel_id = ? ;";

    /**
     * Used to authenticate a user.
     */
    private static final String AUTH_SQL =
            "SELECT username FROM users " +
                    "WHERE username = ? AND password = ? ;";

    /**
     * Used to update login time.
     */
    private static final String UPDATE_LOGIN_TIME_SQL =
            "UPDATE users " +
                    "SET last_login_time = ? , current_login_time = ? ;";
    /**
     * Used to remove a user from the database.
     */
    private static final String DELETE_SQL =
            "DELETE FROM users WHERE username = ?";
    private static Logger log = LogManager.getLogger();
    /**
     * Makes sure only one database handler is instantiated.
     */
    private static DatabaseHandler singleton = new DatabaseHandler();
    /**
     * Used to configure connection to database.
     */
    private DatabaseConnector db;

    /**
     * Used to generate password hash salt for user.
     */
    private Random random;

    /**
     * Initializes a database handler. Private constructor
     * Forces all other classes to use singleton.
     */
    private DatabaseHandler() {
        Status status = Status.OK;
        random = new Random(System.currentTimeMillis());

        try {
            db = new DatabaseConnector("database.properties");
            status = db.testConnection() ? Status.OK : setupTables();
        } catch (FileNotFoundException e) {
            status = Status.MISSING_CONFIG;
        } catch (IOException e) {
            status = Status.MISSING_VALUES;
        }

        if (status != Status.OK) {
            log.fatal(status.message());
        }
    }

    /**
     * Gets the single instance of the database handler.
     * Notice: singleton instance variable and this method are static
     *
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
        return singleton;
    }

    /**
     * Checks to see if a String is null or empty.
     *
     * @param text - String to check
     * @return true if non-null and non-empty
     */
    private static boolean isBlank(String text) {
        return (text == null) || text.trim().isEmpty();
    }

    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes  - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    public static String encodeHex(byte[] bytes, int length) {
        // The java.lang.Math.signum() returns the Sign function of a value passed to it as argument.
        // bytes are in endian binary representation of the magnitude of the number.
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);
        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt     - salt associated with user
     * @return hashed password
     */
    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        } catch (Exception ex) {
            log.debug("Unable to properly hash password.", ex);
        }
        return hashed;
    }

    /**
     * Checks if necessary table exists in database, and if not tries to
     * create it.
     */
    private Status setupTables() {
        Status status = Status.ERROR;

        try (Connection connection = db.getConnection(); Statement stmt = connection.createStatement()) {
            if (!stmt.executeQuery(TABLES_SQL).next()) {
                // Table missing, must create
                System.out.println("Creating tables...");
                log.debug("Creating tables...");
                stmt.execute(CREATE_SQL_HOTEL);
                stmt.execute(CREATE_SQL_USR);
                stmt.execute(CREATE_SQL_REVIEWS);
                stmt.execute(CREATE_SQL_HOTEL_WISHLIST);
                stmt.execute(CREATE_SQL_REVIEW_LIKES);
                stmt.execute(CREATE_VISITED_LINKS);

                // Check if create was successful
                if (!stmt.executeQuery(TABLES_SQL).next()) {
                    status = Status.CREATE_FAILED;
                } else {
                    status = Status.OK;
                }
            } else {
                log.debug("Table already exists.");
                status = Status.OK;
            }
        } catch (Exception ex) {
            status = Status.CREATE_FAILED;
            log.debug(status, ex);
        }

        return status;
    }

    /**
     * Tests if a user already exists in the database. Requires an active
     * database connection.
     *
     * @param connection - active database connection
     * @param username   - username to check
     * @return Status.OK if user does not exist in database
     * @throws SQLException if unable to establish database connection
     */
    private Status checkIfUserExists(Connection connection, String username) {

        assert connection != null;
        assert username != null;

        Status status = Status.ERROR;

        try (
                PreparedStatement statement = connection.prepareStatement(USERNAME_SQL)
        ) {
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            status = results.next() ? Status.DUPLICATE_USER : Status.OK;
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
            status = Status.SQL_EXCEPTION;
        }
        return status;
    }

    private Status addToWishList(Connection connection, int updateStatus, int hotelId, int userId) {
        assert connection != null;
        Status status = Status.ERROR;

        try (
                PreparedStatement statement = connection.prepareStatement(HOTEL_WISHLIST_SQL)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, hotelId);
            statement.setInt(3, updateStatus);
            statement.setInt(4, updateStatus);
            int affectedRows = statement.executeUpdate();
            status = Status.OK;
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
            status = Status.SQL_EXCEPTION;
        }
        return status;
    }

    private Status addToVisitingList(Connection connection, String link, int hotelId, int userId) {
        assert connection != null;
        Status status = Status.ERROR;
        try (
                PreparedStatement statement = connection.prepareStatement(VISITED_LINK_SQL)
        ) {
            statement.setInt(1, userId);
            statement.setString(2, link);
            statement.setInt(3, hotelId);
            statement.execute();
            status = Status.OK;
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
            status = Status.SQL_EXCEPTION;
        }
        return status;
    }

    private Status deleteVisitingLinks(Connection connection, int userId) {
        assert connection != null;
        Status status = Status.ERROR;
        try (
                PreparedStatement statement = connection.prepareStatement(DELETE_VISITED_LINK_SQL)
        ) {
            statement.setInt(1, userId);
            statement.execute();
            status = Status.OK;
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
            status = Status.SQL_EXCEPTION;
        }
        return status;
    }

    private Status addToLikesList(Connection connection, int updateStatus, int reviewId, int userId, String hotelId) {
        assert connection != null;
        Status status = Status.ERROR;

        try (
                PreparedStatement statement = connection.prepareStatement(REVIEW_LIKES_SQL)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, reviewId);
            statement.setString(3, hotelId);
            statement.setInt(4, updateStatus);
            statement.setInt(5, updateStatus);
            int affectedRows = statement.executeUpdate();
            status = Status.OK;
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
            status = Status.SQL_EXCEPTION;
        }
        return status;
    }

    /**
     * Tests if a user already exists in the database.
     *
     * @param user - username to check
     * @return Status.OK if user does not exist in database
     * @see #checkIfUserExists(Connection, String)
     */
    public Status checkIfUserExists(String user) {
        Status status = Status.ERROR;

        try (
                Connection connection = db.getConnection()
        ) {
            status = checkIfUserExists(connection, user);
        } catch (SQLException e) {
            status = Status.CONNECTION_FAILED;
            log.debug(e.getMessage(), e);
        }

        return status;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database if the username does not already exist.
     *
     * @param username - username of new user
     * @param password - password of new user
     * @return status ok if registration successful
     */
    private Status registerUser(Connection connection, String username, String password) {

        assert connection != null;
        assert username != null;
        assert password != null;

        Status status = Status.ERROR;
        String[] pwdAndSalt = generateHashedPwdAndSalt(password);
        try (
                PreparedStatement statement = connection.prepareStatement(REGISTER_SQL)
        ) {
            statement.setString(1, username);
            statement.setString(2, pwdAndSalt[1]);
            statement.setString(3, pwdAndSalt[0]);
            statement.executeUpdate();
            status = Status.OK;

        } catch (SQLException ex) {
            status = Status.SQL_EXCEPTION;
            log.debug(ex.getMessage(), ex);
        }
        return status;
    }

    private String[] generateHashedPwdAndSalt(String password) {

        byte[] saltBytes = new byte[16];

        // Generates random bytes and places them into a user-supplied
        // byte array.  The number of random bytes produced is equal to
        // the length of the byte array.
        random.nextBytes(saltBytes);

        String userSalt = encodeHex(saltBytes, 32);
        String hashedPwd = getHash(password, userSalt);
        String pwdAndSalt[] = new String[] { userSalt, hashedPwd };
        return pwdAndSalt;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database if the username does not already exist.
     *
     * @param username - username of new user
     * @param newpass - password of new user
     * @return status.ok if registration successful
     */
    public Status registerUser(String username, String newpass) {
        Status status = Status.ERROR;
        log.debug("Registering " + username + ".");

        // make sure we have non-null and non-empty values for login
        if (isBlank(username) || isBlank(newpass)) {
            status = Status.INVALID_LOGIN;
            log.debug(status);
            return status;
        }

        try (Connection connection = db.getConnection();) {
            status = checkIfUserExists(connection, username);

            // if okay so far, try to insert new user
            if (status == Status.OK) {
                status = registerUser(connection, username, newpass);
            }
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return status;
    }

    /**
     * This method updates the like unlike status of a hotel
     * @param updateStatus the new like/dislike status
     * @param reviewId Id of the review
     * @param userId User which liked/disliked the review
     * @param hotelId Id of the hotel
     * @return Status.OK if the like/unlike status updated successfully
     */
    public Status updateLikeStatus(int updateStatus, int reviewId, int userId, String hotelId) {
        Status status = Status.ERROR;
        log.debug("Updating wishlist for userId " + userId + ".");

        try (Connection connection = db.getConnection();) {
            status = addToLikesList(connection, updateStatus, reviewId, userId, hotelId);

        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return status;
    }

    /**
     * Insert hotels into the user's wishlist
     * @param updateStatus Status whether the hotel is added to wishlist or not
     * @param hotelId  Id of the hotel
     * @param userId Id of user
     * @return Status.OK if hotel added/removed successfully from the wishlist
     * @throws SQLException if unable to establish database connection
     */
    public Status insertUpdateWishList(int updateStatus, int hotelId, int userId) {
        Status status = Status.ERROR;
        log.debug("Updating wishlist for userId " + userId + ".");

        try (Connection connection = db.getConnection();) {
            status = addToWishList(connection, updateStatus, hotelId, userId);

        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return status;
    }

    /**
     * This added the user's visited expedia links to db
     * @param link hotel expedia link
     * @param hotelId Id of hotel
     * @param userId Id of user
     * @return Status.OK if visited links added successfully
     * @throws SQLException if unable to establish database connection
     */
    public Status insertVisitedLinks(String link, int hotelId, int userId) {
        Status status = Status.ERROR;
        log.debug("Adding visited links for userId " + userId + ".");

        try (Connection connection = db.getConnection();) {
            status = addToVisitingList(connection, link, hotelId, userId);

        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return status;
    }

    /**
     * This removes all the visited links of a user
     * @param userId id of user
     * @return Status.OK if all visited links of a user deleted successfully
     * @throws SQLException if unable to establish database connection
     */
    public Status deleteVisitedLinks(int userId) {
        Status status = Status.ERROR;
        log.debug("Clearing visited links for userId " + userId + ".");

        try (Connection connection = db.getConnection();) {
            status = deleteVisitingLinks(connection, userId);

        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return status;
    }

    /**
     * Gets the salt for a specific user.
     *
     * @param connection - active database connection
     * @param username   - which user to retrieve salt for
     * @return salt for the specified user or null if user does not exist
     * @throws SQLException if any issues with database connection
     */
    private String getSalt(Connection connection, String username) throws SQLException {

        assert connection != null;
        assert username != null;

        String salt = null;

        try (
                PreparedStatement statement = connection.prepareStatement(SALT_SQL)
        ) {
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("usersalt");
            }
        }
        return salt;
    }

    /**
     * Checks if the provided username and password match what is stored
     * in the database. Requires an active database connection.
     *
     * @param username - username to authenticate
     * @param password - password to authenticate
     * @return status.ok if authentication successful
     * @throws SQLException if unable to establish database connection
     */
    private Status authenticateUser(Connection connection, String username,
                                    String password) throws SQLException {
        assert connection != null;

        Status status = Status.ERROR;
        try (
                PreparedStatement statement = connection.prepareStatement(AUTH_SQL)
        ) {
            String userSalt = getSalt(connection, username);
            String hashedPwd = getHash(password, userSalt);

            statement.setString(1, username);
            statement.setString(2, hashedPwd);

            ResultSet results = statement.executeQuery();
            status = results.next() ? Status.OK : Status.INVALID_LOGIN;
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
            status = Status.SQL_EXCEPTION;
        }
        return status;
    }

    private Status updateLoginTimeInDB(Connection connection,
                                       String lastLoginTime, String currentLoginTime) throws SQLException {
        assert connection != null;

        Status status = Status.ERROR;
        try (
                PreparedStatement statement = connection.prepareStatement(UPDATE_LOGIN_TIME_SQL)
        ) {
            if(lastLoginTime == null){
                statement.setString(1, currentLoginTime);
            }
            else{
                statement.setString(1, lastLoginTime);
            }
            statement.setString(2, currentLoginTime);

            statement.executeUpdate();
            status = Status.OK;
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
            status = Status.SQL_EXCEPTION;
        }
        return status;
    }

    /**
     * Loads hotels' data into database from HotelData
     *
     * @return status.ok if all data loaded successfully
     * @throws SQLException if unable to establish database connection
     */
    public Status addHotelDataToDB(HotelData hdata) throws SQLException {
        Status status = Status.ERROR;

        try (Connection connection = db.getConnection();) {
            final int batchSize = 25;
            AtomicInteger count = new AtomicInteger();
            List<String> hotels = hdata.getHotels();
            AtomicReference<AtomicIntegerArray> recordsAffected = new AtomicReference<>(new AtomicIntegerArray(new int[hotels.size()]));
            try (
                    PreparedStatement statement = connection.prepareStatement(ADD_HOTEL_SQL)
            ) {
                hotels.forEach(hotelId->{
                    try {
                        statement.setString(1, hotelId);
                        statement.setString(2, hdata.getHotelName(hotelId));
                        statement.setString(3, hdata.getHotelAddress(hotelId));
                        statement.setString(4, hdata.getHotelCity(hotelId));
                        statement.setString(5, hdata.getHotelState(hotelId));
                        statement.setDouble(6, hdata.getHotelLat(hotelId));
                        statement.setDouble(7, hdata.getHotelLon(hotelId));
                        statement.addBatch();

                        if(count.incrementAndGet() % batchSize == 0) {
                            recordsAffected.set(new AtomicIntegerArray(statement.executeBatch()));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                statement.executeBatch(); // insert remaining records
                status = Status.OK;
            } catch (SQLException e) {
                log.debug(e.getMessage(), e);
                status = Status.SQL_EXCEPTION;
            }
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return status;
    }

    /**
     * Loads reviews and users into DB where username is not blank
     * Requires an active database connection.
     *
     * @return status.ok if all users and reviews added successfully
     * @throws SQLException if unable to establish database connection
     */
    public Status addUserReviewDataDB(HotelData hdata) throws SQLException {
        Status status = Status.OK;
        try (Connection connection = db.getConnection();) {
            String[] pwdAndSalt = generateHashedPwdAndSalt("HotelHelper@123");
            Set<String> hotels = hdata.getUserReviewHotelKeys();
            final int batchSize = 100;
            hotels.forEach(hotelId->{
                try (PreparedStatement statement = connection.prepareStatement(REGISTER_SQL, Statement.RETURN_GENERATED_KEYS);) {
                    AtomicInteger count = new AtomicInteger();
                    Set<HotelReview> reviews = hdata.getUserReviews(hotelId);
                    Set<HotelReview> iteratedReviews = new HashSet<>();
                    reviews.forEach(review->{
                        try {
                            statement.setString(1, review.getUser());
                            statement.setString(2, pwdAndSalt[1]);
                            statement.setString(3, pwdAndSalt[0]);
                            statement.addBatch();

                            iteratedReviews.add(review);

                            if (count.incrementAndGet() % batchSize == 0) {
                                statement.executeBatch();
                                List<Integer> insertIds = new ArrayList<>();
                                try (ResultSet rs = statement.getGeneratedKeys()) {
                                    while (rs.next()) {
                                        insertIds.add(rs.getInt(1));
                                    }
                                    insertReviewsInBulk(connection, iteratedReviews, insertIds);
                                } catch (SQLException ex) {
                                    Status status1 = Status.SQL_EXCEPTION;
                                    log.debug(status1, ex);
                                }
                                iteratedReviews.clear();
                            }
                        }
                        catch (SQLException ex) {
                            Status status1 = Status.SQL_EXCEPTION;
                            log.debug(status1, ex);
                        }
                    });
                    statement.executeBatch(); // insert remaining records
                    List<Integer> insertIds = new ArrayList<>();
                    try (ResultSet rs = statement.getGeneratedKeys()) {
                        while (rs.next()) {
                            insertIds.add(rs.getInt(1));
                        }
                        insertReviewsInBulk(connection, iteratedReviews, insertIds);
                    } catch (SQLException e) {
                        Status status1 =  Status.SQL_EXCEPTION;
                        log.debug(e.getMessage(), e);
                    }
                } catch (SQLException ex) {
                    Status status1 = Status.SQL_EXCEPTION;
                    log.debug(status1, ex);
                }
            });
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return status;
    }

    private void insertReviewsInBulk(Connection connection,Set<HotelReview> iteratedReviews, List<Integer> insertIds) {

        AtomicInteger index = new AtomicInteger();int insertions = insertIds.size();
        Status status;

        try (PreparedStatement statement = connection.prepareStatement(ADD_REVIEWS_USER_SQL);) {
            for (HotelReview review : iteratedReviews) {
                try {
                    if (index.get() < insertions) {
                        statement.setString(1, review.getHotelId());
                        statement.setInt(2, insertIds.get(index.get()));
                        statement.setInt(3, review.getRatingOverall());
                        statement.setString(4, review.getTitle());
                        statement.setString(5, review.getReviewText());

                        String date = review.getReviewSubmissionTime();
                        String submissionTime = date.substring(0,10) + " " + date.substring(11,19);
                        statement.setString(6, submissionTime);
                        statement.setInt(7, review.isRecommended() == true ? 1 : 0);
                        statement.addBatch();
                    }
                } catch (SQLException e) {
                    log.debug(e.getMessage(), e);
                    status = Status.SQL_EXCEPTION;
                    continue;
                }
                index.incrementAndGet();
            }
            statement.executeBatch();

        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
            status = Status.SQL_EXCEPTION;
        }
    }

    /**
     * Loads reviews and users into DB where username is blank.
     * Requires an active database connection.
     *
     * @return status.ok if all users and reviews added successfully
     * @throws SQLException if unable to establish database connection
     */
    public Status addAnonyUserReviewDataDB(HotelData hdata) throws SQLException {
        Status status = Status.OK;
        AtomicInteger mainCount = new AtomicInteger();
        try (Connection connection = db.getConnection();) {
            String[] pwdAndSalt = generateHashedPwdAndSalt("HotelHelper@123");
            Set<String> hotels = hdata.getAnnReviewKeys();
            final int batchSize = 100;
            hotels.forEach(hotelId->{
                try (PreparedStatement statement = connection.prepareStatement(REGISTER_SQL, Statement.RETURN_GENERATED_KEYS);) {
                    AtomicInteger count = new AtomicInteger();
                    Set<HotelReview> reviews = hdata.getAnnUserReviews(hotelId);
                    Set<HotelReview> iteratedReviews = new HashSet<>();
                    reviews.forEach(review->{
                        try {
                            statement.setString(1, "Anonymous"+mainCount);
                            statement.setString(2, pwdAndSalt[1]);
                            statement.setString(3, pwdAndSalt[0]);
                            statement.addBatch();

                            iteratedReviews.add(review);
                            mainCount.incrementAndGet();

                            if (count.incrementAndGet() % batchSize == 0) {
                                statement.executeBatch();
                                List<Integer> insertIds = new ArrayList<>();
                                try (ResultSet rs = statement.getGeneratedKeys()) {
                                    while (rs.next()) {
                                        insertIds.add(rs.getInt(1));
                                    }
                                    insertReviewsInBulk(connection, iteratedReviews, insertIds);
                                } catch (SQLException ex) {
                                    Status status1 = Status.SQL_EXCEPTION;
                                    log.debug(status1, ex);
                                }
                                iteratedReviews.clear();
                            }
                        }
                        catch (SQLException ex) {
                            Status status1 = Status.SQL_EXCEPTION;
                            log.debug(status1, ex);
                        }
                    });
                    statement.executeBatch(); // insert remaining records
                    List<Integer> insertIds = new ArrayList<>();
                    try (ResultSet rs = statement.getGeneratedKeys()) {
                        while (rs.next()) {
                            insertIds.add(rs.getInt(1));
                        }
                        if(insertIds.size()>0){
                            insertReviewsInBulk(connection, iteratedReviews, insertIds);
                        }
                    } catch (SQLException e) {
                        Status status1 =  Status.SQL_EXCEPTION;
                        log.debug(e.getMessage(), e);
                    }
                } catch (SQLException ex) {
                    Status status1 = Status.SQL_EXCEPTION;
                    log.debug(status1, ex);
                }
            });
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return status;
    }

    /**
     * Return the name of all the distinct cities of different hotels in the DB
     * @return list of cities
     * @throws SQLException if unable to establish database connection
     */
    public List<String> getHotelCities(){
        Status status = Status.ERROR;
        log.info("Fetching cities ....");
        List<String> list= new ArrayList<String>();

        try (
            Connection connection = db.getConnection();
            Statement stmt = connection.createStatement();
        ) {
            ResultSet rs = stmt.executeQuery(CITIES_SQL);
            while (rs.next()) {
                list.add(rs.getString("city"));
            }
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  list;
    }

    /**
     * Returns an array of multiple hotel data
     * @param city city of hotel
     * @param hotelPattern pattern against which hotel name will be checked
     * @param order Order of data being fetched
     * @return array of hotel's data
     * @throws SQLException if unable to establish database connection
     */
    public JsonArray getHotelsData(String city, String hotelPattern, int order){
        Status status = Status.ERROR;
        log.info("Fetching hotels ....");
        JsonArray hotelsArr = new JsonArray();

        try (Connection connection = db.getConnection();) {
            hotelsArr = getHotelJsonArr(connection,city,hotelPattern,order);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  hotelsArr;
    }

    /**
     * Returns user hotel wishlist data
     * @param userId id of the user
     * @param hotelId id of the hotel
     * @return JsonArray of hotel wishlist data
     * @throws SQLException if unable to establish database connection
     */
    public JsonArray getHotelsWishList(int userId, String hotelId){
        Status status = Status.ERROR;
        log.info("Fetching hotels wish lists ....");
        JsonArray hotelsArr = new JsonArray();

        try (Connection connection = db.getConnection();) {
            hotelsArr = getWishListJsonArr(connection,userId, hotelId);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  hotelsArr;
    }

    /**
     * Returns data of the links visited by the user
     * @param userId id of the user
     * @return array of json of visited links
     * @throws SQLException if unable to establish database connection
     */
    public JsonArray getUserVisitedLinks(int userId){
        Status status = Status.ERROR;
        log.info("Fetching visited links ....");
        JsonArray hotelsArr = new JsonArray();

        try (Connection connection = db.getConnection();) {
            hotelsArr = getVisitedLinksArr(connection,userId);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  hotelsArr;
    }

    /**
     * Returns count of review usefulness
     * @param hotelId Id of the hotel
     * @return Array of json of review recommendation status
     * @throws SQLException if unable to establish database connection
     */
    public JsonArray getReviewRecommends(String hotelId, int userId){
        Status status = Status.ERROR;
        log.info("Fetching hotels wish lists ....");
        JsonArray hotelsArr = new JsonArray();

        try (Connection connection = db.getConnection();) {
            hotelsArr = getReviewLikesJsonArr(connection,hotelId, userId);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  hotelsArr;
    }

    /**
     * Returns all the liked reviews of a hotel of a user
     * @param hotelId Id of hotel
     * @param userId Id of user
     * @return array of json of reviewsIds of liked reviews
     */
    public JsonArray getUserLikesPerHotel(String hotelId, int userId){
        Status status = Status.ERROR;
        log.info("Fetching user likes ....");
        JsonArray hotelsArr = new JsonArray();

        try (Connection connection = db.getConnection();) {
            hotelsArr = getUserLikesJsonArr(connection,hotelId,userId);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  hotelsArr;
    }

    /**
     * Returns data of hotels to be shown on map
     * @return JsonArray of hotel lat long
     * @throws SQLException if unable to establish database connection
     */
    public JsonArray getHotelsMapData(){
        Status status = Status.ERROR;
        log.info("Fetching hotels name, lat and lng for map ....");
        JsonArray hotelsArr = new JsonArray();

        try (Connection connection = db.getConnection();) {
            hotelsArr = getHotelsLatLng(connection);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  hotelsArr;
    }

    /**
     * Return all the reviews of a hotel
     * @param hotelId id of hotel
     * @return JsonArray of reviews
     * @throws SQLException if unable to establish database connection
     */
    public JsonArray getReviewsData(String hotelId){

        Status status = Status.ERROR;
        log.info("Fetching reviews ....");
        JsonArray reviewsArr = new JsonArray();

        try (Connection connection = db.getConnection();) {
            reviewsArr = getReviewsJsonArr(connection,hotelId);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  reviewsArr;
    }

    /**
     * Gets id of review for a hotel- user combination
     * @param hotelId id of hotel
     * @param userId if of user
     * @return review id
     * @throws SQLException if unable to establish database connection
     */
    public int getReviewId(String hotelId, int userId){
        int reviewId = -1;
        Status status = Status.ERROR;
        log.info("Fetching reviewId ....");
        try (Connection connection = db.getConnection();) {
            reviewId = getReviewIdForHotelUser(connection,hotelId,userId);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  reviewId;
    }

    /**
     *
     * @param hotelId Id of hotel
     * @param rating rating of the review
     * @param title title of review
     * @param reviewText text of review
     * @param isRecommended recommendation status for a review
     * @param userId Id of the user
     * @return insertion Id of added review
     * @throws SQLException if unable to establish database connection
     */
    public int insertReview(int hotelId, int rating, String title, String reviewText, int isRecommended, int userId, String date){

        Status status = Status.ERROR;
        int insertId = -1;
        log.info("inserting reviews for user_id "+ userId +"....");

        try (Connection connection = db.getConnection();) {
            insertId = insertReview(connection,hotelId,rating,title,reviewText,isRecommended,userId, date);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return insertId;
    }

    /**
     * This updates a corresponding review
     * @param reviewId Id of review to be updated
     * @param rating rating of the review
     * @param title title of review
     * @param reviewText text of review
     * @param isRecommended recommendation status for a review
     * @param userId Id of the user
     * @return Status.OK if review updated sucessfully
     * @throws SQLException if unable to establish database connection
     */
    public Status updateReview(int reviewId, int rating, String title, String reviewText, int isRecommended, int userId, String date){

        Status status = Status.ERROR;
        log.info("inserting reviews for user_id "+ userId +"....");

        try (Connection connection = db.getConnection();) {
            status = updateHotelReview(connection,reviewId,rating,title,reviewText,isRecommended, userId, date);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  status;
    }

    /**
     * This deletes a review from DB
     * @param reviewId Id of the review
     * @param userId Id of the user
     * @return Status.OK if review deleted successfully
     * @throws SQLException if unable to establish database connection
     */
    public Status deleteReview(int reviewId, int userId){

        Status status = Status.ERROR;
        log.info("inserting reviews for user_id "+ userId +"....");

        try (Connection connection = db.getConnection();) {
            status = deleteHotelReview(connection,reviewId,userId);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  status;
    }

    private int insertReview(Connection connection, int hotelId,int rating,String title,String reviewText,int isRecommended, int userId, String date) {

        assert connection != null;

        Status status = Status.ERROR;
        int insertId = 0;
        try (
            PreparedStatement statement = connection.prepareStatement(ADD_REVIEW_SQL, Statement.RETURN_GENERATED_KEYS);) {
            statement.setInt(1, hotelId);
            statement.setInt(2, userId);
            statement.setInt(3, rating);
            statement.setString(4, title);
            statement.setString(5, reviewText);
            statement.setInt(6, isRecommended);
            statement.setString(7, date);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating review failed, no rows affected.");
            }
            try (ResultSet rs = statement.getGeneratedKeys()) {
                while (rs.next()) {
                    insertId = rs.getInt(1);
                    status = Status.OK;
                }
            }
        } catch (SQLException ex) {
            status = Status.SQL_EXCEPTION;
            log.debug(ex.getMessage(), ex);
        }
        return insertId;
    }

    private Status updateHotelReview(Connection connection, int reviewId,int rating,String title,String reviewText,int isRecommended ,int userId, String date) {

        assert connection != null;

        Status status = Status.ERROR;
        try (
            PreparedStatement statement = connection.prepareStatement(UPDATE_REVIEW_SQL)
        ) {
            statement.setInt(1, rating);
            statement.setString(2, title);
            statement.setString(3, reviewText);
            statement.setInt(4, isRecommended);
            statement.setString(5, date);
            statement.setInt(6, userId);
            statement.setInt(7, reviewId);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating review failed, no rows affected.");
            }
            status = Status.OK;

        } catch (SQLException ex) {
            status = Status.SQL_EXCEPTION;
            log.debug(ex.getMessage(), ex);
        }
        return status;
    }
    private Status deleteHotelReview(Connection connection, int reviewId,int userId) {

        assert connection != null;

        Status status = Status.ERROR;
        try (
            PreparedStatement statement = connection.prepareStatement(DELETE_REVIEW_SQL)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, reviewId);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting review failed, no rows affected.");
            }
            status = Status.OK;

        } catch (SQLException ex) {
            status = Status.SQL_EXCEPTION;
            log.debug(ex.getMessage(), ex);
        }
        return status;
    }

    /**
     * Gets a hotel's data
     * @param hotelId If of the hote
     * @return JsonObject containing hotel data
     * @throws SQLException if unable to establish database connection
     */
    public JsonObject getHotelData(String hotelId){
        Status status = Status.ERROR;
        log.info("Fetching hotel " + hotelId + " ....");
        JsonObject hotelObj = null;
        try (Connection connection = db.getConnection();) {
            hotelObj = getHotel(connection,hotelId);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return  hotelObj;
    }

    private JsonArray getHotelJsonArr(Connection connection, String city, String hotelPattern, int order) {
        assert connection != null;
        assert city != null;
        assert hotelPattern != null;

        JsonArray hotelsArr = new JsonArray();

        String orderedHotelSql = order == 0 ? HOTELS_DESC_SQL :  HOTELS_SQL;
        try(PreparedStatement statement = connection.prepareStatement(orderedHotelSql);) {
            statement.setString(1, "%"+ hotelPattern + "%");
            statement.setString(2, "%"+ city + "%");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                JsonObject hotelObj = createHotelObject(connection, rs,1,1);
                hotelsArr.add(hotelObj);
            }
        }
        catch (SQLException ex){
            log.debug(ex.getMessage(), ex);
        }
        return hotelsArr;
    }

    private JsonArray getWishListJsonArr(Connection connection, int userId, String hotelId) {
        assert connection != null;
        JsonArray hotelsArr = new JsonArray();
        String finalStatement = hotelId == null ? USR_WISH_LIST_SQL : USR_HOTEL_WISH_LIST_SQL;
        try(PreparedStatement statement = connection.prepareStatement(finalStatement);) {
            statement.setInt(1, userId);
            if(hotelId != null){
                statement.setString(2, hotelId);
            }
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                JsonObject hotelObj = createWishListObject(connection, rs);
                hotelsArr.add(hotelObj);
            }
        }
        catch (SQLException ex){
            log.debug(ex.getMessage(), ex);
        }
        return hotelsArr;
    }

    private JsonArray getVisitedLinksArr(Connection connection, int userId) {
        assert connection != null;
        JsonArray hotelsArr = new JsonArray();
        try(PreparedStatement statement = connection.prepareStatement(USR_VISITED_LINKS_SQL);) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                JsonObject hotelObj = createVisitedLinksObj(connection, rs);
                hotelsArr.add(hotelObj);
            }
        }
        catch (SQLException | ParseException ex){
            log.debug(ex.getMessage(), ex);
        }
        return hotelsArr;
    }

    private JsonArray getReviewLikesJsonArr(Connection connection, String hotelId, int userId) {

        assert connection != null;

        JsonArray hotelsArr = new JsonArray();
        try(PreparedStatement statement = connection.prepareStatement(USR_REVIEW_LIKES_SQL);) {
            statement.setString(1, hotelId);
            statement.setInt(2, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                JsonObject hotelObj = createReviewLikesObject(connection, rs);
                hotelsArr.add(hotelObj);
            }
        }
        catch (SQLException ex){
            log.debug(ex.getMessage(), ex);
        }
        return hotelsArr;
    }

    private JsonArray getUserLikesJsonArr(Connection connection, String hotelId, int userId){

        assert connection != null;

        JsonArray hotelsArr = new JsonArray();
        try(PreparedStatement statement = connection.prepareStatement(USR_LIKES_SQL);) {
            statement.setString(1, hotelId);
            statement.setInt(2, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                JsonObject hotelObj = createReviewIdLikeObj(connection, rs);
                hotelsArr.add(hotelObj);
            }
        }
        catch (SQLException ex){
            log.debug(ex.getMessage(), ex);
        }
        return hotelsArr;
    }

    private JsonArray getHotelsLatLng(Connection connection) {
        assert connection != null;
        JsonArray hotelsArr = new JsonArray();
        try(Statement stmt = connection.createStatement();) {
            ResultSet rs = stmt.executeQuery(HOTELS_MAP_SQL);
            while (rs.next()) {
                JsonObject hotelObj = createHotelObject(connection, rs,0,0);
                hotelsArr.add(hotelObj);
            }
        }
        catch (SQLException ex){
            log.debug(ex.getMessage(), ex);
        }
        return hotelsArr;
    }

    private JsonArray getReviewsJsonArr(Connection connection, String hotelId) {
        assert connection != null;
        assert hotelId != null;

        JsonArray hotelsArr = new JsonArray();

        try(PreparedStatement statement = connection.prepareStatement(HOTELS_REVIEWS_SQL);) {
            statement.setString(1, hotelId);
            ResultSet rs = statement.executeQuery();
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
            while (rs.next()) {
                JsonObject hotelObj =
                        createReviewObject(connection, rs,dateFormat);
                hotelsArr.add(hotelObj);
            }
        }
        catch (SQLException | ParseException ex){
            log.debug(ex.getMessage(), ex);
        }
        return hotelsArr;
    }

    private int getReviewIdForHotelUser(Connection connection, String hotelId, int userId) {
        assert connection != null;
        assert hotelId != null;
        int reviewId = -1;

        try(PreparedStatement statement = connection.prepareStatement(HOTELS_REVIEW_SQL);) {
            statement.setInt(1, userId);
            statement.setString(2, hotelId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                reviewId = rs.getInt("review_id");
            }
        }
        catch (SQLException ex){
            log.debug(ex.getMessage(), ex);
        }
        return reviewId;
    }

    private JsonObject getHotel(Connection connection, String hotelId) {
        assert connection != null;
        assert hotelId != null;
        JsonObject hotelObj = new JsonObject();

        try(PreparedStatement statement = connection.prepareStatement(HOTEL_SQL);) {
            statement.setString(1, hotelId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                hotelObj = createHotelObject(connection, rs,0,1);
            }
        }
        catch (SQLException ex){
            log.debug(ex.getMessage(), ex);
        }
        return hotelObj;
    }

    private JsonObject createHotelObject(Connection connection,ResultSet rs, int metaDataRequired, int nonMapDataRqd) throws SQLException {

        assert connection != null;

        JsonObject hotelObj = new JsonObject();
        if(nonMapDataRqd == 1){
            hotelObj.addProperty("hotel_id", rs.getInt("id"));
            hotelObj.addProperty("address", rs.getString("address"));
        }
        hotelObj.addProperty("name", rs.getString("name"));
        hotelObj.addProperty("city", rs.getString("city"));
        hotelObj.addProperty("state", rs.getString("state"));
        hotelObj.addProperty("latitude", rs.getString("latitude"));
        hotelObj.addProperty("longitude", rs.getString("longitude"));

        if(metaDataRequired == 1){
            hotelObj.addProperty("review_count", rs.getString("review_count"));
            hotelObj.addProperty("avg_rating", rs.getString("avg_rating"));
        }
        return hotelObj;
    }
    private JsonObject createWishListObject(Connection connection,ResultSet rs) throws SQLException {

        assert connection != null;

        JsonObject hotelObj = new JsonObject();
        hotelObj.addProperty("name", rs.getString("name"));
        hotelObj.addProperty("hotel_id", rs.getString("hotel_id"));
        hotelObj.addProperty("date", rs.getString("date"));
        return hotelObj;
    }
    private JsonObject createVisitedLinksObj(Connection connection,ResultSet rs) throws SQLException, ParseException {

        assert connection != null;

        JsonObject hotelObj = new JsonObject();
        hotelObj.addProperty("link", rs.getString("link"));
        hotelObj.addProperty("name", rs.getString("name"));
        String date  = rs.getString("date");
        SimpleDateFormat sdfo = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
        Date d1 = sdfo.parse(date);
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
        hotelObj.addProperty("date", dateFormat.format(d1));
        return hotelObj;
    }

    private JsonObject createReviewLikesObject(Connection connection,ResultSet rs) throws SQLException {

        assert connection != null;

        JsonObject hotelObj = new JsonObject();
        hotelObj.addProperty("review_id", rs.getString("review_id"));
        hotelObj.addProperty("count", rs.getString("count"));
        return hotelObj;
    }
    
    private JsonObject createReviewIdLikeObj(Connection connection,ResultSet rs) throws SQLException {

        assert connection != null;

        JsonObject hotelObj = new JsonObject();
        hotelObj.addProperty("review_id", rs.getString("review_id"));
        hotelObj.addProperty("status", rs.getString("status"));
        return hotelObj;
    }

    private JsonObject createUserObject(Connection connection,ResultSet rs) throws SQLException {
        assert connection != null;

        JsonObject userObj = new JsonObject();
        userObj.addProperty("username", rs.getString("username"));
        userObj.addProperty("user_id", rs.getInt("id"));
        if(rs.getString("current_login_time") != null){
            userObj.addProperty("current_login_time", rs.getString("current_login_time"));
        }
        if(rs.getString("last_login_time") != null){
            userObj.addProperty("last_login_time", rs.getString("last_login_time"));
        }
        return userObj;
    }

    private JsonObject createReviewObject(Connection connection,ResultSet rs, DateFormat dateFormat) throws SQLException, ParseException {
        assert connection != null;
        JsonObject hotelObj = new JsonObject();
        try {

        hotelObj.addProperty("username", rs.getString("username"));
        hotelObj.addProperty("title", rs.getString("title"));
        hotelObj.addProperty("review_id", rs.getString("review_id"));
        hotelObj.addProperty("rating", rs.getString("rating"));
        hotelObj.addProperty("review_text", rs.getString("review_text"));
        hotelObj.addProperty("is_recommended", rs.getInt("is_recommended"));

            String date = rs.getString("review_submission_time");

            SimpleDateFormat sdfo = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
            Date d1 = sdfo.parse(date);
            hotelObj.addProperty("review_submission_time", dateFormat.format(d1));
        }
        catch (ParseException e){
            log.error(e.getMessage());
        }

        return hotelObj;
    }

    /**
     * Checks if the provided username and password match what is stored
     * in the database. Must retrieve the salt and hash the password to
     * do the comparison.
     *
     * @param username - username to authenticate
     * @param password - password to authenticate
     * @return status.ok if authentication successful
     */
    public Status authenticateUser(String username, String password) {
        Status status = Status.ERROR;

        log.debug("Authenticating user " + username + ".");

        try (
                Connection connection = db.getConnection()
        ) {
            status = authenticateUser(connection, username, password);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return status;
    }

    /**
     * This updates the last login time of the user
     * @param lastLoginTime The last time when the user logged in before the current login
     * @param currentLoginTime Current login time
     * @return Status.OK if last login time updated successfully
     * @throws SQLException if unable to establish database connection
     */
    public Status updateLoginTime(String lastLoginTime, String currentLoginTime) {
        Status status = Status.ERROR;
        try (
                Connection connection = db.getConnection()
        ) {
            status = updateLoginTimeInDB(connection, lastLoginTime, currentLoginTime);
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return status;
    }

    /**
     * Gets a user's data with respect to username
     *
     * @param username - username of user
     * @return json object of user
     * @throws SQLException if unable to establish database connection
     */
    public JsonObject getUser(String username) {
        Status status = Status.ERROR;
        JsonObject userObj = null;
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(USER_SQL);
        ) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                userObj = createUserObject(connection, rs);
            }
        } catch (SQLException ex) {
            status = Status.CONNECTION_FAILED;
            log.debug(status, ex);
        }
        return userObj;
    }
}
