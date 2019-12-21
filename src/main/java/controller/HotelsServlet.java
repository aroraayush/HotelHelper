package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.velocity.Template;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class represents a servlet that triggers at endpoint /hotels. It does operations related to group of hotels
 */
public class HotelsServlet extends BaseServlet {

    /**
     *  This gets the request object and returns data of a hotels
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);
        initializeVelocity(request,response);

        // Getting query params
        String city = request.getParameter("city_name");
        String hotelPattern = request.getParameter("hotel_pattern");
        String order = request.getParameter("order");

        JsonArray hotelArr;
        int orderInt = 1;

        order = escapeHtml(order);

        if(order!=null && !order.trim().isEmpty() && Integer.parseInt(order)== 0) {
            orderInt = 0;
        }
        try {
            if(hotelPattern == null && city == null)
                hotelArr = dbHandler.getHotelsMapData();
            else {
                hotelPattern = hotelPattern == null ? "" : escapeHtml(hotelPattern);
                city = city == null ? "" : escapeHtml(city);
                hotelArr = dbHandler.getHotelsData(city,hotelPattern,orderInt);
            }

            PrintWriter out = response.getWriter();
            context.put("title", "Hotels in "+city.toUpperCase());
            context.put("importMapsAPI", true);
            context.put("hotelArr", hotelArr);
            context.put("city", city.toUpperCase());
            if(hotelPattern != null && hotelPattern.length()>0){
                context.put("hotelPattern", hotelPattern);
            }
            Template template = ve.getTemplate("src/main/webapp/view/hotel/hotels.vm");
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            out.println(writer.toString());
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }

    /**
     *  This gets the request object and returns data of a hotels
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doInitialChecks(request,response);

        // Getting body params
        String city = request.getParameter("city_name");
        String hotelPattern = request.getParameter("hotel_pattern");
        String order = request.getParameter("order");
        JsonArray hotelArr;
        int orderInt = 1;

        city = escapeHtml(city);
        hotelPattern = escapeHtml(hotelPattern);
        order = escapeHtml(order);

        if(order!=null && !order.trim().isEmpty() && Integer.parseInt(order)== 0) {
            orderInt = 0;
        }
        try {
            if(hotelPattern == null && city == null)
                hotelArr = dbHandler.getHotelsMapData();
            else {
                hotelPattern = hotelPattern == null ? "" : escapeHtml(hotelPattern);
                city = city == null ? "" : escapeHtml(city);
                hotelArr = dbHandler.getHotelsData(city,hotelPattern,orderInt);
            }
            if(hotelArr == null || hotelArr.size() == 0){
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            else{
                PrintWriter out = response.getWriter();

                JsonObject resObj = new JsonObject();
                resObj.addProperty("success",true);

                resObj.add("data", hotelArr);
                resObj.addProperty("length",hotelArr.size());
                out.println(resObj);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
            }
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }
}