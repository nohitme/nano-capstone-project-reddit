package info.ericlin.redditnow.room;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = { SubredditEntity.class, PostEntity.class }, version = 1)
@TypeConverters({ Converters.class })
public abstract class RedditNowDatabase extends RoomDatabase {

  @NonNull
  public abstract RedditNowDao redditNowDao();
}
