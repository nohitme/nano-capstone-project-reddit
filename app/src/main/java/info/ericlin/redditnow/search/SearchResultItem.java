package info.ericlin.redditnow.search;

import androidx.annotation.NonNull;
import com.google.auto.value.AutoValue;
import info.ericlin.redditnow.recyclerview.RedditListItem;

@AutoValue
public abstract class SearchResultItem implements RedditListItem {

  public abstract String id();

  public abstract String name();

  public abstract int numberOfSubscribers();

  public abstract boolean isSubscribed();

  @NonNull
  @Override
  public String getId() {
    return id();
  }

  public static Builder builder() {
    return new AutoValue_SearchResultItem.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder id(String id);

    public abstract Builder name(String name);

    public abstract Builder numberOfSubscribers(int numberOfSubscribers);

    public abstract Builder isSubscribed(boolean isSubscribed);

    public abstract SearchResultItem build();
  }
}
