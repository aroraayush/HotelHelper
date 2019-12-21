package controller;

import com.google.gson.JsonObject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import utilities.Status;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles login requests.
 */
public class LoginServlet extends BaseServlet {
	/**
	 *  This gets the request object and opens login page
	 * @param request http request object
	 * @param request http response object
	 * @throws IOException if error in processing request
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		session = request.getSession();
		if (session.getAttribute("username") != null) {
			response.sendRedirect("/home");
		}
		initializeVelocity(request,response);
		String newUser = request.getParameter("newuser");
		String logout = request.getParameter("logout");
		String error = request.getParameter("error");
		newUser = escapeHtml(newUser);
		logout = escapeHtml(logout);
		error = escapeHtml(error);

		PrintWriter out = response.getWriter();
		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();
		context.put("title", "Login");
		context.put("importNav", "false");
		if (newUser != null) {
			context.put("newUser", newUser);
		}
		else if (error != null) {
			context.put("error", getStatusMessageForView(error));
		}
		else if (logout != null) {
			context.put("logout", true);
		}
		Template template = ve.getTemplate("src/main/webapp/view/user/login.vm");
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		out.println(writer.toString());
	}

	/**
	 *  This gets the request object and performs login in backend
	 * @param request http request object
	 * @param request http response object
	 * @throws IOException if error in processing request
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		Status status = Status.ERROR;

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		if(isBlank(username)){
			out.println(invalidKeyName("username"));
		}
		else if(isBlank(password)){
			out.println(invalidKeyName("password"));
		}
		else{
			username = escapeHtml(username);
			password = escapeHtml(password);
			status = dbHandler.authenticateUser(username, password);

		}
		try {
			if (status == Status.OK) {
				// should eventually change this to something more secure
				String lastLoginTime = null;
				JsonObject userObj = dbHandler.getUser(username);
				response.addCookie(new Cookie("login", "true"));
				response.addCookie(new Cookie("name", username));
				session = request.getSession();
				session.setAttribute("username",username);
				int userId = userObj.get("user_id").getAsInt();
				session.setAttribute("userId",userId);
				if(userObj.get("current_login_time") != null){
					lastLoginTime = userObj.get("current_login_time").getAsString();
					session.setAttribute("last_login_time",lastLoginTime);

					SimpleDateFormat sdfo = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
					Date d1 = sdfo.parse(lastLoginTime);
					DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
					session.setAttribute("last_login_time_print",dateFormat.format(d1));
				}
				String currentLoginTime = getCurrentDateTime();
				session.setAttribute("current_login_time",currentLoginTime);
				dbHandler.updateLoginTime(lastLoginTime, currentLoginTime);
				response.sendRedirect(response.encodeRedirectURL("/home"));
			}
			else {
				response.addCookie(new Cookie("login", "false"));
				response.addCookie(new Cookie("name", ""));
				response.setContentType("application/json");
				response.sendRedirect(response.encodeRedirectURL("/login?error=" + status.ordinal()));
			}
		}
		catch (Exception ex) {
			log.error("  " +request.getRequestURI());
		}
	}
}