package info.ericlin.redditnow.main;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import info.ericlin.redditnow.room.EntityConverters;
import info.ericlin.redditnow.room.PostEntity;
import info.ericlin.redditnow.room.RedditNowDao;
import info.ericlin.redditnow.room.RedditNowDatabase;
import info.ericlin.redditnow.room.SubredditEntity;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.pagination.BarebonesPaginator;
import net.dean.jraw.references.SubredditReference;
import timber.log.Timber;

/**
 * Fetches all subreddits that a user subscribes to and first 5 posts for each subreddit from the
 * server. The returned data then gets transformed and saved into database via {@link
 * RedditNowDao}. Because the data is stored directly to database, this class can be reused without
 * worry about leaking context.
 */
@Singleton
public class MainFeedDataManager {

  private static final int NUMBER_OF_ACTIVE_POST_TO_FETCH = 7;

  private final CompositeDisposable disposables = new CompositeDisposable();
  private final RedditClientWrapper redditClientWrapper;
  private final RedditNowDao redditNowDao;

  @Inject
  public MainFeedDataManager(RedditClientWrapper redditClientWrapper,
      RedditNowDatabase redditNowDatabase) {
    this.redditClientWrapper = redditClientWrapper;
    redditNowDao = redditNowDatabase.redditNowDao();
  }

  public void updateHomeFeed() {
    // cancel all ongoing tasks
    disposables.clear();

    RedditClient redditClient = redditClientWrapper.get();

    Disposable disposable = getSubreddits(redditClient)
        .doOnNext(subreddits -> Timber.i("get %s subreddits", subreddits.size()))
        .subscribeOn(Schedulers.io())
        .flatMapSingle(this::saveSubreddits)
        .flatMapIterable(entities -> entities)
        .flatMapSingle(subredditEntity -> getPostsForSubreddit(redditClient, subredditEntity))
        .doOnNext(posts -> Timber.i("get %s posts", posts.size()))
        .toList()
        .flatMapCompletable(this::savePosts)
        .onErrorComplete(throwable -> {
          Timber.w(throwable, "failed to update home feed");
          return true;
        })
        .subscribe();

    disposables.add(disposable);
  }

  private Completable savePosts(List<List<Submission>> submissionLists) {
    return Completable.fromAction(() -> {
      List<PostEntity> entities =
          FluentIterable.from(submissionLists).transformAndConcat(lists -> lists)
              .transform(EntityConverters::toEntity)
              .toList();
      redditNowDao.insertPosts(entities);
    });
  }

  private Single<List<SubredditEntity>> saveSubreddits(List<Subreddit> subreddits) {
    return Single.fromCallable(() -> {
      List<SubredditEntity> entities =
          FluentIterable.from(subreddits).transform(EntityConverters::toEntity).toList();
      redditNowDao.insertSubreddits(entities);
      return entities;
    });
  }

  /**
   * Returns top 5 posts from given subreddit. This will filter out those posts that has been seen
   * by the user already.
   */
  private Single<List<Submission>> getPostsForSubreddit(RedditClient redditClient,
      SubredditEntity subredditEntity) {

    return Observable.<Submission>create(emitter -> {
      Set<String> swipedIds = Sets.newHashSet(redditNowDao.getSwipedPostIds());

      SubredditReference subreddit = redditClient.subreddit(subredditEntity.name);
      Iterator<Listing<Submission>> iterator = subreddit.posts().limit(10).build().iterator();
      int count = 0;
      while (iterator.hasNext()) {
        for (Submission submission : iterator.next()) {
          if (swipedIds.contains(submission.getId())) {
            continue;
          }

          emitter.onNext(submission);
          count++;

          if (count >= NUMBER_OF_ACTIVE_POST_TO_FETCH) {
            break;
          }
        }

        if (count >= NUMBER_OF_ACTIVE_POST_TO_FETCH) {
          break;
        }
      }
      emitter.onComplete();
    }).toList();
  }

  private Observable<List<Subreddit>> getSubreddits(RedditClient redditClient) {
    return Observable.create(emitter -> {
      // clear out all subreddits
      redditNowDao.deleteAllSubreddits();

      BarebonesPaginator<Subreddit> subscribed =
          redditClient.me().subreddits("subscriber").build();

      for (Listing<Subreddit> subreddits : subscribed) {
        try {
          emitter.onNext(subreddits);
        } catch (Exception e) {
          Timber.w(e, "failed to load subreddits");
        }
      }

      emitter.onComplete();
    });
  }
}
