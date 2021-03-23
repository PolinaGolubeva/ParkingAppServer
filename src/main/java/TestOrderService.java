import dbservice.objects.Order;
import dbservice.services.DBService;
import exceptions.ModelException;
import net.notifiers.Manager;

import java.util.ArrayList;
import java.util.List;

public class TestOrderService implements DBService<Order> {
    private List<Order> orderList;
    private Manager<Order> manager;

    public TestOrderService() {
        this.manager = new Manager<>();
    }

    @Override
    public void createTable() {
        this.orderList = new ArrayList<Order>();
    }

    @Override
    public Long insert(Order obj) {
        orderList.add(obj);
        try {
            obj.setId((long) (orderList.size() - 1));
        } catch (ModelException e) {
            e.printStackTrace();
        }
        return obj.getId();
    }

    @Override
    public Order get(long id) {
        return orderList.get((int) id);
    }

    @Override
    public List<Order> getAll() {
        return orderList;
    }

    @Override
    public Long update(Order obj) {
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getId().equals(obj.getId())) {
                orderList.remove(i);
                orderList.add(i, obj);
                manager.updateAll(obj.toString());
                return obj.getId();
            }
        }
        return null;
    }

    @Override
    public void delete(long id) {
        orderList.remove(id);
    }

    @Override
    public void delete(Order obj) {
        orderList.remove(obj);
    }

    @Override
    public void cleanUp() {
        orderList = null;
    }

    @Override
    public Manager<Order> getManager() {
        return this.manager;
    }
}
