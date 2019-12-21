package controller;

import com.google.gson.JsonObject;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import utilities.DatabaseHandler;
import utilities.Status;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Provides base functionality to all servlets
 */
public class BaseServlet extends HttpServlet {

	protected static Logger log = LogManager.getLogger();

	// Using getInstance() custom getter for Singleton, creating object
	// via constructor would have created another instance
	protected static final DatabaseHandler dbHandler = DatabaseHandler.getInstance();
	protected HttpSession session;
	protected VelocityEngine ve;
	protected VelocityContext context;
	protected final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected Date currentDateTime;

	/**
	 * Checks to see if a String is null or empty.
	 *
	 * @param text - String to check
	 * @return true if non-null and non-empty
	 */
	protected static boolean isBlank(String text) {
		return (text == null) || text.trim().isEmpty();
	}

	protected void finishResponse(HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();
			writer.printf("%n");
			writer.printf("<p style=\"font-size: 10pt; font-style: italic;\">");
			writer.printf("Last updated at %s.", getDate());
			writer.printf("</p>%n%n");
			writer.printf("</body>%n");
			writer.printf("</html>%n");
			writer.flush();
			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();
		}
		catch (IOException ex) {
			log.warn("Unable to finish HTTP response.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}

	protected JsonObject invalidKeyName(String keyname){
		JsonObject errObj = new JsonObject();
		errObj.addProperty("success",false);
		errObj.addProperty("message","Invalid "+keyname);
		return errObj;
	}

	protected JsonObject jsonResponseObject(boolean status, String message){
		JsonObject resObj = new JsonObject();
		resObj.addProperty("success",status);
		resObj.addProperty("message",message);
		return resObj;
	}

	protected String getDate() {
		String format = "hh:mm a 'on' EEE, MMM dd, yyyy";
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	protected Map<String, String> getCookieMap(HttpServletRequest request) {
		HashMap<String, String> map = new HashMap<>();
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie.getValue());
			}
		}
		return map;
	}

	protected void clearCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			return;
		}
		for(Cookie cookie : cookies) {
			cookie.setValue("");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

	protected void clearCookie(String cookieName, HttpServletResponse response) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	protected void debugCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if(cookies == null) {
			log.info("Saved Cookies: []");
		}
		else {
			String[] names = new String[cookies.length];

			for(int i = 0; i < names.length; i++) {
				names[i] = String.format("(%s, %s, %d)",
						cookies[i].getName(),
						cookies[i].getValue(),
						cookies[i].getMaxAge());
			}
			log.info("Saved Cookies: " + Arrays.toString(names));
		}
	}

	protected String getStatusMessage(String errorName) {
		Status status = null;
		try {
			status = Status.valueOf(errorName);
		}
		catch (Exception ex) {
			log.debug(errorName, ex);
			status = Status.ERROR;
		}
		return status.toString();
	}

    /**
     * Here, code is ordinal
	 * @param code ordinal of Status enum
	 * @return
     */
	protected String getStatusMessage(int code) {
		Status status = null;

		try {
			status = Status.values()[code];
		}
		catch (Exception ex) {
			log.debug(ex.getMessage(), ex);
			status = Status.ERROR;
		}
		return status.toString();
	}

    /**
     * Here, error is ordinal
	 * @param error Error to be obtained from error ordinal
	 * @return
     */
	protected String getStatusMessageForView(String error) {
		int code = 0; // ordinal 0 is for OK in Status enum
		try {
			code = Integer.parseInt(error);
		}
		catch (Exception ex) {
			code = -1;
		}
		String errorMessage = getStatusMessage(code);
		return errorMessage;
	}

	protected void doInitialChecks(HttpServletRequest request, HttpServletResponse response) throws IOException {
		checkURIForFavIco(request,response);
		checkSession(request,response);
	}

	protected void checkURIForFavIco(HttpServletRequest request, HttpServletResponse response) throws IOException {

		log.info( " - " + request.getServletPath());
		if (request.getRequestURI().endsWith("favicon.ico")) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			log.debug("favicon.ico before endpoint : "+ request.getRequestURI());
			return;
		}
	}
	protected void initializeVelocity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		context = new VelocityContext();
		context.put("name", session.getAttribute("username"));
		String lastLoginTime = (String) session.getAttribute("last_login_time");
		if(lastLoginTime== null){
			context.put("last_login_time", "N/A (Logged in first time)");
		}
		else {
			String lastLoginTimePrint = (String) session.getAttribute("last_login_time_print");
			context.put("last_login_time", lastLoginTimePrint);
		}
	}

	protected void checkSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
		session = request.getSession();
		if (session.getAttribute("username") == null) {
			response.sendRedirect("/unauthorized");
		}
	}
	protected String getCurrentDateTime(){
		currentDateTime = new Date();
		return dateFormat.format(currentDateTime);
	}

	protected String escapeHtml(String keyValue){
		if(keyValue != null)
			return StringEscapeUtils.escapeHtml4(keyValue).trim();
		else
			return null;
	}
}