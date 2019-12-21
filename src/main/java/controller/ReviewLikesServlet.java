package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import utilities.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class represents a servlet that handles the liking and unliking of a hotel by a user
 */
public class ReviewLikesServlet extends BaseServlet {

    /**
     *  This gets the request object and updates the like unlike status of a review
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);

        Status status = Status.ERROR;
        // Getting query params
        String hotelId = request.getParameter("hotel_id");
        String like = request.getParameter("my_likes");
        hotelId = escapeHtml(hotelId);
        try {
            PrintWriter out = response.getWriter();
            if(like == null){
                Integer userId = (Integer) session.getAttribute("userId");
                JsonArray likesList = dbHandler.getReviewRecommends(hotelId,userId);
                if(likesList.size()>0){
                    JsonObject resObj = new JsonObject();
                    resObj.addProperty("success",true);
                    resObj.add("data", likesList);
                    out.println(resObj);
                    response.setStatus(HttpServletResponse.SC_OK);
                }
                else {
                    JsonObject resObj = new JsonObject();
                    resObj.addProperty("success",false);
                    out.println(resObj);
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
            else {
                session = request.getSession();
                Integer userId = (Integer) session.getAttribute("userId");
                JsonArray likesList = dbHandler.getUserLikesPerHotel(hotelId,userId);
                if(likesList.size()>0){
                    JsonObject resObj = new JsonObject();
                    resObj.addProperty("success",true);
                    resObj.add("data", likesList);
                    out.println(resObj);
                    response.setStatus(HttpServletResponse.SC_OK);
                }
                else {
                    JsonObject resObj = new JsonObject();
                    resObj.addProperty("success",false);
                    out.println(resObj);
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }

    /**
     *  This gets the request object and updates the like unlike status of a hotel
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);

        Status status = Status.ERROR;
        // Getting query params
        String reviewId = request.getParameter("review_id");
        String hotelId = request.getParameter("hotel_id");
        String updateStatus = request.getParameter("status");
        updateStatus = escapeHtml(updateStatus);
        reviewId = escapeHtml(reviewId);
        hotelId = escapeHtml(hotelId);
        try {
            PrintWriter out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if(updateStatus != null && reviewId != null){
                int updateStatusInt = Integer.parseInt(updateStatus);
                int reviewIdInt = Integer.parseInt(reviewId);
                int userId = (int) session.getAttribute("userId");
                status = dbHandler.updateLikeStatus(updateStatusInt,reviewIdInt,userId,hotelId);
            }
            if (status == Status.OK) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }
}