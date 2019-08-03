package info.ericlin.redditnow.room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.common.base.Objects;
import info.ericlin.redditnow.recyclerview.RedditListItem;

@Entity
public class SubredditEntity implements RedditListItem {

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

  @NonNull
  @Override
  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubredditEntity that = (SubredditEntity) o;
    return numberOfSubscribers == that.numberOfSubscribers &&
        Objects.equal(id, that.id) &&
        Objects.equal(name, that.name) &&
        Objects.equal(bannerImageUrl, that.bannerImageUrl) &&
        Objects.equal(title, that.title) &&
        Objects.equal(description, that.description) &&
        Objects.equal(keyColor, that.keyColor);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, name, bannerImageUrl, title, numberOfSubscribers, description,
        keyColor);
  }
}
