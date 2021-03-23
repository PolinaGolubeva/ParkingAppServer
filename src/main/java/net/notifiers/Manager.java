package net.notifiers;

import java.util.ArrayList;
import java.util.List;

public class Manager<T> {
    private List<Listener<T>> listeners;

    public Manager() {
        this.listeners = new ArrayList<Listener<T>>();
    }

    public void subscribe(Listener listener) {
        this.listeners.add(listener);
    }

    public void unsubscribe(Listener listener) {
        this.listeners.remove(listener);
    }

    public void updateAll(String message) {
        for (Listener listener : listeners)
            listener.update(message);
    }
}
