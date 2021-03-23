package dbservice.services;

import dbservice.dao.ParkingDAO;
import dbservice.exceptions.DBException;
import exceptions.ModelException;
import dbservice.objects.Coordinates;
import dbservice.objects.Parking;
import net.notifiers.Manager;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ParkingServiceImpl implements DBService<Parking> {
    private Connection connection;
    private Manager<Parking> manager;

    public ParkingServiceImpl(Connection connection) {
        this.connection = connection;
        printConnectInfo();
        this.manager = new Manager<Parking>();
    }

    public void createTable() {
        ParkingDAO dao = new ParkingDAO(connection);
        try {
            dao.createTable();
            System.out.println("Table created");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long insert(Parking parking) {
        try {
            ParkingDAO dao = new ParkingDAO(connection);
            dao.createTable();
            return dao.insert(parking);
        } catch (SQLException | DBException | ModelException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            e.printStackTrace();
        }
        return null;
    }

    public Parking get(long id) {
        try {
            return (new ParkingDAO(connection).get(id));
        } catch (SQLException | DBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Parking get(Coordinates coordinates) {
        try {
            Long id = new ParkingDAO(connection).getId(coordinates);
            return new ParkingDAO(connection).get(id);
        } catch (SQLException | DBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Parking> getAll() {
        try {
            return (new ParkingDAO(connection).getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long update(Parking parking) {
        try {
            Long id =  new ParkingDAO(connection).update(parking);
            getManager().updateAll(parking.toString());
            return id;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void delete(long id) {
        try {
            new ParkingDAO(connection).delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Parking parking) {
        try {
            ParkingDAO dao = new ParkingDAO(connection);
            long id = dao.getId(parking.getCoordinates());
            dao.delete(id);
        } catch (SQLException | DBException e) {
            e.printStackTrace();
        }
    }

    public void cleanUp() {
        try {
            new ParkingDAO(connection).dropTable();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Manager<Parking> getManager() {
        return this.manager;
    }

    public static Connection getConnection() {
        try {
            StringBuilder url = new StringBuilder();
            url.
                    append("jdbc:tarantool://").        //db type
                    append("localhost:").           //host name
                    append("3301/").                //port
                    append("user=test&").          //login
                    append("password=test");       //password
            System.out.println("URL: " + url + "\n");
            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void printConnectInfo() {
        try {
            System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
