package net.clients;

import dbservice.objects.Parking;
import dbservice.services.DBService;
import net.notifiers.Listener;
import net.utils.MessageGenerator;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientService implements Listener<Parking> {
    private DBService<Parking> parkingDBService;
    private Set<ClientWebSocket> webSockets;

    public ClientService(DBService<Parking> parkingDBService) {
        this.parkingDBService = parkingDBService;
        this.webSockets = Collections.newSetFromMap(new ConcurrentHashMap<>());
        parkingDBService.getManager().subscribe(this);
    }

    public void sendMessage(String data, ClientWebSocket socket) {
            try {
                socket.sendString(data);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
    }

    public void add(ClientWebSocket webSocket) {
        webSockets.add(webSocket);
    }

    public void remove(ClientWebSocket webSocket) {
        webSockets.remove(webSocket);
    }

    public DBService<Parking> getDBService() { return parkingDBService; }

    @Override
    public void update(String message) {
        String resp = MessageGenerator.SEND_PARKING + message;
        for (ClientWebSocket webSocket: webSockets) {
            try {
                webSocket.sendString(resp);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
