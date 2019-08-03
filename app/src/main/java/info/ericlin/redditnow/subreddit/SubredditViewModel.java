package info.ericlin.redditnow.subreddit;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import info.ericlin.redditnow.main.RedditClientWrapper;
import net.dean.jraw.RedditClient;

@AutoFactory
public class SubredditViewModel extends ViewModel {

  private final RedditClient redditClient;

  public SubredditViewModel(@Provided RedditClientWrapper redditClientWrapper) {
    redditClient = redditClientWrapper.get();
  }

  void loadSubreddit(@NonNull String subredditName) {

  }
}
