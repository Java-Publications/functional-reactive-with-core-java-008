package org.rapidpm.frp.v005;

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


  // working methods
  public static class Worker {

    public String doWork(String input) {
      return input.toUpperCase();
    }

    public String[] split(String input) {
      return input.split(" ");
    }
  }


  public static void main(String[] args) {


    final Worker worker = new Worker();
    final String[] split = worker.split(worker.doWork("Hello World"));
    System.out.println("result = " + Arrays.asList(split));

    final List<String> results = new ArrayList<>();

    final Observable<String, String> observableA = new Observable<>();
    final Observable<String, String> observableB = new Observable<>();
    final Observable<String, String[]> observableC = new Observable<>();

    observableA.register("A" , s -> observableB.sendEvent(worker.doWork(s)));
    observableB.register("B" , s -> observableC.sendEvent(worker.split(s)));
    observableC.register("C" , strings -> results.addAll(Arrays.asList(strings)));

    observableA.sendEvent("Hello World ");

    System.out.println("results = " + results);
  }
}
