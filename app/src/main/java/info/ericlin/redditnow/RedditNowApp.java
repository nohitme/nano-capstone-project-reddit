package info.ericlin.redditnow;

import android.app.Application;
import com.facebook.stetho.Stetho;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import info.ericlin.redditnow.dagger.DaggerAppComponent;
import javax.inject.Inject;
import timber.log.Timber;

public class RedditNowApp extends Application implements HasAndroidInjector {

  @Inject DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

  @Override public void onCreate() {
    super.onCreate();

    DaggerAppComponent.builder()
        .application(this)
        .build()
        .inject(this);

    Stetho.initializeWithDefaults(this);

    Timber.uprootAll();
    Timber.plant(new ThreadNameDebugTree());
  }

  @Override public AndroidInjector<Object> androidInjector() {
    return dispatchingAndroidInjector;
  }
}
