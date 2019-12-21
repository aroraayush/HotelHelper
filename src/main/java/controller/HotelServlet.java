package controller;

import com.google.gson.*;
import org.apache.velocity.Template;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class represents a servlet that triggers at endpoint /hotel. It does operations related to a single hotel
 */
public class HotelServlet extends BaseServlet {

    /**
     *  This gets the request object and returns hotel page
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);
        initializeVelocity(request,response);

        // Getting query params
        String hotelId = request.getParameter("id");
        hotelId = escapeHtml(hotelId);
        try {
            PrintWriter out = response.getWriter();
            JsonObject hotelObj = dbHandler.getHotelData(hotelId);
            context.put("title", "Hotel - "+hotelObj.get("name"));
            context.put("importMapsAPI", true);
            context.put("hotelObj", hotelObj);
            context.put("hotelId", hotelId);
            Template template = ve.getTemplate("src/main/webapp/view/hotel/hotel.vm");
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
     * This gets the request object and returns data of a hotel
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);

        // Getting body params
        String hotelId = request.getParameter("id");

        try {
            JsonObject hotelObj = dbHandler.getHotelData(hotelId);
            if(hotelObj == null){
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            else{
                PrintWriter out = response.getWriter();

                JsonObject resObj = new JsonObject();
                resObj.addProperty("success",true);
                resObj.add("data", hotelObj);
                out.println(resObj);
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
            }
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }
}