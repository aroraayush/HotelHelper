package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlets logs out the user, removes data from session and clears cookies if stored
 */
public class LogoutServlet extends BaseServlet {
    /**
     *  This gets the request object and clears the user data in session
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        session = request.getSession();
        if (session.getAttribute("username") != null) {
            session.setAttribute("username",null);
            clearCookies(request,response);
            response.sendRedirect("/login?logout=true");
        }
        else {
            response.sendRedirect("/login");
        }
    }
}
