package net.main;

import dbservice.objects.Order;
import dbservice.objects.Parking;
import dbservice.services.DBService;
import net.parking.WebSocketParkingServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ParkingServer implements Runnable {
    private DBService<Parking> parkingDBService;
    private DBService<Order> orderDBService;
    private Server server;

    public ParkingServer(DBService<Parking> parkingDBService, DBService<Order> orderDBService) {
        this.parkingDBService = parkingDBService;
        this.orderDBService = orderDBService;
    }

    public void init() throws Exception {
        server = new Server(8081);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(new WebSocketParkingServlet(this.parkingDBService,
                this.orderDBService)), "/parking");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("public_html");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});
        server.setHandler(handlers);

        server.start();
        System.out.println("Server started");
        //server.join();
        server.setStopAtShutdown(true);
    }

    @Override
    public void run() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() throws Exception {
        for (Handler handler: server.getHandlers())
            handler.stop();
        server.stop();
        System.out.println("Parking server stopped");
    }
}
