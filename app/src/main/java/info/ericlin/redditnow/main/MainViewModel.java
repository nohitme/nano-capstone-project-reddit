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
          BarebonesPaginator<Subreddit> popular = redditClient.userSubreddits("popular").build();
          return FluentIterable.from(popular).toList();
        })
        .subscribeOn(Schedulers.io())
        .subscribe(list -> {
          Timber.i("eric, loadUserSubreddits -> %s", list);
        });

    disposables.add(disposable);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposables.clear();
  }
}
