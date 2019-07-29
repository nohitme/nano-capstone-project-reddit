package info.ericlin.redditnow.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import info.ericlin.redditnow.MainActivity;
import info.ericlin.redditnow.RedditOAuthActivity;

@Module
public abstract class AndroidInjectorModule {

  @ContributesAndroidInjector
  abstract MainActivity mainActivity();

  @ContributesAndroidInjector
  abstract RedditOAuthActivity redditOAuthActivity();
}
