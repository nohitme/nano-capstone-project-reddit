package info.ericlin.redditnow.recyclerview;

import androidx.annotation.NonNull;

public class LoadingDummyItem implements RedditListItem {

  private static final LoadingDummyItem INSTANCE = new LoadingDummyItem();

  private LoadingDummyItem() {
    //no instance
  }

  @NonNull
  public static LoadingDummyItem getInstance() {
    return INSTANCE;
  }

  @NonNull
  @Override
  public String getId() {
    return LoadingDummyItem.class.getName();
  }
}
