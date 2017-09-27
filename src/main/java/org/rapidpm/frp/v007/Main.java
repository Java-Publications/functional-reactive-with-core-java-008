package org.rapidpm.frp.v007;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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


  public static void main(String[] args) {

    final List<String> results = new ArrayList<>();

    final Observable<String, String> observableA = new Observable<>();
    final Observable<String, String> observableB = new Observable<>();
    final Observable<String, String[]> observableC = new Observable<>();

    observableA.register("A" , s -> observableB.sendEvent(s.toUpperCase()));
    observableB.register("B" , s -> observableC.sendEvent(s.split(" ")));
    observableC.register("C" , strings -> results.addAll(Arrays.asList(strings)));

    observableA.sendEvent("Hello World ");

    System.out.println("results = " + results);

  }
}
