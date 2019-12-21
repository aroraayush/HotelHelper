package controller;

import com.google.gson.*;
import org.apache.commons.text.StringEscapeUtils;
import utilities.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * This class represents a servlet that triggers at endpoint /reviews and do reviews related operations
 */
public class HotelReviewServlet extends BaseServlet {

    /**
     *  This gets the request object and returns reviews of a hotel
     * @param request http request object
     * @param response http response object
     * @throws IOException if error in processing request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);

        // Getting query params
        String hotelId = request.getParameter("hotel_id");
        String reviewId = request.getParameter("review_id");
        try {
            PrintWriter out = response.getWriter();
            if(reviewId == null){
                JsonArray hotelArr = dbHandler.getReviewsData(hotelId);
                if(hotelArr == null || hotelArr.size() == 0){
                    JsonObject resObj = new JsonObject();
                    resObj.addProperty("success",false);
                    out.println(resObj);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                }
                else{
                    JsonObject resObj = new JsonObject();
                    resObj.addProperty("success",true);
                    resObj.add("data", hotelArr);
                    resObj.addProperty("length",hotelArr.size());
                    out.println(resObj);
                    response.setStatus(HttpServletResponse.SC_OK);
                }
                response.setContentType("application/json");
            }
            else{
                int reviewIdInt = -1;
                int userId = (int) session.getAttribute("userId");
                reviewIdInt = dbHandler.getReviewId(hotelId,userId);
                JsonObject resObj = new JsonObject();
                if(reviewIdInt == -1){
                    resObj.addProperty("success",false);
                }
                else {
                    resObj.addProperty("success",true);
                    resObj.addProperty("data", reviewIdInt);
                }
                response.setContentType("application/json");
                out.println(resObj);
            }
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }

    /**
     *  This gets the request object and returns reviews of a hotel
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);

        // Getting body params
        String hotelId = request.getParameter("hotel_id");
        String rating = request.getParameter("rating");
        String title = request.getParameter("title");
        String reviewText = request.getParameter("review_text");
        String isRecommended = request.getParameter("is_recommended");
        String date = request.getParameter("date");

        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            if(hotelId == null || hotelId.trim().isEmpty())
                out.println(invalidKeyName("hotelId"));
            else if(rating == null || rating.trim().isEmpty())
                out.println(invalidKeyName("rating"));
            else if(title == null || title.trim().isEmpty())
                out.println(invalidKeyName("title"));
            else if(reviewText == null || reviewText.trim().isEmpty())
                out.println(invalidKeyName("reviewText"));
            else if(isRecommended == null || isRecommended.trim().isEmpty())
                out.println(invalidKeyName("isRecommended"));
            else if(date == null || date.trim().isEmpty())
                out.println(invalidKeyName("date"));
            else {
                Integer userId = (Integer) session.getAttribute("userId");
                hotelId = escapeHtml(hotelId).trim();
                rating = escapeHtml(rating).trim();
                title = escapeHtml(title).trim();
                reviewText = escapeHtml(reviewText).trim();
                isRecommended = escapeHtml(isRecommended).trim();
                date = escapeHtml(date).trim();
                int ratingInt = Integer.parseInt(rating);
                int hotelIdInt = Integer.parseInt(hotelId);
                int isRecommendedInt = Integer.parseInt(isRecommended);

                int insertId = dbHandler.insertReview(hotelIdInt, ratingInt, title, reviewText, isRecommendedInt, userId,date);
                if (insertId > 0) {
                    JsonObject resObj = new JsonObject();
                    resObj.addProperty("success",true);
                    resObj.add("insert_id", new JsonPrimitive(insertId));
                    out.println(resObj);
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
            response.setContentType("application/json");
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }

    /**
     *  This gets the request object and updates a review
     * @param request http request object
     * @param response http response object
     * @throws IOException if error in processing request
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);
        // Getting body params
        String reviewId = request.getParameter("id");
        String rating = request.getParameter("rating");
        String title = request.getParameter("title");
        String reviewText = request.getParameter("review_text");
        String isRecommended = request.getParameter("is_recommended");
        String date = request.getParameter("date");

        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            if(reviewId == null || reviewId.trim().isEmpty())
                out.println(invalidKeyName("reviewId"));
            else if(rating == null || rating.trim().isEmpty())
                out.println(invalidKeyName("rating"));
            else if(title == null || title.trim().isEmpty())
                out.println(invalidKeyName("title"));
            else if(reviewText == null || reviewText.trim().isEmpty())
                out.println(invalidKeyName("reviewText"));
            else if(isRecommended == null || isRecommended.trim().isEmpty())
                out.println(invalidKeyName("isRecommended"));
            else if(date == null || date.trim().isEmpty())
                out.println(invalidKeyName("date"));
            else {
                session = request.getSession();

                Integer userId = (Integer) session.getAttribute("userId");

                reviewId = escapeHtml(reviewId).trim();
                rating = escapeHtml(rating).trim();
                title = escapeHtml(title).trim();
                reviewText = escapeHtml(reviewText).trim();
                isRecommended = escapeHtml(isRecommended).trim();
                date = escapeHtml(date).trim();
                int ratingInt = Integer.parseInt(rating);
                int reviewIdInt = Integer.parseInt(reviewId);
                int isRecommendedInt = Integer.parseInt(isRecommended);

                Status status = dbHandler.updateReview(reviewIdInt, ratingInt, title, reviewText, isRecommendedInt, userId,date);
                if (status == Status.OK) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
            }
            response.setContentType("application/json");
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }

    /**
     *  This gets the request object and deletes a review
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        doInitialChecks(request,response);
        // Getting body params
        String reviewId = request.getParameter("id");
        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            if(reviewId == null || reviewId.trim().isEmpty())
                out.println(invalidKeyName("reviewId"));
            else {
                session = request.getSession();
                Integer userId = (Integer) session.getAttribute("userId");
                reviewId = escapeHtml(reviewId).trim();
                int reviewIdInt = Integer.parseInt(reviewId);

                Status status = dbHandler.deleteReview(reviewIdInt, userId);
                if (status == Status.OK) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
            }
            response.setContentType("application/json");
        }
        catch (Exception e){
            log.error("  " +request.getRequestURI() + e);
        }
    }
}