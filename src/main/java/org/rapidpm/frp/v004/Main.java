package org.rapidpm.frp.v004;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
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

    private final Map<KEY, WeakReference<Consumer<VALUE>>> listeners = new ConcurrentHashMap<>();
    private final ReferenceQueue<Consumer<VALUE>> referenceQueue = new ReferenceQueue<>();

    public Registration register(KEY key , Consumer<VALUE> listener) {
      final WeakReference<Consumer<VALUE>> weakReference = new WeakReference<>(listener , referenceQueue);
      listeners.put(key , weakReference);
      return () -> listeners.remove(key);
    }

    public void sendEvent(VALUE event) {
      listeners.forEach((key , weakReference) -> {
        final boolean enqueued = weakReference.isEnqueued();
        System.out.println("enqueued = " + enqueued);
        final Consumer<VALUE> valueConsumer = weakReference.get();
        if (valueConsumer != null) {
          valueConsumer.accept(event);
        }
      });
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

  public static class MyVirtualHolder {
    private MyVirtualButton myVirtualButton;

  }

  public static class MyVirtualButton {
    public void writeOnScreen(String value) {
      System.out.println("value = " + value);
    }
  }

  public static void main(String[] args) {


    MyVirtualButton myVirtualButtonA = new MyVirtualButton();
    MyVirtualButton myVirtualButtonB = new MyVirtualButton();

//    final Registration registerA = Registry.register("key1" , myVirtualButtonA::writeOnScreen);
//    final Registration registerB = Registry.register("key2" , myVirtualButtonB::writeOnScreen);
    MyVirtualButton finalMyVirtualButtonA = myVirtualButtonA;
    final Registration registerA = Registry.register("key1" , value -> finalMyVirtualButtonA.writeOnScreen(value));
    final Registration registerB = Registry.register("key2" , value -> myVirtualButtonB.writeOnScreen(value));
    Registry.sendEvent("Hello World");

    //done by life cycle
//    registerA.remove();

    myVirtualButtonA = null;
    System.gc();
    System.gc();
    System.gc();

    Registry.sendEvent("Hello World again");

    registerA.remove();
    registerB.remove();

    System.out.println("myVirtualButtonA = " + myVirtualButtonA);
    System.out.println("myVirtualButtonB = " + myVirtualButtonB);

    Registry.sendEvent("Hello World again 002");

  }


}
