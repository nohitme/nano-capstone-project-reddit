package info.ericlin.redditnow;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.Reusable;
import info.ericlin.redditnow.settings.PreferenceManager;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Helps to find which app to launch external intents
 */
@Reusable
public class ExternalRedditIntentBuilder {

  private static final String REDDIT_OFFICIAL_PACKAGE_NAME = "com.reddit.frontpage";

  private Context context;
  private PreferenceManager preferenceManager;

  @Inject
  public ExternalRedditIntentBuilder(Context context, PreferenceManager preferenceManager) {
    this.context = context;
    this.preferenceManager = preferenceManager;
  }

  @Nullable
  public Intent buildIntent(@NonNull String postUrl) {
    final Uri uri;
    try {
      uri = Uri.parse(postUrl);
    } catch (Exception e) {
      Timber.w(e, "failed to parse post url");
      return null;
    }

    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    PackageManager packageManager = context.getPackageManager();
    List<ResolveInfo> activities =
        packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    if (activities.isEmpty()) {
      return null;
    }

    return intent;
  }
}
