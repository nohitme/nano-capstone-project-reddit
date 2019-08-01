package info.ericlin.redditnow;

import android.app.Application;
import com.facebook.stetho.Stetho;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import info.ericlin.redditnow.dagger.DaggerAppComponent;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.ThreadPoolExecutor;
import javax.inject.Inject;
import timber.log.Timber;

public class RedditNowApp extends Application implements HasAndroidInjector {

  @Inject
  DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

  @Inject
  ThreadPoolExecutor threadPoolExecutor;

  @Override
  public void onCreate() {
    super.onCreate();

    DaggerAppComponent.builder()
        .application(this)
        .build()
        .inject(this);

    Stetho.initializeWithDefaults(this);

    Timber.uprootAll();
    Timber.plant(new ThreadNameDebugTree());

    RxJavaPlugins.setInitIoSchedulerHandler(
        schedulerCallable -> Schedulers.from(threadPoolExecutor));
  }

  @Override
  public AndroidInjector<Object> androidInjector() {
    return dispatchingAndroidInjector;
  }
}
