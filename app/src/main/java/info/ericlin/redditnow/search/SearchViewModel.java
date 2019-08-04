package info.ericlin.redditnow.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import info.ericlin.redditnow.main.RedditClientWrapper;
import info.ericlin.redditnow.recyclerview.LoadingDummyItem;
import info.ericlin.redditnow.recyclerview.RedditListItem;
import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.references.SubredditReference;
import timber.log.Timber;

@AutoFactory
public class SearchViewModel extends ViewModel {

  private final MutableLiveData<List<RedditListItem>> listLiveData = new MutableLiveData<>();
  private final MutableLiveData<Boolean> initialLoading = new MutableLiveData<>();
  private final RedditClientWrapper redditClientWrapper;

  private final CompositeDisposable disposables = new CompositeDisposable();
  private final List<SearchResultItem> redditListItems = new ArrayList<>(64);

  @Nullable
  private Iterator<Listing<Subreddit>> iterator;

  @Nullable
  private String query;

  public SearchViewModel(@Provided RedditClientWrapper redditClientWrapper) {
    this.redditClientWrapper = redditClientWrapper;
  }

  public LiveData<List<RedditListItem>> getListLiveData() {
    return listLiveData;
  }

  public LiveData<Boolean> getInitialLoadingLiveData() {
    return initialLoading;
  }

  public void searchSubreddits(@NonNull String query) {
    if (Objects.equal(this.query, query)) {
      return;
    }

    this.query = query;
    redditListItems.clear();
    listLiveData.postValue(Collections.emptyList());
    initialLoading.postValue(true);
    iterator = redditClientWrapper.get().searchSubreddits()
        .query(query).build().iterator();
    loadNextPage();
  }

  void loadNextPage() {
    if (iterator == null || !iterator.hasNext()) {
      listLiveData.postValue(ImmutableList.copyOf(redditListItems));
      initialLoading.postValue(false);
      return;
    }

    Disposable disposable = Completable.fromAction(() -> {
      List<SearchResultItem> resultItems =
          FluentIterable.from(iterator.next()).transform(SearchViewModel::toSearchResult).toList();

      redditListItems.addAll(resultItems);
      List<RedditListItem> newItems = new ArrayList<>(this.redditListItems);
      newItems.add(LoadingDummyItem.getInstance());
      listLiveData.postValue(newItems);
    })
        .subscribeOn(Schedulers.io())
        .doOnTerminate(() -> initialLoading.postValue(false))
        .subscribe();

    disposables.add(disposable);
  }

  private static SearchResultItem toSearchResult(@NonNull Subreddit subreddit) {
    return SearchResultItem.builder()
        .id(subreddit.getId())
        .name(subreddit.getName())
        .numberOfSubscribers(subreddit.getSubscribers())
        .isSubscribed(subreddit.isUserSubscriber())
        .build();
  }

  void updateSubredditSubscription(String subredditName, boolean isCurrentlySubscribed) {
    Disposable disposable = Completable.fromAction(() -> {
      SubredditReference subreddit = redditClientWrapper.get().subreddit(subredditName);
      if (isCurrentlySubscribed) {
        subreddit.unsubscribe();
      } else {
        subreddit.subscribe();
      }

      // find the one in the list
      List<SearchResultItem> newList = new ArrayList<>(this.redditListItems);
      int index = Iterables.indexOf(newList, input -> Objects.equal(subredditName, input.name()));
      if (index >= 0) {

        Timber.i("list state size: %s", redditListItems.size());
        SearchResultItem searchResultItem = newList.get(index);
        SearchResultItem newCopy = SearchResultItem.builder()
            .name(subredditName)
            .numberOfSubscribers(searchResultItem.numberOfSubscribers())
            .isSubscribed(!isCurrentlySubscribed)
            .id(searchResultItem.id())
            .build();
        newList.set(index, newCopy);
        redditListItems.clear();
        redditListItems.addAll(newList);
        Timber.i("list state size after: %s", redditListItems.size());
        listLiveData.postValue(ImmutableList.copyOf(newList));
      }
    }).subscribeOn(Schedulers.io())
        .onErrorComplete()
        .subscribe();

    disposables.add(disposable);
  }
}
