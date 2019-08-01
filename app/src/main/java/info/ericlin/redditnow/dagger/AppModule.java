package info.ericlin.redditnow.dagger;

import android.app.Application;
import android.content.Context;
import androidx.room.Room;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import info.ericlin.redditnow.room.RedditNowDatabase;
import javax.inject.Singleton;

@Module
public abstract class AppModule {

  @Binds
  public abstract Context context(Application application);

  @Provides
  @Singleton
  static RedditNowDatabase redditNowDatabase(Context context) {
    return Room.databaseBuilder(context, RedditNowDatabase.class, "reddit-now-db").build();
  }
}
