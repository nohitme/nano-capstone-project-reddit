package info.ericlin.redditnow;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import org.greenrobot.eventbus.EventBus;

public class EventBusUtils {

  private EventBusUtils() {
    //no instance
  }

  public static void registerEventBusWithLifecycle(@NonNull EventBus eventBus,
      @NonNull LifecycleOwner owner, @NonNull Object subscriber) {
    owner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
      @Override
      public void onStart(@NonNull LifecycleOwner owner) {
        eventBus.register(subscriber);
      }

      @Override
      public void onStop(@NonNull LifecycleOwner owner) {
        eventBus.unregister(subscriber);
      }
    });
  }
}
