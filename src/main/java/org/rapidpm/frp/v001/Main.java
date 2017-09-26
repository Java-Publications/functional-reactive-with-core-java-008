package org.rapidpm.frp.v001;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 *
 */
public class Main {


  public static class Observable<KEY, VALUE> {

    private final Map<KEY, Consumer<VALUE>> listeners = new ConcurrentHashMap<>();

    public void register(KEY key , Consumer<VALUE> listener) {
      listeners.put(key , listener);
    }

    public void unregister(KEY key) { listeners.remove(key);}

    public void sendEvent(VALUE event) {
      listeners.values().forEach(c -> c.accept(event));
    }
  }

  public static void main(String[] args) {

    final Observable<String, String> observable = new Observable<>();

    observable.register("key1", System.out::println);
    observable.register("key2", System.out::println);

    observable.sendEvent("Hello World");

    observable.unregister("key1");
    observable.sendEvent("Hello World again");
  }

}
