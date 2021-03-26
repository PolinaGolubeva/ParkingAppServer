package net.parking;

import dbservice.objects.Order;
import dbservice.objects.Parking;
import dbservice.services.DBService;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "WebSocketParkingServlet", urlPatterns = {"/parking"})
public class WebSocketParkingServlet extends WebSocketServlet {
    private final static int LOGOUT_TIME = 10 * 60 * 1000;
    private final ParkingService parkingService;

    public WebSocketParkingServlet(DBService<Parking> parkingDBService, DBService<Order> orderDBService) {
        this.parkingService = new ParkingService(parkingDBService, orderDBService);
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(LOGOUT_TIME);
        factory.setCreator((req, resp) -> new ParkingWebSocket(parkingService));
    }
}