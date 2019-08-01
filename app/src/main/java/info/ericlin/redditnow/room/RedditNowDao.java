package info.ericlin.redditnow.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Flowable;
import java.util.List;

@Dao
public interface RedditNowDao {

  @Query("SELECT * FROM SubredditEntity")
  Flowable<List<SubredditEntity>> getAllSubreddits();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertSubreddits(List<SubredditEntity> subredditEntities);

  @Query("DELETE FROM SubredditEntity")
  void deleteAllSubreddits();

  @Query("SELECT * FROM PostEntity")
  Flowable<List<PostEntity>> getAllPosts();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertPosts(List<PostEntity> postEntities);

  @Query("DELETE FROM PostEntity")
  void deleteAllPost();

  @Query("SELECT id FROM SwipedPostEntity")
  List<String> getSwipedPostIds();
}
