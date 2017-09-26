package org.rapidpm.frp.v002;

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


  public static class Registry {

    private static final Observable<String, String> observable = new Observable<>();

    public static void register(String key, Consumer<String> consumer){
      observable.register(key, consumer);
    }

    public static void unregister(String key){
      observable.unregister(key);
    }

    public static void sendEvent(String input){
      observable.sendEvent(input);
    }

  }


  public static void main(String[] args) {
    Registry.register("key1" , System.out::println);
    Registry.register("key2" , System.out::println);

    Registry.sendEvent("Hello World");

    Registry.unregister("key1");
    Registry.sendEvent("Hello World again");
  }

}
