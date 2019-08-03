package info.ericlin.redditnow;

import android.content.Context;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.Excludes;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import java.io.InputStream;
import javax.inject.Inject;
import okhttp3.OkHttpClient;

@com.bumptech.glide.annotation.GlideModule
@Excludes({ com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule.class })
@SuppressWarnings("unused")
public class GlideModule extends AppGlideModule {

  @Inject
  OkHttpClient okHttpClient;

  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide,
      @NonNull Registry registry) {

    RedditNowApp redditNowApp = (RedditNowApp) context.getApplicationContext();
    redditNowApp.getAppComponent().inject(this);

    registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
  }

  @Override
  public boolean isManifestParsingEnabled() {
    return false;
  }
}
