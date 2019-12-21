package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Redirects to home page or unauthorized page depending on whether user
 * session is detected.
 */
@SuppressWarnings("serial")
public class RedirectServlet extends BaseServlet {

	/**
	 *  This gets the request object and updates the like unlike status of a hotel
	 * @param request http request object
	 * @param request http response object
	 * @throws IOException if error in processing request
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		session = request.getSession();
		if (session.getAttribute("username") != null) {
			response.sendRedirect("/home");
		}
		else {
			response.sendRedirect("/login");
		}
	}

	/**
	 *  This gets the request object and updates the like unlike status of a hotel
	 * @param request http request object
	 * @param request http response object
	 * @throws IOException if error in processing request
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
}