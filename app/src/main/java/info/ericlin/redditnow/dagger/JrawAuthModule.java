package info.ericlin.redditnow.dagger;

import android.content.Context;
import android.util.Log;
import dagger.Module;
import dagger.Provides;
import java.util.UUID;
import javax.inject.Singleton;
import net.dean.jraw.android.AndroidHelper;
import net.dean.jraw.android.AppInfoProvider;
import net.dean.jraw.android.ManifestAppInfoProvider;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import net.dean.jraw.android.SimpleAndroidLogAdapter;
import net.dean.jraw.http.LogAdapter;
import net.dean.jraw.http.SimpleHttpLogger;
import net.dean.jraw.oauth.AccountHelper;
import net.dean.jraw.oauth.TokenStore;

@Module
public abstract class JrawAuthModule {

  @Provides
  @Singleton
  static TokenStore tokenStore(Context context) {
    SharedPreferencesTokenStore preferencesTokenStore =
        new SharedPreferencesTokenStore(context);

    preferencesTokenStore.load();
    preferencesTokenStore.setAutoPersist(true);

    return preferencesTokenStore;
  }

  @Provides
  @Singleton
  static AccountHelper accountHelper(Context context, TokenStore tokenStore) {
    AccountHelper accountHelper =
        AndroidHelper.accountHelper(appInfoProvider(context), UUID.randomUUID(), tokenStore);

    accountHelper.onSwitch(redditClient -> {
      LogAdapter logAdapter = new SimpleAndroidLogAdapter(Log.DEBUG);

      // We're going to use the LogAdapter to write down the summaries produced by
      // SimpleHttpLogger
      redditClient.setLogger(
          new SimpleHttpLogger(SimpleHttpLogger.DEFAULT_LINE_LENGTH, logAdapter));

      return null;
    });

    return accountHelper;
  }

  private static AppInfoProvider appInfoProvider(Context context) {
    return new ManifestAppInfoProvider(context);
  }
}
