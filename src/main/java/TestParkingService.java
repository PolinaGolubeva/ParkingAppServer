import dbservice.objects.Coordinates;
import dbservice.objects.Parking;
import dbservice.services.DBService;
import exceptions.ModelException;
import net.notifiers.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestParkingService implements DBService<Parking> {
    private ArrayList<Parking> pList;
    private Manager<Parking> manager;

    public TestParkingService() {
        this.manager = new Manager<>();
    }

    @Override
    public void createTable() {
        pList = new ArrayList<Parking>();
        String info = "here comes address and cost information ";
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            Coordinates coords = new Coordinates(random.nextDouble() * 180,
                    random.nextDouble() * 180);
            int capacity = random.nextInt(50) + 50;
            int available = random.nextInt(capacity);
            Parking parking = new Parking(i, coords, info + i, capacity, available);
            pList.add(parking);
        }
    }

    @Override
    public Long insert(Parking obj) {
        pList.add(obj);
        try {
            obj.setId((long) (pList.size() - 1));
            manager.updateAll(obj.toString());
            return obj.getId();
        } catch (ModelException e) {
            //e.printStackTrace();
        }
        return null;
    }

    @Override
    public Parking get(long id) {
        return pList.get((int) id);
    }

    @Override
    public List<Parking> getAll() {
        return pList;
    }

    @Override
    public Long update(Parking obj) {
        for (int i = 0; i < pList.size(); i++) {
            if (pList.get(i).getId() == obj.getId()) {
                pList.remove(i);
                pList.add(i, obj);
                manager.updateAll(obj.toString());
                return Long.valueOf(i);
            }
        }
        return null;
    }

    @Override
    public void delete(long id) {
        pList.remove(id);
    }

    @Override
    public void delete(Parking obj) {
        pList.remove(obj);
    }

    @Override
    public void cleanUp() {
        pList = null;
    }

    @Override
    public Manager<Parking> getManager() {
        return manager;
    }
}
