package net.clients;


import dbservice.objects.Order;
import dbservice.objects.Parking;
import dbservice.services.DBService;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "WebSocketClientServlet", urlPatterns = {"/client"})
public class WebSocketClientServlet extends WebSocketServlet {
    private final static int LOGOUT_TIME = 10 * 60 * 1000;
    private final ClientService clientService;

    public WebSocketClientServlet(DBService<Parking> parkingDBService, DBService<Order> orderDBService) {
        this.clientService = new ClientService(parkingDBService,orderDBService);
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(LOGOUT_TIME);
        factory.setCreator((req, resp) -> new ClientWebSocket(clientService));
    }
}
