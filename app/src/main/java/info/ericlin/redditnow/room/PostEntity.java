package info.ericlin.redditnow.room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity
public class PostEntity {

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

  /**
   * Returns true if users have seen and swiped the post away.
   *
   * <p>This is an extra properties added for Reddit Now! app.
   */
  @ColumnInfo(name = "is_swiped")
  public boolean isSwiped;
}
