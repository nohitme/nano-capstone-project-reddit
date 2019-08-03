package info.ericlin.redditnow.recyclerview;

import androidx.annotation.NonNull;

public class EmptyDummyItem implements RedditListItem {

  private static final EmptyDummyItem INSTANCE = new EmptyDummyItem();

  private EmptyDummyItem() {
    //no instance
  }

  @NonNull
  @Override
  public String getId() {
    return EmptyDummyItem.class.getName();
  }

  @NonNull
  public static EmptyDummyItem getInstance() {
    return INSTANCE;
  }
}
