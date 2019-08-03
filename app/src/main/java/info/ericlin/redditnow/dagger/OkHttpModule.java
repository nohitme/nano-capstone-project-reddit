package info.ericlin.redditnow.dagger;

import android.content.Context;
import android.net.TrafficStats;
import dagger.Module;
import dagger.Provides;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.inject.Singleton;
import javax.net.SocketFactory;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module
class OkHttpModule {

  @Provides
  @Singleton
  OkHttpClient okHttpClient(Context context) {

    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    // cache
    int cacheSize = 64 * 1024 * 1024; // 64 MiB
    Cache cache = new Cache(context.getCacheDir(), cacheSize);
    builder.cache(cache);

    // just to get rid of strict mode warning
    // reference:
    builder.socketFactory(new TagSocketFactory());

    // interceptors
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(Timber::v);
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

    return builder.addNetworkInterceptor(loggingInterceptor).build();
  }

  /**
   * A socket factory delegates actual call to {@link SocketFactory#getDefault()} and sets the
   * thread stats to get rid of strict mode warnings.
   */
  private static class TagSocketFactory extends SocketFactory {

    // by default okhttp only use parameter-less method
    // see:
    // https://square.github.io/okhttp/3.x/okhttp/okhttp3/OkHttpClient.Builder.html#socketFactory-javax.net.SocketFactory-
    static final String DEFAULT_ERROR_MESSAGE = "okhttp should not call this method";

    @Override
    public Socket createSocket() throws IOException {
      Socket socket = SocketFactory.getDefault().createSocket();
      TrafficStats.setThreadStatsTag(TagSocketFactory.class.hashCode());
      return socket;
    }

    @Override
    public Socket createSocket(String host, int port) {
      throw new UnsupportedOperationException(DEFAULT_ERROR_MESSAGE);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) {
      throw new UnsupportedOperationException(DEFAULT_ERROR_MESSAGE);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) {
      throw new UnsupportedOperationException(DEFAULT_ERROR_MESSAGE);
    }

    @Override
    public Socket createSocket(
        InetAddress address, int port, InetAddress localAddress, int localPort) {
      throw new UnsupportedOperationException(DEFAULT_ERROR_MESSAGE);
    }
  }
}
