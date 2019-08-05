package info.ericlin.redditnow.settings;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.firebase.analytics.FirebaseAnalytics;
import dagger.android.support.AndroidSupportInjection;
import info.ericlin.redditnow.R;
import java.util.List;
import javax.inject.Inject;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import timber.log.Timber;

public class SettingFragment extends PreferenceFragmentCompat {

  @Inject
  SharedPreferencesTokenStore tokenStore;

  @Inject
  FirebaseAnalytics firebaseAnalytics;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    AndroidSupportInjection.inject(this);
    super.onCreate(savedInstanceState);
    Preference accountPreference = findPreference("account_logout");
    String username = FluentIterable.from(tokenStore.getUsernames()).first().orNull();
    if (Strings.isNullOrEmpty(username)) {
      getPreferenceScreen().removePreference(accountPreference);
    } else {
      accountPreference.setSummary(getString(R.string.pref_account_summary, username));
      accountPreference.setOnPreferenceClickListener(p -> {
        showLogoutDialog();
        return true;
      });
    }

    List<String> keys = Lists.newArrayList("number_to_show", "number_to_fetch");
    for (String key : keys) {
      Preference preference = findPreference(key);
      if (preference != null) {
        preference.setOnPreferenceChangeListener(onPreferenceChangeListener);
      }
    }
  }

  private final Preference.OnPreferenceChangeListener onPreferenceChangeListener =
      (preference, newValue) -> {
        Timber.i("preference %s changed %s", preference.getKey(), newValue);
        Bundle bundle = new Bundle();
        bundle.putString(preference.getKey(), String.valueOf(newValue));
        firebaseAnalytics.logEvent("preferences_changed", bundle);
        SettingActivity activity = (SettingActivity) requireActivity();
        activity.onPreferenceChanged();
        return true;
      };

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.preference_settings, null);
  }

  private void showLogoutDialog() {
    new AlertDialog.Builder(requireContext()).setMessage(R.string.pref_account_message)
        .setPositiveButton(R.string.pref_account_yes, (dialogInterface, i) -> {
          SettingActivity activity = (SettingActivity) requireActivity();
          activity.confirmToLogout();
        })
        .setNegativeButton(android.R.string.cancel, null)
        .show();
  }
}
