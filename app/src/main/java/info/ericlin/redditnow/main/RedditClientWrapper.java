package info.ericlin.redditnow.main;

import androidx.annotation.NonNull;
import com.google.common.base.Preconditions;
import dagger.Reusable;
import javax.inject.Inject;
import net.dean.jraw.RedditClient;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import net.dean.jraw.oauth.AccountHelper;

/**
 * A wrapper class for getting {@link RedditClient} using first authenticated username.
 */
@Reusable
public class RedditClientWrapper {

  private final SharedPreferencesTokenStore tokenStore;
  private final AccountHelper accountHelper;

  @Inject
  public RedditClientWrapper(SharedPreferencesTokenStore tokenStore, AccountHelper accountHelper) {
    this.tokenStore = tokenStore;
    this.accountHelper = accountHelper;
  }

  @NonNull
  public RedditClient get() {
    if (accountHelper.isAuthenticated()) {
      return accountHelper.getReddit();
    }

    Preconditions.checkArgument(!tokenStore.getUsernames().isEmpty(),
        "must have a least one authenticated user");

    String username = tokenStore.getUsernames().get(0);
    RedditClient redditClient = accountHelper.trySwitchToUser(username);
    if (redditClient == null) {
      throw new IllegalStateException("cannot get non-null reddit client");
    }

    return redditClient;
  }
}
