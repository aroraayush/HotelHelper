package controller;

import com.google.gson.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * This class represents a servlet that returns the different cities where hotels are located
 */
public class HotelCityServlet extends BaseServlet {

    /**
     *  This gets the request object and returns data of a hotel
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);

        List<String> cities = dbHandler.getHotelCities();
        if(cities == null || cities.size() == 0){
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        else{
            JsonObject resObj = new JsonObject();
            resObj.addProperty("success",true);
            PrintWriter out = response.getWriter();
            JsonArray cityArray = new JsonArray();
            cities.forEach(city->{
                cityArray.add(city);
            });

            resObj.add("data", cityArray);
            resObj.addProperty("length",cities.size());
            out.println(resObj);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
        }
    }
}