package info.ericlin.redditnow.recyclerview;

import android.graphics.Color;
import android.webkit.URLUtil;
import androidx.annotation.ColorInt;
import com.google.common.base.Strings;
import info.ericlin.redditnow.room.SubredditEntity;

public class UiUtils {

  private static final String DEFAULT_BANNER_URL =
      "https://redditupvoted.files.wordpress.com/2017/02/upvoted_banner-green-plain.jpg";

  private UiUtils() {
    //no instance
  }

  // reference: https://gist.github.com/martintreurnicht/f6bbb20a43211bc2060e
  public static int lighten(@ColorInt int color, double fraction) {
    int red = Color.red(color);
    int green = Color.green(color);
    int blue = Color.blue(color);
    red = lightenColor(red, fraction);
    green = lightenColor(green, fraction);
    blue = lightenColor(blue, fraction);
    int alpha = Color.alpha(color);
    return Color.argb(alpha, red, green, blue);
  }

  public static int darken(@ColorInt int color, double fraction) {
    int red = Color.red(color);
    int green = Color.green(color);
    int blue = Color.blue(color);
    red = darkenColor(red, fraction);
    green = darkenColor(green, fraction);
    blue = darkenColor(blue, fraction);
    int alpha = Color.alpha(color);

    return Color.argb(alpha, red, green, blue);
  }

  private static int darkenColor(@ColorInt int color, double fraction) {
    return (int) Math.max(color - (color * fraction), 0);
  }

  private static int lightenColor(@ColorInt int color, double fraction) {
    return (int) Math.min(color + (color * fraction), 255);
  }

  public static String getSubredditBannerImageUrl(SubredditEntity subredditEntity) {
    if (URLUtil.isValidUrl(Strings.nullToEmpty(subredditEntity.bannerImageUrl))) {
      return subredditEntity.bannerImageUrl;
    }

    return DEFAULT_BANNER_URL;
  }
}
