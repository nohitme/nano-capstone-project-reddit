package info.ericlin.redditnow.recyclerview;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public abstract class RedditViewHolder<T extends RedditListItem> extends RecyclerView.ViewHolder {

  @NonNull
  private final EventBus eventBus;

  public RedditViewHolder(@NonNull View itemView, @NonNull EventBus eventBus) {
    super(itemView);
    this.eventBus = eventBus;
  }

  @NonNull
  protected final EventBus getEventBus() {
    return eventBus;
  }

  protected final Context getContext() {
    return itemView.getContext();
  }

  protected abstract void bind(@NonNull T item);

  protected void bind(@NonNull T item, @NonNull List<Object> payloads) {
    // fallback to bind
    bind(item);
  }

  protected abstract void unbind();
}
