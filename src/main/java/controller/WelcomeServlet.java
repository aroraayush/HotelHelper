package controller;

import org.apache.velocity.Template;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This shows a page that allows to search hotels
 */
@SuppressWarnings("serial")
public class WelcomeServlet extends BaseServlet {

	/**
	 *  This gets the request object and shows the home page
	 * @param request http request object
	 * @param request http response object
	 * @throws IOException if error in processing request
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		doInitialChecks(request,response);
		initializeVelocity(request,response);

		PrintWriter out = response.getWriter();
		context.put("title", "Welcome to TravelHelper !!");
		context.put("navSearch", false);
		context.put("importMapsAPI", true);
		Template template = ve.getTemplate("src/main/webapp/view/home.vm");
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		out.println(writer.toString());
	}
}