package info.ericlin.redditnow;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.DaggerFragment;
import javax.inject.Inject;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import net.dean.jraw.oauth.AccountHelper;

public class MainActivity extends DaggerAppCompatActivity implements
    UserlessFragment.OnRedditLoginSuccessListener {

  private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

  @Inject
  AccountHelper accountHelper;
  @Inject
  SharedPreferencesTokenStore tokenStore;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) == null) {
      DaggerFragment fragment =
          tokenStore.getUsernames().isEmpty() ? new UserlessFragment() : new MainFragment();
      replaceFragment(fragment);
    }
  }

  @Override
  public void onLoginSuccess() {
    replaceFragment(new MainFragment());
  }

  private void replaceFragment(Fragment fragment) {
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.main_fragment_container, fragment, FRAGMENT_TAG)
        .commitNow();
  }
}
