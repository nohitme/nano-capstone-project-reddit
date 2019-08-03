package info.ericlin.redditnow.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.common.base.Strings;
import info.ericlin.redditnow.R;
import info.ericlin.redditnow.room.SubredditEntity;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

public class SubredditViewHolder extends RedditViewHolder<SubredditEntity> {

  @BindView(R.id.subreddit_name)
  TextView subredditName;

  @BindView(R.id.subreddit_num_subs)
  TextView subredditNumSubs;

  @BindView(R.id.subreddit_description)
  TextView subredditDescription;

  public SubredditViewHolder(@NonNull View itemView, @NonNull EventBus eventBus) {
    super(itemView, eventBus);
    ButterKnife.bind(this, itemView);
  }

  @Override
  protected void bind(@NonNull SubredditEntity item) {

    Context context = itemView.getContext();

    subredditName.setText(context.getString(R.string.vh_subreddit_name, item.name));
    subredditName.setTextColor(getTextColor(item.keyColor));

    subredditNumSubs.setText(
        context.getString(R.string.vh_subreddit_members, item.numberOfSubscribers));

    subredditDescription.setText(item.description);
  }

  @Override
  protected void unbind() {
    // do nothing
  }

  @ColorInt
  private int getTextColor(@Nullable String colorHex) {
    if (!Strings.isNullOrEmpty(colorHex)) {
      try {
        int color = Color.parseColor(colorHex);
        return UiUtils.darken(color, /* fraction= */ 0.3d);
      } catch (Exception e) {
        Timber.w(e, "cannot parse color hex: %s", colorHex);
      }
    }

    return Color.BLACK;
  }
}
