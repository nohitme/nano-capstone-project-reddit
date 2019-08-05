package info.ericlin.redditnow.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import info.ericlin.redditnow.R;
import info.ericlin.redditnow.subreddit.SubredditActivity;
import javax.annotation.Nonnegative;

public class SubredditWidgetProvider extends AppWidgetProvider {

  public static void updateWidget(@Nonnegative Context context) {
    Intent intent = new Intent(context, SubredditWidgetProvider.class);
    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    ComponentName componentName = new ComponentName(context, SubredditWidgetProvider.class);
    int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
    context.sendBroadcast(intent);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // update each of the app widgets with the remote adapter
    for (int appWidgetId : appWidgetIds) {

      Intent intent = new Intent(context, SubredditWidgetService.class);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
      remoteViews.setRemoteAdapter(R.id.widget_list, intent);
      remoteViews.setEmptyView(R.id.widget_list, R.id.widget_empty_view);

      Intent startActivityIntent = new Intent(context, SubredditActivity.class);
      PendingIntent startActivityPendingIntent =
          PendingIntent.getActivity(context, 0, startActivityIntent,
              PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setPendingIntentTemplate(R.id.widget_list, startActivityPendingIntent);

      appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }
}
