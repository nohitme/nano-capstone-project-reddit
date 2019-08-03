package info.ericlin.redditnow.room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.common.base.Objects;
import info.ericlin.redditnow.recyclerview.RedditListItem;
import java.util.Date;

@Entity
public class PostEntity implements RedditListItem {

  @PrimaryKey
  @NonNull
  public String id;

  @NonNull
  public String author;

  @ColumnInfo(name = "created_at")
  @NonNull
  public Date createdAt;

  @NonNull
  public String url;

  @NonNull
  public String title;

  @ColumnInfo(name = "thumbnail_url")
  @Nullable
  public String thumbnailImageUrl;

  @Nullable
  public String text;

  @ColumnInfo(name = "comment_count")
  public int commentCount;

  @NonNull
  public String subreddit;

  @NonNull
  @Override
  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PostEntity that = (PostEntity) o;
    return commentCount == that.commentCount &&
        Objects.equal(id, that.id) &&
        Objects.equal(author, that.author) &&
        Objects.equal(createdAt, that.createdAt) &&
        Objects.equal(url, that.url) &&
        Objects.equal(title, that.title) &&
        Objects.equal(thumbnailImageUrl, that.thumbnailImageUrl) &&
        Objects.equal(text, that.text) &&
        Objects.equal(subreddit, that.subreddit);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, author, createdAt, url, title, thumbnailImageUrl, text,
        commentCount,
        subreddit);
  }
}
