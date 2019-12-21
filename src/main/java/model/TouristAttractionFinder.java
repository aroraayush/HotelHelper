package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class helps in find attractions near a specific location
 */
public class TouristAttractionFinder {

    private String city;
    private double radius;
    private String lat;
    private String lng;

    /**
     * Constructor for TouristAttractionFinder
     * @param city hotel city
     * @param radiusInMiles hotel boundary value
     * @param lat latitude
     * @param lng longitude
     */
    public TouristAttractionFinder(String city, int radiusInMiles, String lat, String lng) {
        this.city = city;
        this.radius = covertMileToMeter(radiusInMiles);
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * This function finds the attraction near a hotel within a specific radius and array json of the same
     * @return returns the response received from Google API after some processing
     */
    public JsonArray findAttractions(){
        String jsonString = null;
        JsonArray attractionsArr = new JsonArray();
        Map<String,String> config = getMapsConfig();

        try{
            StringBuilder requestQuery = new StringBuilder("https://"+config.get("host")+config.get("path")+"?query=");
            String query = "tourist attractions in ".concat(city);
            requestQuery.append(URLEncoder.encode(query, "UTF-8"));
            requestQuery.append("&location="+lat+","+lng);
            requestQuery.append("&radius="+radius);
            requestQuery.append("&key="+ config.get("apikey"));

            URL url = new URL(requestQuery.toString());
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            // HTTPS uses port 443
            try(SSLSocket socket = (SSLSocket) factory.createSocket(url.getHost(), 443);
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

                String line;
                int count = 0;
                StringBuffer sb = new StringBuffer();

                String request = getRequest(url.getHost(), url.getPath() + "?" + url.getQuery());
                out.println(request); // send a request to the server
                out.flush();

                while ((line = in.readLine()) != null) {
                    if (line.trim().isEmpty()){
                        count++;
                    }
                    if(count > 0){
                        sb.append(line);
                    }
                }
                jsonString = sb.toString().trim();
                attractionsArr = loadAttractionsInfo(jsonString);
            }
            catch (UnsupportedEncodingException | MalformedURLException e) {
                System.out.println("UnsupportedEncodingException | MalformedURLException : " + e);
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException : "+e);
        } catch (IOException e) {
            System.err.println("IOException : " + e);
        }
        return attractionsArr;
    }

    /**
     * Converts radius in miles to meters
     * @param radiusInMiles Radius for finding attractions
     * @return radius Radius in unit meter
     */
    private double covertMileToMeter(int radiusInMiles){
        return radiusInMiles / 0.00062137;
    }

    /**
     * Takes a host and a string containing path/resource/query and creates a
     * string of the HTTP GET request
     * @param host
     * @param pathResourceQuery
     * @return
     */
    private static String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET
                // request
                + "Host: " + host + System.lineSeparator() // Host header required for HTTP/1.1
                + "Connection: close" + System.lineSeparator() // make sure the server closes the
                // connection after we fetch one page
                + System.lineSeparator();
        return request;
    }

    /**
     * Read the json object with information about the attractions near a hotel and loads it into the
     * appropriate data structure(s).
     * @param jsonString The JSON object extracted from the HTTP response
     */
    private JsonArray loadAttractionsInfo(String jsonString) {

        JsonArray attractionsArr = new JsonArray();

        try (JsonReader reader = new JsonReader(new StringReader(jsonString)))  {
            reader.beginObject();

            while (reader.hasNext()) {

                String keyName = reader.nextName();

                if (keyName.equals("results")) {

                    reader.beginArray();

                    while (reader.hasNext()) {

                        reader.beginObject();
                        String id, name, address;
                        double rating;
                        rating = 0;
                        id = name = address = "";

                        while (reader.hasNext()) {

                            String innerKeyName = reader.nextName();

                            if (innerKeyName.equals("formatted_address")) {
                                address = reader.nextString();
                            } else if (innerKeyName.equals("name")) {
                                name = reader.nextString();
                            } else if (innerKeyName.equals("rating")) {
                                rating = reader.nextDouble();
                            } else if (innerKeyName.equals("id")) {
                                id = reader.nextString();
                            } else {
                                reader.skipValue();
                                continue;
                            }
                        }
                        reader.endObject();
                        JsonObject attractionObj = createAttrObject(name, rating, address);
                        attractionsArr.add(attractionObj);
                    }
                    reader.endArray();
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return attractionsArr;
    }

    private JsonObject createAttrObject(String name, double rating, String address) {
        JsonObject attraction = new JsonObject();
        attraction.addProperty("name",name);
        attraction.addProperty("rating",rating);
        attraction.addProperty("address",address);
        return attraction;
    }


    private Map<String,String> getMapsConfig(){

        String apikey = null;
        String path = null;
        String host = null;
        Map<String,String> configMap = new HashMap<>();

        // Obtaining the API key
        try (JsonReader reader = new JsonReader(new FileReader("input/config.json"))) {
            reader.beginObject();

            while (reader.hasNext()) {
                String keyName = reader.nextName();

                if (keyName.equals("apikey")) {
                    apikey = reader.nextString();
                    configMap.put("apikey",apikey);
                } else if (keyName.equals("host")) {
                    host = reader.nextString();
                    configMap.put("host",host);
                } else if (keyName.equals("path")) {
                    path = reader.nextString();
                    configMap.put("path",path);
                } else {
                    reader.skipValue();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException :"+e);
        } catch (IOException e) {
            System.err.println(
                    "An IOException occured while writing to the socket stream or reading from the stream: " + e);
            e.printStackTrace();
        }
        return configMap;
    }
}
