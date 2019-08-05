package info.ericlin.redditnow.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;
import dagger.android.AndroidInjection;
import javax.inject.Inject;

public class SubredditWidgetService extends RemoteViewsService {

  @Inject
  SubredditWidgetRemoteViewFactoryFactory factory;

  @Override
  public void onCreate() {
    AndroidInjection.inject(this);
    super.onCreate();
  }

  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return factory.create();
  }
}
