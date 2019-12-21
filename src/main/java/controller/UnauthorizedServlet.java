package controller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This servlet shows the unauthorized page if session expired
 */
public class UnauthorizedServlet extends BaseServlet {

	/**
	 *  This gets the request object and shows the unauthorized page
	 * @param request http request object
	 * @param request http response object
	 * @throws IOException if error in processing request
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
		VelocityContext context = new VelocityContext();
		context.put("title", "Unauthorized - Login Again");
		context.put("importNav", "false");
		Template template = ve.getTemplate("src/main/webapp/view/user/unauthorized.vm");
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		out.println(writer.toString());
	}
}