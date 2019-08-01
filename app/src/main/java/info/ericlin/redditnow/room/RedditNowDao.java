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
  void insertSubreddits(SubredditEntity... subredditEntities);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertSubreddits(List<SubredditEntity> subredditEntities);

  @Query("SELECT * FROM PostEntity WHERE is_swiped IS 1")
  Flowable<List<PostEntity>> getAllActivePosts();
}
