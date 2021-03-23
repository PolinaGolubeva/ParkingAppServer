package net.clients;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dbservice.objects.Order;
import dbservice.objects.Parking;
import net.utils.MessageGenerator;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@WebSocket
public class ClientWebSocket {
    private ClientService clientService;
    private Session session;

    public ClientWebSocket(ClientService clientService) {
        this.clientService = clientService;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        System.out.println("Connection with client established");
        clientService.add(this);
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        System.out.println("Message received from client: " + data);
        if (data.startsWith(MessageGenerator.GET_ALL_PARKINGS)) {
            sendAllParkings();
        }
        if (data.startsWith(MessageGenerator.SEND_ORDER)) {
            getOrder(data);
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        clientService.remove(this);
    }

    public void sendString(String data) {
        try {
            session.getRemote().sendString(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendAllParkings() {
        List<Parking> pList = clientService.getParkingDBService().getAll();
        String msg = MessageGenerator.SEND_ALL_PARKINGS;
        String json = (new Gson().toJson(pList));
        msg += json;
        clientService.sendMessage(msg, this);
        System.out.println("All parkings sent: " + msg);
    }

    private void getOrder(String data) {
        String msg = data.replace(MessageGenerator.SEND_ORDER, "");
        try {
            Order order = Order.fromJson(msg);
            System.out.println("Order id:" + order.getId());
            String response = MessageGenerator.MESSAGE + "Order received";
            System.out.println(response + order.toString());
            clientService.getOrderDBService().insert(order);
        } catch (JsonParseException e) {
            String response = MessageGenerator.ERROR + "Error: order was not received, wrong format";
        } catch (NullPointerException e) {
            String response = MessageGenerator.ERROR + "Error: empty order";
        }
    }
}