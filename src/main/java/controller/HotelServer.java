package controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

/** This class uses Jetty & servlets to implement a server
 * This is the driver class for this TravelHelper server
 */
public class HotelServer {

    private int port;
    private static Logger log = LogManager.getLogger();

    /**
     * Constructor of JettyHotelServer
     * @param port  Port at which the server will run
     */
    public HotelServer(int port) {
        this.port = port;

    }

    public static void main (String[]args) throws Exception {
        HotelServer server = new HotelServer(8090);
        server.startServer();
    }

    /**
     * This maps several endpoints to different servlets and starts the Jetty Server
     * @throws Exception Exception of any type
     */
    private void startServer() throws Exception {
        try {
            Server server = new Server(port);
            ServletHandler handler = new ServletHandler();
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            mapUrlsToServlet(context);
            ResourceHandler resourceHandler = initiateResourceHandler(server,handler);

            // To map /files url path to the same resource handler
            ContextHandler ctx = new ContextHandler("/assets"); // url
            ctx.setHandler(resourceHandler);

            // initialize Velocity
            VelocityEngine velocity = new VelocityEngine();
            velocity.init();

            // set velocity as an attribute of the context so that we can access it
            // from servlets
            context.setContextPath("/");
            context.setAttribute("templateEngine", velocity);
            server.setHandler(context);

            // Setup handlers (and handler order)
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{resourceHandler, ctx, context});
            server.setHandler(handlers);

            log.info("Server started on port " + port + "...");

            try {
                server.start();
                server.join();
                log.info("Exiting...");
            } catch (Exception ex) {
                log.fatal("Interrupted while running server.", ex);
                System.exit(-1);
            }
        }
        catch (Exception ex){
            log.fatal("Interrupted while running server.", ex);
            System.exit(-1);
        }
    }

    private ResourceHandler initiateResourceHandler(Server server, ServletHandler handler) {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        server.setHandler(handler);
        server.setHandler(resourceHandler);
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("src/main/webapp/static/");
        return resourceHandler;
    }

    private void mapUrlsToServlet(ServletContextHandler context) {
        context.addServlet(RegisterServlet.class, "/register");
        context.addServlet(LoginServlet.class, "/login");
        context.addServlet(WelcomeServlet.class, "/home");
        context.addServlet(LogoutServlet.class, "/logout");

        context.addServlet(ReviewLikesServlet.class, "/likes");
        context.addServlet(HotelServlet.class, "/hotel");
        context.addServlet(WishListServlet.class, "/wishlist");
        context.addServlet(VisitedLinksServlet.class, "/visited");
        context.addServlet(HotelsServlet.class, "/hotels");
        context.addServlet(HotelCityServlet.class, "/cities");
        context.addServlet(HotelReviewServlet.class, "/reviews");
        context.addServlet(HotelAttractionsServlet.class, "/attractions");
        context.addServlet(UnauthorizedServlet.class, "/unauthorized");
        context.addServlet(RedirectServlet.class, "/*");
    }

}
