package info.ericlin.redditnow.subreddit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import info.ericlin.redditnow.main.RedditClientWrapper;
import info.ericlin.redditnow.recyclerview.LoadingDummyItem;
import info.ericlin.redditnow.recyclerview.RedditListItem;
import info.ericlin.redditnow.room.EntityConverters;
import info.ericlin.redditnow.room.PostEntity;
import info.ericlin.redditnow.room.RedditNowDao;
import info.ericlin.redditnow.room.RedditNowDatabase;
import info.ericlin.redditnow.room.SubredditEntity;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.references.SubredditReference;

@AutoFactory
public class SubredditViewModel extends ViewModel {

  private final MutableLiveData<List<RedditListItem>> listLiveData = new MutableLiveData<>();
  private final MutableLiveData<Boolean> initialLoading = new MutableLiveData<>();
  private final MutableLiveData<SubredditEntity> subredditEntity = new MutableLiveData<>();

  private final List<RedditListItem> redditListItems = new ArrayList<>(64);
  private final CompositeDisposable disposables = new CompositeDisposable();

  private final RedditClientWrapper redditClientWrapper;
  private final RedditNowDao redditNowDao;

  @Nullable
  private Iterator<Listing<Submission>> iterator;

  public SubredditViewModel(@Provided RedditClientWrapper redditClientWrapper,
      @Provided RedditNowDatabase redditNowDatabase) {
    this.redditClientWrapper = redditClientWrapper;
    this.redditNowDao = redditNowDatabase.redditNowDao();
  }

  void init(@NonNull String subredditName) {
    redditListItems.clear();
    SubredditReference subredditReference = redditClientWrapper.get().subreddit(subredditName);
    iterator = subredditReference.posts().build().iterator();
    initialLoading.postValue(true);
    loadSubreddit(subredditReference);
    loadNextPage();
  }

  private void loadSubreddit(SubredditReference subredditReference) {
    Disposable disposable = Maybe.fromCallable(subredditReference::about)
        .subscribeOn(Schedulers.io())
        .onErrorComplete()
        .map(EntityConverters::toEntity)
        .subscribe(subredditEntity::postValue);

    disposables.add(disposable);
  }

  void loadNextPage() {
    if (iterator == null || !iterator.hasNext()) {
      listLiveData.postValue(ImmutableList.copyOf(redditListItems));
      initialLoading.postValue(false);
      return;
    }

    Disposable disposable = Completable.fromAction(() -> {
      List<PostEntity> postEntities =
          FluentIterable.from(iterator.next()).transform(EntityConverters::toEntity).toList();
      redditNowDao.insertPosts(postEntities);
      this.redditListItems.addAll(postEntities);

      List<RedditListItem> newItems = new ArrayList<>(this.redditListItems);
      newItems.add(LoadingDummyItem.getInstance());
      listLiveData.postValue(newItems);
    })
        .subscribeOn(Schedulers.io())
        .doOnTerminate(() -> initialLoading.postValue(false))
        .subscribe();

    disposables.add(disposable);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposables.clear();
  }

  public LiveData<List<RedditListItem>> getListLiveData() {
    return listLiveData;
  }

  public LiveData<Boolean> getInitialLoadingLiveData() {
    return initialLoading;
  }

  public LiveData<SubredditEntity> getSubredditEntityLiveData() {
    return subredditEntity;
  }
}
