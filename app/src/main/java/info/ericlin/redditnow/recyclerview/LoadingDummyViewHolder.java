package info.ericlin.redditnow.recyclerview;

import android.view.View;
import androidx.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;

public class LoadingDummyViewHolder extends RedditViewHolder<LoadingDummyItem> {

  public LoadingDummyViewHolder(@NonNull View itemView, @NonNull EventBus eventBus) {
    super(itemView, eventBus);
  }

  @Override
  protected void bind(@NonNull LoadingDummyItem item) {
    getEventBus().post(OnLoadingViewBoundEvent.INSTANCE);
  }

  @Override
  protected void unbind() {
    // do nothing
  }

  public static class OnLoadingViewBoundEvent {

    static final OnLoadingViewBoundEvent INSTANCE = new OnLoadingViewBoundEvent();

    private OnLoadingViewBoundEvent() {
      //no instance
    }
  }
}
