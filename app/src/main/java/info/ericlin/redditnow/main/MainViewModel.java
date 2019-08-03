package info.ericlin.redditnow.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import info.ericlin.redditnow.recyclerview.EmptyDummyItem;
import info.ericlin.redditnow.recyclerview.RedditListItem;
import info.ericlin.redditnow.room.PostEntity;
import info.ericlin.redditnow.room.RedditNowDao;
import info.ericlin.redditnow.room.RedditNowDatabase;
import info.ericlin.redditnow.room.SubredditEntity;
import info.ericlin.redditnow.room.SwipedPostEntity;
import info.ericlin.redditnow.settings.PreferenceManager;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AutoFactory
public class MainViewModel extends ViewModel {

  private final CompositeDisposable disposables = new CompositeDisposable();
  private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
  private final MutableLiveData<List<RedditListItem>> redditListItemLiveData =
      new MutableLiveData<>();
  private final RedditNowDao redditNowDao;
  private PreferenceManager preferenceManager;
  private final MainFeedDataManager mainFeedDataManager;

  public MainViewModel(@Provided MainFeedDataManager mainFeedDataManager,
      @Provided RedditNowDatabase redditNowDatabase,
      @Provided PreferenceManager preferenceManager) {
    this.mainFeedDataManager = mainFeedDataManager;
    this.redditNowDao = redditNowDatabase.redditNowDao();
    this.preferenceManager = preferenceManager;

    subscribeToDatabaseChanges();
    updateHomeFeed();
  }

  public void updateHomeFeed() {
    Disposable disposable = mainFeedDataManager.updateHomeFeed()
        .doOnSubscribe(d -> isLoadingLiveData.postValue(true))
        .doOnTerminate(() -> isLoadingLiveData.postValue(false))
        .subscribe();

    disposables.add(disposable);
  }

  private void subscribeToDatabaseChanges() {
    Flowable<List<SubredditEntity>> subreddits =
        redditNowDao.getAllSubreddits().subscribeOn(Schedulers.io());
    Flowable<List<PostEntity>> activePosts =
        redditNowDao.getAllActivePosts().subscribeOn(Schedulers.io());

    Disposable disposable =
        Flowable.combineLatest(subreddits, activePosts, this::joinSubredditAndPosts)
            .onErrorReturn(throwable -> Collections.emptyList())
            .subscribeOn(Schedulers.io())
            .subscribe(redditListItemLiveData::postValue);

    disposables.add(disposable);
  }

  private List<RedditListItem> joinSubredditAndPosts(List<SubredditEntity> subredditEntities,
      List<PostEntity> postEntities) {

    Multimap<String, PostEntity> postMap =
        FluentIterable.from(postEntities).index(post -> post.subreddit);

    List<RedditListItem> items = new ArrayList<>();
    for (SubredditEntity subreddit : subredditEntities) {
      items.add(subreddit);
      List<PostEntity> list = FluentIterable.from(postMap.get(subreddit.name))
          .limit(preferenceManager.getNumberPostToShow())
          .toList();

      if (list.isEmpty()) {
        items.add(EmptyDummyItem.getInstance());
      } else {
        items.addAll(list);
      }
    }

    return items;
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposables.clear();
  }

  @NonNull
  public LiveData<List<RedditListItem>> getRedditListItemLiveData() {
    return redditListItemLiveData;
  }

  @NonNull
  public LiveData<Boolean> isLoadingLiveData() {
    return isLoadingLiveData;
  }

  public void markPostAsRead(String postId) {
    Completable.fromAction(() -> {
      SwipedPostEntity entity = new SwipedPostEntity();
      entity.id = postId;
      redditNowDao.insertSwipedPosts(entity);
    }).subscribeOn(Schedulers.io())
        .subscribe();
  }
}
