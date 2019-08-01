package info.ericlin.redditnow.room;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
    SubredditEntity.class, PostEntity.class, SwipedPostEntity.class
}, version = 2)
@androidx.room.TypeConverters({ TypeConverters.class })
public abstract class RedditNowDatabase extends RoomDatabase {

  @NonNull
  public abstract RedditNowDao redditNowDao();
}
