package info.ericlin.redditnow.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class RedditNowDao {

    // subreddit

    @Query("SELECT * FROM SubredditEntity")
    public abstract List<SubredditEntity> getAllSubredditsSync();

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
        deleteAllPosts();
    }

    // posts

    @Query("SELECT * FROM PostEntity WHERE id NOT IN (SELECT id FROM SwipedPostEntity)")
    public abstract Flowable<List<PostEntity>> getAllActivePosts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertPosts(List<PostEntity> postEntities);

    @Query("DELETE FROM PostEntity")
    public abstract void deleteAllPosts();

    @Query("SELECT id FROM SwipedPostEntity")
    public abstract List<String> getSwipedPostIds();

    // swiped posts

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertSwipedPosts(SwipedPostEntity... swipedPostEntities);
}
