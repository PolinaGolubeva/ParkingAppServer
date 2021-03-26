package net.parking;

import dbservice.objects.Coordinates;
import dbservice.objects.Parking;
import dbservice.services.DBService;
import exceptions.ModelException;
import net.utils.MessageGenerator;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@SuppressWarnings("UnusedDeclaration")
@WebSocket
public class ParkingWebSocket {
    private Long id;
    private ParkingService parkingService;
    private Session session;

    public ParkingWebSocket(ParkingService parkingService) {
        this.id = null;
        this.parkingService = parkingService;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        System.out.println("Connection with parking opened");
        parkingService.add(this);
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        System.out.println("Message from parking received: " + data);
        String message = "";
        if (data.startsWith(MessageGenerator.GET_ID)) {
            getParkingId(data);
        }
        if (data.startsWith(MessageGenerator.ADD_CAR)) {
            add(data);
        }
        if (data.startsWith(MessageGenerator.REMOVE_CAR)) {
            remove(data);
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        parkingService.remove(this);
    }

    public void sendString(String data) {
        try {
            session.getRemote().sendString(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Long getId() { return id; }

    public void setId(Long id) {
        if (this.id == null)
            this.id = id;
    }

    public void getParkingId(String data) {
        Long id = Long.parseLong(data.replace(MessageGenerator.GET_ID, ""));
        this.setId(id);
        DBService<Parking> pService = parkingService.getDBService();
        Parking parking = pService.get(id);
        String message = MessageGenerator.SEND_PARKING + parking.toString();
        parkingService.sendMessage(id, message);
    }

    public void add(String data) {
        Long id = Long.parseLong(data.replace(MessageGenerator.ADD_CAR, ""));
        if (this.id != null && this.id == id) {
            System.out.println("Received add message:" + data);
            DBService<Parking> pService = parkingService.getDBService();
            Parking parking = pService.get(id);
            try {
                parking.addCar();
            } catch (ModelException e) {
                String msg = MessageGenerator.ERROR + "Cannot add car: parking is full";
                parkingService.sendMessage(id, msg);
            }
            if (pService.update(parking) == null) {
                String msg = MessageGenerator.ERROR + "Error while updating parking";
                parkingService.sendMessage(id, msg);
            } else {
                System.out.println("Parking updated successfully");
            }
        }
    }

    public void remove(String data) {
        Long id = Long.parseLong(data.replace(MessageGenerator.REMOVE_CAR, ""));
        if (this.id != null && this.id == id) {
            System.out.println("Received remove message:" + data);
            DBService<Parking> pService = parkingService.getDBService();
            Parking parking = pService.get(id);
            try {
                parking.removeCar();
            } catch (ModelException e) {
                String msg = MessageGenerator.ERROR + "Cannot remove car: parking is empty";
                parkingService.sendMessage(id, msg);
            }
            if (pService.update(parking) == null) {
                String msg = MessageGenerator.ERROR + "Error while updating parking";
                parkingService.sendMessage(id, msg);
            }
        }
    }
}
