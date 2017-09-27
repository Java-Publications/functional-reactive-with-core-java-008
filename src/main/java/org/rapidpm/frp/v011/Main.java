package org.rapidpm.frp.v011;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 *
 */
public class Main {

  public static interface Registration {
    public void remove();
  }

  public static class Observable<VALUE> {

    private final Set<Consumer<VALUE>> listeners = ConcurrentHashMap.newKeySet();

    public Registration register(Consumer<VALUE> listener) {
      listeners.add(listener);

      return () -> listeners.remove(listener);
    }

    public void sendEvent(VALUE event) {
      listeners.forEach(c -> c.accept(event));
    }
  }


  public static void main(String[] args) {

    final List<String> results = new ArrayList<>();

    final Observable<String> observableA = new Observable<>();
    final Observable<String> observableB = new Observable<>();
    final Observable<String[]> observableC = new Observable<>();

    observableA.register(s -> observableB.sendEvent(s.toUpperCase()));
    observableA.register(s -> observableB.sendEvent(s.toLowerCase()));
    observableB.register(s -> observableC.sendEvent(s.split(" ")));
    observableC.register(strings -> results.addAll(Arrays.asList(strings)));
    observableC.register(strings -> System.out.println("From C2 " + Arrays.asList(strings)));

    observableA.sendEvent("Hello World ");

    System.out.println("results = " + results);

  }

}
