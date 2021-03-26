package dbservice.services;

import dbservice.dao.OrderDAO;
import dbservice.exceptions.DBException;
import exceptions.ModelException;
import dbservice.objects.Order;
import net.notifiers.Manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderServiceImpl implements DBService<Order> {
    private Connection connection;
    private Manager<Order> manager;

    public OrderServiceImpl(Connection connection) {
        this.connection = connection;
        this.manager = new Manager<Order>();
    }

    public void createTable() {
        OrderDAO dao = new OrderDAO(connection);
        try {
            dao.createTable();
            System.out.println("Table created");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Long insert(Order order) {
        OrderDAO dao = new OrderDAO(connection);
        try {
            dao.createTable();
            Long id = dao.insert(order);
            manager.updateAll(order.toString());
            return id;
        } catch (SQLException | ModelException | DBException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {}
            e.printStackTrace();
        }
        return null;
    }

    public Order get(long id) {
        try {
            return (new OrderDAO(connection).get(id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Order> getAll() {
        try {
            return (new OrderDAO(connection).getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long update(Order order) {
        try {
            manager.updateAll(order.toString());
            return (new OrderDAO(connection).update(order));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(long id) {
        try {
            new OrderDAO(connection).delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Order order) {
        OrderDAO dao = new OrderDAO(connection);
        long id = -1;
        try {
            id = dao.getId(order);
            dao.delete(id);
        } catch (SQLException | DBException e) {
            e.printStackTrace();
        }
    }

    public void cleanUp() {
        try {
            new OrderDAO(connection).dropTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Manager<Order> getManager() {
        return this.manager;
    }

}
