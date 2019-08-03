package info.ericlin.redditnow.subreddit;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import info.ericlin.redditnow.main.RedditClientWrapper;

@AutoFactory
public class SubredditViewModel extends ViewModel {

  private RedditClientWrapper redditClientWrapper;

  public SubredditViewModel(@Provided RedditClientWrapper redditClientWrapper) {
    this.redditClientWrapper = redditClientWrapper;
  }

  void loadSubreddit(@NonNull String subredditName) {

  }
}
