package org.rapidpm.frp.v003;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 *
 */
public class Main {


  public static interface Registration {
    public void remove();
  }

  public static class Observable<KEY, VALUE> {

    private final Map<KEY, Consumer<VALUE>> listeners = new ConcurrentHashMap<>();

    public Registration register(KEY key , Consumer<VALUE> listener) {
      listeners.put(key , listener);

      return () -> listeners.remove(key);
    }

    public void sendEvent(VALUE event) {
      listeners.values().forEach(c -> c.accept(event));
    }
  }


  public static class Registry {

    private static final Observable<String, String> observable = new Observable<>();

    public static Registration register(String key , Consumer<String> consumer) {
      return observable.register(key , consumer);
    }

    public static void sendEvent(String input) {
      observable.sendEvent(input);
    }

  }


  public static void main(String[] args) {
    final Registration registerA = Registry.register("key1" , System.out::println);
    final Registration registerB = Registry.register("key2" , System.out::println);
    Registry.sendEvent("Hello World");

    //done by life cycle
    registerA.remove();

    Registry.sendEvent("Hello World again");
  }
}
