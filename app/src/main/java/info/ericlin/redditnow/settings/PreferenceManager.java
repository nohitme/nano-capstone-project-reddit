package info.ericlin.redditnow.settings;

import android.content.Context;
import android.content.SharedPreferences;
import dagger.Reusable;
import javax.inject.Inject;
import timber.log.Timber;

@Reusable
public class PreferenceManager {

  private static final int DEFAULT_NUMBER_TO_FETCH = 5;
  private static final int DEFAULT_NUMBER_TO_SHOW = 2;

  private final SharedPreferences sharedPreferences;

  @Inject
  public PreferenceManager(Context context) {
    sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
  }

  public final int getNumberPostToFetch() {
    String number_to_fetch =
        sharedPreferences.getString("number_to_fetch", String.valueOf(DEFAULT_NUMBER_TO_FETCH));
    try {
      return Integer.parseInt(number_to_fetch);
    } catch (NumberFormatException e) {
      Timber.w(e, "failed to parse number to fetch");
      return DEFAULT_NUMBER_TO_FETCH;
    }
  }

  public final int getNumberPostToShow() {
    String number_to_show =
        sharedPreferences.getString("number_to_show", String.valueOf(DEFAULT_NUMBER_TO_SHOW));
    try {
      return Integer.parseInt(number_to_show);
    } catch (NumberFormatException e) {
      Timber.w(e, "failed to parse number to show");
      return DEFAULT_NUMBER_TO_SHOW;
    }
  }
}
