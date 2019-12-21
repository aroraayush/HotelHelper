package utilities;

import model.HotelDataBuilder;
import model.HotelData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * This loads all the data from HotelData into Database tables.
 * Note tables should already exist
 */
public class DataLoader {

    private static HotelData hdata;
    private HotelDataBuilder builder;
    private final static  DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    /**
     * The returns HotelData instance
     * @return  HotelData instance
     */
    private static HotelData getHdata() {
        return hdata;
    }

    /**
     * The returns HotelDataBuilder instance
     * @return  HotelDataBuilder instance
     */
    private HotelDataBuilder getBuilder() {
        return builder;
    }

    /**
     *
     * @param hdata HotelData
     * @throws SQLException if unable to establish database connection
     */
    public DataLoader(HotelData hdata) throws SQLException {
        this.hdata = hdata;
        this.builder = new HotelDataBuilder(this.hdata);
    }

    public static void main(String[] args) throws SQLException {

        String hotelPath = "input/hotels.json";
        String reviewsDirPath = "input/reviews";

        if (hotelPath == null || hotelPath.isEmpty() || testPathString(hotelPath)){
            System.out.println("Error: hotel Path cannot be empty, blank spaces or incorrect location");
        }
        else if (reviewsDirPath == null || reviewsDirPath.isEmpty() || testPathString(reviewsDirPath)){
            System.out.println("Error: Reviews directory path cannot be empty, blank spaces or incorrect location");
        }
        else {

            HotelData hdata = new HotelData();
            DataLoader loader = new DataLoader(hdata);
            loader.getBuilder().loadHotelInfo(hotelPath);
            loader.getBuilder().loadReviews(Paths.get(reviewsDirPath));
            System.out.println("Done loading data to HotelData object");
            loader.getBuilder().addHotelDataReviewsToDB(dbHandler, getHdata());
            System.out.println("Finished adding Data to Database");
        }
    }

    /**
     * A helper function that tests whether a provided input string is valid or not.
     * @param pathString  String form of path of location of a file
     * @return Boolean True if string/path is valid, False if not valid
     */
    private static boolean testPathString(String pathString){
        if(pathString.trim().length() > 0){
            Path tempPath = Paths.get(pathString);
            return !(Files.exists(tempPath));
        }
        return false;
    }
}
