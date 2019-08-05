package info.ericlin.redditnow.main;

import androidx.annotation.NonNull;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import info.ericlin.redditnow.room.EntityConverters;
import info.ericlin.redditnow.room.PostEntity;
import info.ericlin.redditnow.room.RedditNowDao;
import info.ericlin.redditnow.room.RedditNowDatabase;
import info.ericlin.redditnow.room.SubredditEntity;
import info.ericlin.redditnow.settings.PreferenceManager;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
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
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
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

  private final RedditClientWrapper redditClientWrapper;
  private final RedditNowDao redditNowDao;
  private final PreferenceManager preferenceManager;

  @Inject
  MainFeedDataManager(RedditClientWrapper redditClientWrapper, RedditNowDatabase redditNowDatabase,
      PreferenceManager preferenceManager) {
    this.redditClientWrapper = redditClientWrapper;
    this.redditNowDao = redditNowDatabase.redditNowDao();
    this.preferenceManager = preferenceManager;
  }

  @NonNull
  public Completable updateHomeFeed() {
    RedditClient redditClient = redditClientWrapper.get();

    return getSubreddits(redditClient)
        .doOnNext(subreddits -> Timber.i("get %s subreddits", subreddits.size()))
        .subscribeOn(Schedulers.io())
        .flatMapSingle(this::saveSubreddits)
        .flatMapIterable(entities -> entities)
        .flatMapSingle(subredditEntity -> getPostsForSubreddit(redditClient, subredditEntity))
        .doOnNext(submissions -> Timber.i("get %s submissions", submissions.size()))
        .flatMapCompletable(this::savePosts)
        .onErrorComplete(throwable -> {
          Timber.w(throwable, "failed to update home feed");
          return true;
        });
  }

  private Completable savePosts(List<Submission> submissionLists) {
    return Completable.fromAction(() -> {
      List<PostEntity> entities = FluentIterable.from(submissionLists)
              .transform(EntityConverters::toEntity)
              .toList();
      redditNowDao.insertPosts(entities);
    });
  }

  private Single<List<SubredditEntity>> saveSubreddits(List<Subreddit> subreddits) {
    return Single.fromCallable(() -> {
      List<SubredditEntity> entities =
          FluentIterable.from(subreddits).transform(EntityConverters::toEntity).toList();
      redditNowDao.replaceAllSubreddits(entities);
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

      int numberPostToPrefetch = preferenceManager.getNumberPostToFetch();
      int queryLimit = (int) (numberPostToPrefetch * 1.5);

      Iterator<Listing<Submission>> iterator =
          subreddit.posts()
              .sorting(SubredditSort.HOT)
              .timePeriod(TimePeriod.DAY)
              .limit(queryLimit)
              .build()
              .iterator();

      int count = 0;
      outerIterator:
      while (iterator.hasNext()) {
        for (Submission submission : iterator.next()) {
          if (swipedIds.contains(submission.getId())) {
            continue;
          }

          emitter.onNext(submission);
          count++;

          if (count >= numberPostToPrefetch) {
            break outerIterator;
          }
        }
      }
      emitter.onComplete();
    }).toList();
  }

  private Observable<List<Subreddit>> getSubreddits(RedditClient redditClient) {
    return Observable.create(emitter -> {
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
