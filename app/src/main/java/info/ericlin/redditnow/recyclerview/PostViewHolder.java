package info.ericlin.redditnow.recyclerview;

import android.content.Context;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;
import info.ericlin.redditnow.R;
import info.ericlin.redditnow.room.PostEntity;
import java.util.Date;
import org.greenrobot.eventbus.EventBus;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

public class PostViewHolder extends RedditViewHolder<PostEntity> {

  private final RequestOptions requestOptions =
      new RequestOptions().error(R.drawable.oops_placeholder);

  @BindView(R.id.post_username)
  TextView postUsername;

  @BindView(R.id.post_created_at)
  TextView postCreatedAt;

  @BindView(R.id.post_text)
  TextView postText;

  @BindView(R.id.post_image)
  ImageView postImage;

  @BindView(R.id.post_comment_count)
  TextView postCommentCount;

  public PostViewHolder(@NonNull View itemView, @NonNull EventBus eventBus) {
    super(itemView, eventBus);
    ButterKnife.bind(this, itemView);
  }

  @Override
  protected void bind(@NonNull PostEntity item) {
    itemView.setOnClickListener(view -> getEventBus().post(OnClickPostEvent.create(item)));

    if (item.thumbnailImageUrl == null || !URLUtil.isValidUrl(item.thumbnailImageUrl)) {
      postImage.setVisibility(View.GONE);
    } else {
      postImage.setVisibility(View.VISIBLE);
      Glide.with(postImage).load(item.thumbnailImageUrl).apply(requestOptions).into(postImage);
    }

    postCommentCount.setText(String.valueOf(item.commentCount));
    postCreatedAt.setText(getCreatedTimeDisplayText(item.createdAt));

    final String text;
    if (!Strings.isNullOrEmpty(item.text)) {
      text = item.text;
    } else {
      text = item.title;
    }

    if (Strings.isNullOrEmpty(text)) {
      postText.setVisibility(View.GONE);
    } else {
      postText.setVisibility(View.VISIBLE);
      postText.setText(text);
    }

    postUsername.setText(getContext().getString(R.string.vh_post_username, item.author));
  }

  @Override
  protected void unbind() {
    Glide.with(postImage).clear(postImage);
  }

  private String getCreatedTimeDisplayText(Date createdAt) {
    Instant now = Instant.now();
    Instant created = Instant.ofEpochMilli(createdAt.getTime());
    Duration between = Duration.between(created, now);

    Context context = itemView.getContext();
    long days = between.toDays();
    if (days > 0) {
      return context.getString(R.string.vh_duration_days, days);
    }

    long hours = between.toHours();
    if (hours > 0) {
      return context.getString(R.string.vh_duration_hours, hours);
    }

    return context.getString(R.string.vh_duration_minutes, between.toMinutes());
  }

  @AutoValue
  public static abstract class OnClickPostEvent {

    public abstract PostEntity post();

    public static OnClickPostEvent create(PostEntity post) {
      return new AutoValue_PostViewHolder_OnClickPostEvent(post);
    }
  }
}
