package info.ericlin.redditnow.dagger;

import android.app.Application;
import android.content.Context;
import dagger.Binds;
import dagger.Module;

@Module
public abstract class AppModule {

  @Binds
  public abstract Context context(Application application);
}
