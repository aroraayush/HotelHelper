package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.TouristAttractionFinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class represents a servlet that triggers at endpoint /attractions which gives attractions near a hotel location
 */
public class HotelAttractionsServlet extends BaseServlet {

    private TouristAttractionFinder finder;

    /**
     *  This gets the request object and returns Tourist Attractions near the hotel
     * @param request http request object
     * @param response http response object containg Tourist Attractions near the hotel
     * @throws IOException if error in processing request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);

        // Getting body params and cleaning up
        String city = request.getParameter("city");
        String lat = request.getParameter("lat");
        String lng = request.getParameter("lng");
        String radius = request.getParameter("radius");
        PrintWriter out = response.getWriter();

        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            if(city == null || city.trim().isEmpty())
                out.println(invalidKeyName("city"));
            else if(radius == null || radius.trim().isEmpty())
                out.println(invalidKeyName("radius"));
            else if(lat == null || lat.trim().isEmpty())
                out.println(invalidKeyName("lat"));
            else if(lng == null || lng.trim().isEmpty())
                out.println(invalidKeyName("lng"));
            else{
                city = escapeHtml(city).trim();
                radius = escapeHtml(radius).trim();
                lat = escapeHtml(lat).trim();
                lng = escapeHtml(lng).trim();

                int radiusInt = Integer.parseInt(radius);
                if(radiusInt <= 0){
                    out.println(invalidKeyName("lng"));
                }
                else {
                    this.finder = new TouristAttractionFinder(city,radiusInt,lat,lng);
                    JsonArray attrctionsArr = finder.findAttractions();
                    if(attrctionsArr == null || attrctionsArr.size() == 0){
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    }
                    else{

                        JsonObject resObj = new JsonObject();
                        resObj.addProperty("success",true);

                        resObj.add("data", attrctionsArr);
                        resObj.addProperty("length",attrctionsArr.size());
                        out.println(resObj);
                        response.setCharacterEncoding("UTF-8");
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
            response.setContentType("application/json");
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }
}