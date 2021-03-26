package net.parking;

import dbservice.objects.Order;
import dbservice.objects.Parking;
import dbservice.services.DBService;
import exceptions.ModelException;
import net.notifiers.Listener;
import net.utils.MessageGenerator;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingService implements Listener<Order>{
    private DBService<Parking> parkingDBService;
    private DBService<Order> orderDBService;
    private Set<ParkingWebSocket> webSockets;

    public ParkingService(DBService<Parking> parkingDBService, DBService<Order> orderDBService) {
        this.parkingDBService = parkingDBService;
        this.orderDBService = orderDBService;
        this.webSockets = Collections.newSetFromMap(new ConcurrentHashMap<>());
        orderDBService.getManager().subscribe(this);
    }

    public void sendMessage(Long id, String data) {
        for (ParkingWebSocket user : webSockets) {
            try {
                if (user.getId().equals(id))
                    user.sendString(data);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void add(ParkingWebSocket webSocket) {
        webSockets.add(webSocket);
    }

    public void remove(ParkingWebSocket webSocket) {
        webSockets.remove(webSocket);
    }

    public DBService<Parking> getDBService() { return parkingDBService; }

    @Override
    public void update(String message) {
        Order order = Order.fromJson(message);
        Long parkingId = order.getParkingId();
        Parking parking = parkingDBService.get(parkingId);
        try {
            parking.addCar();
        } catch (ModelException e) {
            e.printStackTrace();
        }
        parkingDBService.update(parking);
        System.out.println("Parking " + parking.getId() + " updated");
        for (ParkingWebSocket p: webSockets) {
            if (p.getId().equals(parkingId)) {
                String data = MessageGenerator.SEND_ORDER + message;
                p.sendString(data);
                data = MessageGenerator.SEND_PARKING + parking.toString();
                p.sendString(data);
                break;
            }
        }
    }
}