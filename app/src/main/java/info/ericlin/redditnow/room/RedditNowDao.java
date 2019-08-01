package info.ericlin.redditnow.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Flowable;
import java.util.List;

@Dao
public abstract class RedditNowDao {

  // subreddit

  @Query("SELECT * FROM SubredditEntity")
  public abstract Flowable<List<SubredditEntity>> getAllSubreddits();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public abstract void insertSubreddits(List<SubredditEntity> subredditEntities);

  @Query("DELETE FROM SubredditEntity")
  public abstract void deleteAllSubreddits();

  @Transaction
  public void replaceAllSubreddits(List<SubredditEntity> subredditEntities) {
    deleteAllSubreddits();
    insertSubreddits(subredditEntities);
  }

  // posts

  @Query("SELECT * FROM PostEntity")
  public abstract Flowable<List<PostEntity>> getAllPosts();

  @Query("SELECT * FROM PostEntity WHERE id NOT IN (SELECT id FROM SwipedPostEntity)")
  public abstract Flowable<List<PostEntity>> getAllActivePosts();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public abstract void insertPosts(List<PostEntity> postEntities);

  @Query("DELETE FROM PostEntity")
  public abstract void deleteAllPosts();

  @Query("SELECT id FROM SwipedPostEntity")
  public abstract List<String> getSwipedPostIds();

  @Transaction
  public void replaceAllPosts(List<PostEntity> postEntities) {
    deleteAllPosts();
    insertPosts(postEntities);
  }
}
