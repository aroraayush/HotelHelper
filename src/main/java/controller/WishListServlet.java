package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.velocity.Template;
import utilities.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class represents a servlet that handles the hotels added to wish list by the user
 */
public class WishListServlet extends BaseServlet {
    /**
     *  This gets the request object and shows the wishlist page
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);
        initializeVelocity(request,response);

        // Getting query params
        String hotelId = request.getParameter("hotel_id");
        hotelId = escapeHtml(hotelId);
        try {
            PrintWriter out = response.getWriter();
            Integer userId = (Integer) session.getAttribute("userId");
            JsonArray wishList = dbHandler.getHotelsWishList(userId,hotelId);
            if(hotelId == null){
                context.put("title", "Your Hotels Wishlist !!");
                context.put("wishList", wishList);
                context.put("hotelId", hotelId);
                Template template = ve.getTemplate("src/main/webapp/view/hotel/wishlist.vm");
                StringWriter writer = new StringWriter();
                template.merge(context, writer);
                out.println(writer.toString());
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else{
                if(wishList.size()>0){
                    JsonObject resObj = new JsonObject();
                    resObj.addProperty("success",true);
                    resObj.add("data", wishList);
                    out.println(resObj);
                    response.setStatus(HttpServletResponse.SC_OK);
                }
                else {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
            }
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }

    /**
     * This inserts/updates wishlist for a hotel, user relation
     * @param request http request hotelId and its status
     * @param response http response
     * @throws IOException if error in processing request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);

        Status status = Status.ERROR;

        String hotelId = request.getParameter("hotel_id");
        String updateStatus = request.getParameter("status");
        updateStatus = escapeHtml(updateStatus);
        hotelId = escapeHtml(hotelId);
        try {
            PrintWriter out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if(updateStatus != null && hotelId != null){
                int updateStatusInt = Integer.parseInt(updateStatus);
                int hotelIdInt = Integer.parseInt(hotelId);
                Integer userId = (Integer) session.getAttribute("userId");
                status = dbHandler.insertUpdateWishList(updateStatusInt,hotelIdInt,userId);
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
}