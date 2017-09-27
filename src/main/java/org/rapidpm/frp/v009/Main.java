package org.rapidpm.frp.v009;

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
    final Observable<String, List<String>> observableD = new Observable<>();

    observableA.register("A1" , s -> observableB.sendEvent(s.toUpperCase()));
    observableA.register("A2" , s -> observableB.sendEvent(s.toLowerCase()));
    observableB.register("B" , s -> observableC.sendEvent(s.split(" ")));
    observableC.register("C1" , strings -> results.addAll(Arrays.asList(strings)));
    observableC.register("C2" , strings -> System.out.println("From C2 " + Arrays.asList(strings)));
    observableD.register("D" , strings -> System.out.println("strings = " + strings));

    observableA.sendEvent("Hello World ");

    System.out.println("results = " + results);
    // define Consumer System.out
//    observableD.register("D" , new Consumer<List<String>>() {
//      @Override
//      public void accept(List<String> strings) {
//        System.out.println("strings = " + strings);
//      }
//    });

//    Kombiniere mit Streams

  }

}
