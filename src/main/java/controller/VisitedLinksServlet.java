package controller;

import com.google.gson.JsonArray;
import org.apache.velocity.Template;
import utilities.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class represents a servlet that handles the user visit to expedia links
 */
public class VisitedLinksServlet extends BaseServlet {

    /**
     *  This gets the request object and returns Visited Links page
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);
        initializeVelocity(request,response);

        // Getting query params
        try {
            PrintWriter out = response.getWriter();
            session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            JsonArray visitedLinks = dbHandler.getUserVisitedLinks(userId);
            context.put("title", "Your Visited Links !!");
            context.put("visitedLinks", visitedLinks);
            Template template = ve.getTemplate("src/main/webapp/view/hotel/visited_links.vm");
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
     *  This gets the request object and inserts visited links of a user
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);

        // Getting query params
        String hotelId = request.getParameter("hotel_id");
        String link = request.getParameter("link");
        hotelId = escapeHtml(hotelId);
        link = escapeHtml(link);
        Status status = Status.ERROR;
        try {
            PrintWriter out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if(hotelId != null && link != null){
                int hotelIdInt = Integer.parseInt(hotelId);
                Integer userId = (Integer) session.getAttribute("userId");
                status = dbHandler.insertVisitedLinks(link,hotelIdInt,userId);
            }
            if (status == Status.OK) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }

    /**
     *  This gets the request object and clears visited links of a user
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);

        // Getting query params
        Status status = Status.ERROR;
        try {
            PrintWriter out = response.getWriter();
            Integer userId = (Integer) session.getAttribute("userId");
            status = dbHandler.deleteVisitedLinks(userId);
            if (status == Status.OK) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }
}