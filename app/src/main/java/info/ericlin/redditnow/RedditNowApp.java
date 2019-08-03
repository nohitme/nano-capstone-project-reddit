package info.ericlin.redditnow;

import android.app.Application;
import androidx.annotation.NonNull;
import com.facebook.stetho.Stetho;
import com.jakewharton.threetenabp.AndroidThreeTen;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import info.ericlin.redditnow.dagger.AppComponent;
import info.ericlin.redditnow.dagger.DaggerAppComponent;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.ThreadPoolExecutor;
import javax.inject.Inject;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

public class RedditNowApp extends Application implements HasAndroidInjector {

  @Inject
  DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

  @Inject
  ThreadPoolExecutor threadPoolExecutor;

  private AppComponent appComponent;

  @Override
  public void onCreate() {
    super.onCreate();

    appComponent = DaggerAppComponent.builder().application(this).build();
    appComponent.inject(this);

    Stetho.initializeWithDefaults(this);

    Timber.uprootAll();
    Timber.plant(new ThreadNameDebugTree());

    RxJavaPlugins.setInitIoSchedulerHandler(
        schedulerCallable -> Schedulers.from(threadPoolExecutor));

    AndroidThreeTen.init(this);
  }

  @Override
  public AndroidInjector<Object> androidInjector() {
    return dispatchingAndroidInjector;
  }

  @NonNull
  public AppComponent getAppComponent() {
    return checkNotNull(appComponent);
  }
}
