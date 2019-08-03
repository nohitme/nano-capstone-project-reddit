package info.ericlin.redditnow.dagger;

import android.app.Application;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import info.ericlin.redditnow.GlideModule;
import info.ericlin.redditnow.RedditNowApp;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
    AndroidInjectionModule.class,
    AppModule.class,
    AndroidInjectorModule.class,
    OkHttpModule.class,
    JrawAuthModule.class,
    ViewModelModule.class,
})
public interface AppComponent {

  void inject(RedditNowApp redditNowApp);

  void inject(GlideModule glideModule);

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder application(Application application);

    AppComponent build();
  }
}
