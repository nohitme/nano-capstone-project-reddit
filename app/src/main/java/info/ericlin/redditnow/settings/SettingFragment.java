package info.ericlin.redditnow.settings;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import dagger.android.support.AndroidSupportInjection;
import info.ericlin.redditnow.R;
import javax.inject.Inject;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import timber.log.Timber;

public class SettingFragment extends PreferenceFragmentCompat {

  @Inject
  SharedPreferencesTokenStore tokenStore;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    AndroidSupportInjection.inject(this);
    super.onCreate(savedInstanceState);
    Preference preference = findPreference("account_logout");
    String username = FluentIterable.from(tokenStore.getUsernames()).first().orNull();
    if (Strings.isNullOrEmpty(username)) {
      getPreferenceScreen().removePreference(preference);
    } else {
      preference.setSummary(getString(R.string.pref_account_summary, username));
      preference.setOnPreferenceClickListener(p -> {
        showLogoutDialog();
        return true;
      });
    }

    getPreferenceManager().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener((sharedPreferences, s) -> {
          Timber.i("preference %s changed to %s", sharedPreferences, s);
          SettingActivity activity = (SettingActivity) requireActivity();
          activity.onPreferenceChanged();
        });
  }

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
