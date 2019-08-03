package info.ericlin.redditnow.dagger;

import android.app.Application;
import android.content.Context;
import androidx.room.Room;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import info.ericlin.redditnow.room.RedditNowDatabase;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import org.greenrobot.eventbus.EventBus;

@Module
public abstract class AppModule {

  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
  private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
  private static final int KEEP_ALIVE = 30;

  @Binds
  public abstract Context context(Application application);

  @Provides
  @Singleton
  static RedditNowDatabase redditNowDatabase(Context context,
      ThreadPoolExecutor threadPoolExecutor) {
    return Room.databaseBuilder(context, RedditNowDatabase.class, "reddit-now-db")
        .setQueryExecutor(threadPoolExecutor)
        .setTransactionExecutor(threadPoolExecutor)
        .build();
  }

  @Provides
  @Singleton
  static ThreadPoolExecutor threadPoolExecutor() {
    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("reddit-io-%s").build();

    return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
        TimeUnit.SECONDS, new LinkedBlockingDeque<>(128), threadFactory);
  }

  // don't use singleton, limit to injection site.
  @Provides
  static EventBus eventBus(ThreadPoolExecutor threadPoolExecutor) {
    return EventBus.builder().executorService(threadPoolExecutor).build();
  }
}
