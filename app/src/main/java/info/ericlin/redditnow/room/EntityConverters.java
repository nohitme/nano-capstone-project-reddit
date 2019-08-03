package info.ericlin.redditnow.room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.common.collect.FluentIterable;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubmissionPreview;
import net.dean.jraw.models.Subreddit;

public class EntityConverters {

  private EntityConverters() {
    //no instance
  }

  @NonNull
  public static SubredditEntity toEntity(@NonNull Subreddit subreddit) {
    SubredditEntity entity = new SubredditEntity();
    entity.id = subreddit.getId();
    entity.bannerImageUrl = subreddit.getBannerImage();
    entity.description = subreddit.getPublicDescription();
    entity.name = subreddit.getName();
    entity.numberOfSubscribers = subreddit.getSubscribers();
    entity.title = subreddit.getTitle();
    entity.keyColor = subreddit.getKeyColor();
    return entity;
  }

  @NonNull
  public static PostEntity toEntity(@NonNull Submission submission) {
    PostEntity postEntity = new PostEntity();
    postEntity.id = submission.getId();
    postEntity.author = submission.getAuthor();
    postEntity.commentCount = submission.getCommentCount();
    postEntity.createdAt = submission.getCreated();
    postEntity.subreddit = submission.getSubreddit();
    postEntity.text = submission.getSelfText();
    postEntity.title = submission.getTitle();
    postEntity.url = submission.getUrl();
    postEntity.thumbnailImageUrl = getThumbnailImage(submission);
    return postEntity;
  }

  @Nullable
  private static String getThumbnailImage(@NonNull Submission submission) {
    if (submission.getPreview() != null) {
      SubmissionPreview.ImageSet imageSet =
          FluentIterable.from(submission.getPreview().getImages()).first().orNull();
      if (imageSet != null) {
        return imageSet.getSource().getUrl();
      }
    }

    return submission.getThumbnail();
  }
}
