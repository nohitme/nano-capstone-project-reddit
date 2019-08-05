package info.ericlin.redditnow.settings;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import info.ericlin.redditnow.R;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import net.dean.jraw.oauth.AccountHelper;

public class SettingActivity extends DaggerAppCompatActivity {

  public static final int SETTINGS_LOGOUT = 12312;
  public static final int SETTINGS_CHANGED = 1213;

  private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

  private CompositeDisposable disposables = new CompositeDisposable();

  @BindView(R.id.settings_toolbar)
  Toolbar toolbar;

  @Inject
  AccountHelper accountHelper;

  @Inject
  SharedPreferencesTokenStore tokenStore;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    ButterKnife.bind(this);

    toolbar.setTitle(R.string.menu_settings);
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolbar.setNavigationOnClickListener(view -> finish());

    if (savedInstanceState == null) {
      if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) == null) {
        getSupportFragmentManager().beginTransaction()
            .add(R.id.settings_fragment_container, new SettingFragment(), FRAGMENT_TAG)
            .commitNow();
      }
    }
  }

  public void confirmToLogout() {
    Disposable disposable = Completable.fromAction(() -> {
      accountHelper.logout();
      tokenStore.clear();
      tokenStore.persist();
    })
        .subscribeOn(Schedulers.io())
        .onErrorComplete()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> {
          setResult(SETTINGS_LOGOUT);
          finish();
        });

    disposables.add(disposable);
  }

  public void onPreferenceChanged() {
    setResult(SETTINGS_CHANGED);
  }
}
