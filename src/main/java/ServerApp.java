import dbservice.objects.Order;
import dbservice.objects.Parking;
import dbservice.services.DBService;
import dbservice.services.ParkingServiceImpl;
import net.main.AndroidServer;
import net.main.ParkingServer;

import java.sql.Connection;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        //Connection connection = ParkingServiceImpl.getConnection();
        //DBService<Parking> parkingDBService = new ParkingServiceImpl(connection);
        DBService<Parking> parkingDBService = new TestParkingService();
        DBService<Order> orderDBService = new TestOrderService();
        parkingDBService.createTable();
        orderDBService.createTable();
        ParkingServer parkingServer = new ParkingServer(parkingDBService);
        parkingServer.init();
        AndroidServer androidServer = new AndroidServer(parkingDBService, orderDBService);
        androidServer.init();
    }
}
