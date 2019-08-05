package info.ericlin.redditnow.dagger;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import info.ericlin.redditnow.main.MainActivity;
import info.ericlin.redditnow.main.MainFragment;
import info.ericlin.redditnow.main.RedditOAuthActivity;
import info.ericlin.redditnow.main.UserlessFragment;
import info.ericlin.redditnow.search.SearchActivity;
import info.ericlin.redditnow.settings.SettingActivity;
import info.ericlin.redditnow.settings.SettingFragment;
import info.ericlin.redditnow.subreddit.SubredditActivity;
import info.ericlin.redditnow.widget.SubredditWidgetService;

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

  @ContributesAndroidInjector
  abstract SubredditActivity subredditActivity();

  @ContributesAndroidInjector
  abstract SearchActivity searchActivity();

  @ContributesAndroidInjector
  abstract SettingActivity settingActivity();

  @ContributesAndroidInjector
  abstract SettingFragment settingFragment();

  @ContributesAndroidInjector
  abstract SubredditWidgetService subredditWidgetService();
}
