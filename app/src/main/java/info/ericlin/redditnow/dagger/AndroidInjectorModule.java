package info.ericlin.redditnow.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import info.ericlin.redditnow.MainActivity;
import info.ericlin.redditnow.MainFragment;
import info.ericlin.redditnow.RedditOAuthActivity;
import info.ericlin.redditnow.UserlessFragment;

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
