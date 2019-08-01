package info.ericlin.redditnow.main;

import androidx.lifecycle.ViewModel;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import info.ericlin.redditnow.room.RedditNowDao;
import info.ericlin.redditnow.room.RedditNowDatabase;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@AutoFactory
public class MainViewModel extends ViewModel {

  private final CompositeDisposable disposables = new CompositeDisposable();
  private final RedditNowDao redditNowDao;
  private final MainFeedDataManager mainFeedDataManager;

  public MainViewModel(@Provided MainFeedDataManager mainFeedDataManager, @Provided
      RedditNowDatabase redditNowDatabase) {
    this.mainFeedDataManager = mainFeedDataManager;
    this.mainFeedDataManager.updateHomeFeed();
    redditNowDao = redditNowDatabase.redditNowDao();
    subscribeToPosts();
  }

  private void subscribeToPosts() {
    Disposable disposable = redditNowDao.getAllActivePosts()
        .subscribeOn(Schedulers.io())
        .subscribe(postEntities -> Timber.i("we have %s active posts", postEntities.size()));

    disposables.add(disposable);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposables.clear();
  }
}
