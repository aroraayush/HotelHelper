package model;

import com.google.gson.stream.JsonReader;
import utilities.DatabaseHandler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
/** Class HotelDataBuilder. Loads hotel info from input files to HotelData. */
public class HotelDataBuilder {

    // the "big" HotelData that will contain all data
	private HotelData hdata;

    private HotelData getHdata() {
        return hdata;
    }

    /** Constructor for class HotelDataBuilder.
	 *  @param data data of type HotelData
     */
	public HotelDataBuilder(HotelData data) {
		this.hdata = data;
	}

	/**
	 * Read the json file with information about the hotels and load it into the
	 * appropriate data structure(s).
	 * @param jsonFilename The filename of the file that contains data of Hotel
	 */
	public void loadHotelInfo(String jsonFilename) {

        try (JsonReader reader = new JsonReader(new FileReader(jsonFilename)))  {
            reader.beginObject();

            while (reader.hasNext()) {

                String name = reader.nextName();

                if (name.equals("sr")) {
                    double lat;
                    double lon;
                    String hotelId;
                    String hotelName;
                    String city;
                    String state;
                    String streetAddress;

                    lat = lon = 0;
					hotelId = hotelName = city = state = streetAddress = "";

                    reader.beginArray();

                    while (reader.hasNext()) {

                        reader.beginObject();

                        while (reader.hasNext()) {

                            String hotelObjKeyName = reader.nextName();

                            if (hotelObjKeyName.equals("f")) {
                                hotelName = reader.nextString();

                            } else if (hotelObjKeyName.equals("ci")) {
                                city = reader.nextString();

                            } else if (hotelObjKeyName.equals("pr")) {
                                state = reader.nextString();

                            } else if (hotelObjKeyName.equals("ad")) {
                                streetAddress = reader.nextString();

                            } else if (hotelObjKeyName.equals("id")) {
                                hotelId = reader.nextString();

                            } else if (hotelObjKeyName.equals("ll")) {

                                reader.beginObject();

                                while (reader.hasNext()) {

                                    String name2 = reader.nextName();

                                    if (name2.equals("lat")) {
                                        lat = reader.nextDouble();
                                    }
                                    else if (name2.equals("lng")) {
										lon = reader.nextDouble();
                                    }
                                    else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            }
                            else {
                                reader.skipValue();
                                continue;
                            }
                        }
                        reader.endObject();
						hdata.addHotel(hotelId, hotelName, city, state, streetAddress, lat, lon);
                    }
                    reader.endArray();
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException : " + e);
        } catch (IOException e) {
            System.out.println("IOException : Could not read the file at location:" + jsonFilename + "\n" +e);
        }
	}

	/** Loads reviews from json files. Recursively processes subfolders.
	 *  Gives JSON files to threads to process them
	 *  Each json file with reviews is processed concurrently (a new runnable job is
	 *  created for each json file that is encountered)
	 *  @param dir Path of a JSON review file
	 */
	public void loadReviews(Path dir) {
		try (DirectoryStream<Path> filesList = Files.newDirectoryStream(dir)) {
			for (Path file : filesList) {
				if (Files.isDirectory(file)){
					loadReviews(file);
				}
				else{
					Path pathTillReviewJSONFile = Paths.get(dir.toString(),file.getFileName().toString());
                    hdata.parseReviews(pathTillReviewJSONFile);
				}
			}
		} catch (IOException e) {
			System.out.println("Can not open directory: " + dir.toString());
		}
	}

    /**
     * Loads hotels data and reviews data in the database
     * @param dbHandler the Database handler object
     * @param hdata HotelData
     * @throws SQLException if unable to establish database connection
     */
	public void addHotelDataReviewsToDB(DatabaseHandler dbHandler,HotelData hdata) throws SQLException {
        dbHandler.addHotelDataToDB(getHdata());
	    dbHandler.addUserReviewDataDB(getHdata());
        dbHandler.addAnonyUserReviewDataDB(getHdata());
    }
}
