package info.ericlin.redditnow.main;

import androidx.lifecycle.ViewModel;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.google.common.collect.FluentIterable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.pagination.BarebonesPaginator;
import net.dean.jraw.references.SubredditReference;
import timber.log.Timber;

@AutoFactory
public class MainViewModel extends ViewModel {

  private final CompositeDisposable disposables = new CompositeDisposable();
  private final RedditClient redditClient;

  public MainViewModel(@Provided RedditClientWrapper redditClientWrapper) {
    redditClient = redditClientWrapper.get();
    loadUserSubreddits();
  }

  private void loadUserSubreddits() {
    Disposable disposable = Single.fromCallable(
        () -> {
          BarebonesPaginator<Subreddit> popular = redditClient.me().subreddits("subscriber").build();
          return FluentIterable.from(popular.next()).toList();
        })
        .subscribeOn(Schedulers.io())
        .subscribe(list -> {
          for (Subreddit subreddit : list) {
            SubredditReference reference = redditClient.subreddit(subreddit.getName());
            Timber.i("eric, ref: %s", reference.about().getTitle());
          }
        });

    disposables.add(disposable);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposables.clear();
  }
}
