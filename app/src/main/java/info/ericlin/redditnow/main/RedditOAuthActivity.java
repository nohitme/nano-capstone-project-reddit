package info.ericlin.redditnow.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import info.ericlin.redditnow.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import net.dean.jraw.oauth.AccountHelper;
import net.dean.jraw.oauth.StatefulAuthHelper;
import timber.log.Timber;

/**
 * Performs OAuth2 needed for using Reddit API. It contains a web view to display reddit login flow.
 */
public class RedditOAuthActivity extends DaggerAppCompatActivity {

  private static final String[] OAUTH_SCOPES =
      new String[] { "identity", "read", "mysubreddits", "subscribe", "vote" };

  private final CompositeDisposable disposables = new CompositeDisposable();

  @Inject
  AccountHelper accountHelper;
  @BindView(R.id.oauth_web_view)
  WebView webView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reddit_oauth);
    ButterKnife.bind(this);

    webView.clearCache(true);
    webView.clearHistory();

    CookieManager.getInstance().removeAllCookies(null);
    CookieManager.getInstance().flush();

    // Get a StatefulAuthHelper instance to manage interactive authentication
    final StatefulAuthHelper helper = accountHelper.switchToNewUser();

    // Watch for pages loading
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Timber.i("eric, onPageStarted -> %s", url);
        if (helper.isFinalRedirectUrl(url)) {
          // No need to continue loading, we've already got all the required information
          webView.stopLoading();
          webView.setVisibility(View.GONE);
          startUserChallenge(helper, url);
        }
      }
    });

    String authUrl =
        helper.getAuthorizationUrl(/* requestRefreshToken= */ true, /* useMobileSite= */ true,
            OAUTH_SCOPES);

    // Finally, show the authorization URL to the user
    webView.loadUrl(authUrl);
  }

  @Override
  protected void onDestroy() {
    disposables.clear();
    super.onDestroy();
  }

  private void startUserChallenge(StatefulAuthHelper helper, String redirectUrl) {
    Observable<Boolean> oauthObservable = Observable.create(emitter -> {
      try {
        Timber.i("eric, startUserChallenge -> %s", redirectUrl);
        helper.onUserChallenge(redirectUrl);
        emitter.onNext(true);
        emitter.onComplete();
      } catch (Exception e) {
        Timber.e(e, "failed to log in user");
        emitter.tryOnError(e);
      }
    });

    Disposable disposable = oauthObservable.subscribeOn(Schedulers.io())
        .onErrorReturnItem(false)
        .doOnEach(notification -> Timber.d("eric, %s", notification))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onLoginCompleted);

    disposables.add(disposable);
  }

  private void onLoginCompleted(boolean success) {
    if (success) {
      Toast.makeText(this, R.string.oauth_login_success,
          Toast.LENGTH_SHORT).show();
      setResult(Activity.RESULT_OK);
    } else {
      Toast.makeText(this, R.string.oauth_login_failed,
          Toast.LENGTH_SHORT).show();
      setResult(Activity.RESULT_CANCELED);
    }

    finish();
  }
}
