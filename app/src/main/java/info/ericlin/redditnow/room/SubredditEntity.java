package info.ericlin.redditnow.room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SubredditEntity {

  @PrimaryKey
  @NonNull
  public String id;

  @NonNull
  public String name;

  @ColumnInfo(name = "banner_image")
  @Nullable
  public String bannerImageUrl;

  @NonNull
  public String title;

  @ColumnInfo(name = "num_subscribers")
  public int numberOfSubscribers;

  @NonNull
  public String description;

  @ColumnInfo(name = "key_color")
  @Nullable
  public String keyColor;
}
