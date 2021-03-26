package dbservice.services;

import net.notifiers.Listener;
import net.notifiers.Manager;

import java.util.List;

public interface DBService<T> {
    public void createTable();
    public Long insert(T obj);
    public T get(long id);
    public List<T> getAll();
    public Long update(T obj);
    public void delete(long id);
    public void delete(T obj);
    public void cleanUp();
    public Manager<T> getManager();
}
