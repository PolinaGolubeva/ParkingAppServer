import dbservice.objects.Coordinates;
import dbservice.objects.Order;
import dbservice.objects.Parking;
import dbservice.services.DBService;
import dbservice.services.OrderServiceImpl;
import dbservice.services.ParkingServiceImpl;
import net.main.AndroidServer;
import net.main.ParkingServer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Locale;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        //Connection connection = ParkingServiceImpl.getConnection();
        //DBService<Parking> parkingDBService = new ParkingServiceImpl(connection);
        //DBService<Order> orderDBService = new OrderServiceImpl(connection);
        DBService<Parking> parkingDBService = new TestParkingService();
        DBService<Order> orderDBService = new TestOrderService();
        parkingDBService.cleanUp();
        orderDBService.cleanUp();
        parkingDBService.createTable();
        for (Parking p: parkingDBService.getAll()) {
            System.out.println(p.toString());
        }
        orderDBService.createTable();
        readTable("./parkings/ParkingBook.txt",parkingDBService);
        ParkingServer parkingServer = new ParkingServer(parkingDBService, orderDBService);
        parkingServer.init();
        AndroidServer androidServer = new AndroidServer(parkingDBService, orderDBService);
        androidServer.init();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = bufferedReader.readLine();
            System.out.println(line);
            System.out.println(line.toLowerCase().trim().equals("stop"));
            if (line != null)
                if (line.toLowerCase().trim().equals("stop")) {
                    parkingServer.stop();
                    androidServer.stop();
                    break;
                }
        }
    }


    public static void readTable(String filename, DBService<Parking> parkingDBService) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename, Charset.forName("UTF-8")));
            for (int i = 0; i < 10; i++) {
                String line = reader.readLine();
                String subs[] = line.split("\\|");
                double lat = Double.parseDouble(subs[1]);
                double lon = Double.parseDouble(subs[2]);
                Coordinates coordinates = new Coordinates(lat, lon);
                String address = subs[3].trim();
                address += "|" + subs[4] + "|" + subs[5] + "|" + subs[6] + "|" + subs[7] + "|" + "tariffPlane";
                int av = Integer.parseInt(subs[8]);
                int cap = Integer.parseInt(subs[9]);
                Parking parking = new Parking(null, coordinates, address, cap, av);
                parkingDBService.insert(parking);
                System.out.println(parking.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
