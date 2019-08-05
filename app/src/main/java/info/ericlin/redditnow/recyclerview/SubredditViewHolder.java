package info.ericlin.redditnow.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;
import info.ericlin.redditnow.GlideApp;
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

  @BindView(R.id.subreddit_banner)
  ImageView imageView;

  public SubredditViewHolder(@NonNull View itemView, @NonNull EventBus eventBus) {
    super(itemView, eventBus);
    ButterKnife.bind(this, itemView);
  }

  @Override
  protected void bind(@NonNull SubredditEntity item) {
    itemView.setOnClickListener(view -> getEventBus().post(OnClickSubredditEvent.create(item)));

    ColorDrawable colorDrawable = new ColorDrawable(getBannerErrorColor(item.keyColor));
    String imageUrl = UiUtils.getSubredditBannerImageUrl(item);

    GlideApp.with(imageView)
        .load(imageUrl)
        .centerCrop()
        .error(colorDrawable)
        .into(imageView);

    Context context = itemView.getContext();

    subredditName.setText(context.getString(R.string.vh_subreddit_name, item.name));
    subredditName.setTextColor(getTextColor(item.keyColor));

    subredditNumSubs.setText(
        context.getString(R.string.vh_subreddit_members, item.numberOfSubscribers));

    subredditDescription.setText(item.description);
  }

  @ColorInt
  private int getBannerErrorColor(String keyColor) {
    try {
      return Color.parseColor(keyColor);
    } catch (Exception e) {
      return ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
    }
  }

  @Override
  protected void unbind() {
    Glide.with(imageView).clear(imageView);
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

  @AutoValue
  public static abstract class OnClickSubredditEvent {

    public abstract SubredditEntity subreddit();

    public static OnClickSubredditEvent create(SubredditEntity subreddit) {
      return new AutoValue_SubredditViewHolder_OnClickSubredditEvent(subreddit);
    }
  }
}
