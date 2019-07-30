package info.ericlin.redditnow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import dagger.android.support.DaggerAppCompatActivity;
import javax.inject.Inject;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import net.dean.jraw.oauth.AccountHelper;
import timber.log.Timber;

public class MainActivity extends DaggerAppCompatActivity {

  public static final int REQUEST_CODE = 2191;

  @Inject AccountHelper accountHelper;
  @Inject SharedPreferencesTokenStore tokenStore;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    View button = findViewById(R.id.main_button);
    button.setOnClickListener(view -> startOAuthFlow());
  }

  private void startOAuthFlow() {
    Intent intent = new Intent(this, RedditOAuthActivity.class);
    startActivityForResult(intent, REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == REQUEST_CODE) {
      Timber.i("eric, onActivityResult -> %s", resultCode);
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }
}
