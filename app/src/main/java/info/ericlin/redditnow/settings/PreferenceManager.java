package info.ericlin.redditnow.settings;

import android.content.Context;
import android.content.SharedPreferences;
import dagger.Reusable;
import javax.inject.Inject;

@Reusable
public class PreferenceManager {

  private static final String SETTINGS_PREF = "settings_pref";

  private final SharedPreferences sharedPreferences;

  @Inject
  public PreferenceManager(Context context) {
    sharedPreferences = context.getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE);
  }

  public final int getNumberPostToPrefetch() {
    return sharedPreferences.getInt("number_of_prefetch", 5);
  }

  public final int getNumberPostToShow() {
    return sharedPreferences.getInt("number_of_show", 3);
  }

  public final boolean isRedditAppPreferred() {
    return sharedPreferences.getBoolean("prefer_reddit_app", true);
  }
}
