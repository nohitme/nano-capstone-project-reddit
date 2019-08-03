package info.ericlin.redditnow.recyclerview;

import android.view.View;
import androidx.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;

public class EmptyDummyViewHolder extends RedditViewHolder<EmptyDummyItem> {

  public EmptyDummyViewHolder(@NonNull View itemView, @NonNull EventBus eventBus) {
    super(itemView, eventBus);
  }

  @Override
  protected void bind(@NonNull EmptyDummyItem item) {
    // do nothing
  }

  @Override
  protected void unbind() {
    // do nothing
  }
}
