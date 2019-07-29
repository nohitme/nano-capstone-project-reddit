package info.ericlin.redditnow;

import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class ThreadNameDebugTree extends Timber.DebugTree {

  @Override protected void log(int priority, String tag, @NotNull String message, Throwable t) {
    String newMessage = String.format("{%s} %s", Thread.currentThread().getName(), message);
    super.log(priority, tag, newMessage, t);
  }
}
