package controller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import utilities.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This servelet registers a new user in DB
 */
public class RegisterServlet extends BaseServlet {

    /**
     * This gets the request object and shows register page
     * @param request  http request object
     * @param response http response object
     * @throws IOException if error in processing request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            PrintWriter out = response.getWriter();
            String error = request.getParameter("error");
            String newUser = request.getParameter("newuser");
            error = escapeHtml(error);
            newUser = escapeHtml(newUser);

            VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
            VelocityContext context = new VelocityContext();
            context.put("title", "Register");
            context.put("importNav", "false");

            if (error != null) {
                context.put("error", getStatusMessageForView(error));
            }
            else if (newUser != null) {
                context.put("newUser", newUser);
            }
            Template template = ve.getTemplate("src/main/webapp/view/user/register.vm");
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            out.println(writer.toString());
        } catch (Exception e) {
            log.error("  " +request.getRequestURI());
        }
    }

    /**
     *  This gets the request object and register user with data from request object
     * @param request http request object
     * @param request http response object
     * @throws IOException if error in processing request
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        PrintWriter out = response.getWriter();
        Status status = Status.ERROR;

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String repPassword = request.getParameter("repeat-password");

        if(isBlank(username)){
            out.println(invalidKeyName("username"));
        }
        else if(isBlank(password)){
            out.println(invalidKeyName("password"));
        }
        else
            if(!password.trim().equals(repPassword.trim())){
            out.println(jsonResponseObject(false,"Passords don't match."));
            status = Status.PWD_NOT_MATCH;
        }
        else {
            Pattern p = Pattern.compile("(?=.+\\d)(?=.+[a-zA-Z])(?=.+[@#$%]).{5,10}");
            Matcher m = p.matcher(password);
            if(m.matches()){
                username = escapeHtml(username);
                password = escapeHtml(password);
                status = dbHandler.registerUser(username, password);
            }
            else {
                status = Status.PWD_PATTERN_MISMATCH;
            }
        }
        if (status == Status.OK) {
            response.sendRedirect(response.encodeRedirectURL("/register?newuser="+username));
        }
        else {
            response.sendRedirect(response.encodeRedirectURL("/register?error=" + status.ordinal()));
        }
    }
}
