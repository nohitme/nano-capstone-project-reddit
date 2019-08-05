package info.ericlin.redditnow.main;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import dagger.android.support.DaggerAppCompatActivity;
import info.ericlin.redditnow.R;
import javax.inject.Inject;
import net.dean.jraw.android.SharedPreferencesTokenStore;

public class MainActivity extends DaggerAppCompatActivity
    implements UserlessFragment.OnRedditLoginSuccessListener {

  private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

  @Inject
  SharedPreferencesTokenStore tokenStore;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (tokenStore.getUsernames().isEmpty()) {
      replaceFragment(new UserlessFragment());
    } else if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) == null) {
      replaceFragment(new MainFragment());
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
