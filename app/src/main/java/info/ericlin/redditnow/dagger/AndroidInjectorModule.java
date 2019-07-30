package info.ericlin.redditnow.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import info.ericlin.redditnow.main.MainActivity;
import info.ericlin.redditnow.main.MainFragment;
import info.ericlin.redditnow.main.RedditOAuthActivity;
import info.ericlin.redditnow.main.UserlessFragment;

@Module
abstract class AndroidInjectorModule {

  @ContributesAndroidInjector
  abstract MainActivity mainActivity();

  @ContributesAndroidInjector
  abstract RedditOAuthActivity redditOAuthActivity();

  @ContributesAndroidInjector
  abstract UserlessFragment userlessFragment();

  @ContributesAndroidInjector
  abstract MainFragment mainFragment();
}
