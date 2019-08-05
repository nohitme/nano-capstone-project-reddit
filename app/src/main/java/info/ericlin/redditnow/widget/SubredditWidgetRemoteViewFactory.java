package info.ericlin.redditnow.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import info.ericlin.redditnow.R;
import info.ericlin.redditnow.room.RedditNowDao;
import info.ericlin.redditnow.room.RedditNowDatabase;
import info.ericlin.redditnow.room.SubredditEntity;
import info.ericlin.redditnow.subreddit.SubredditActivity;
import java.util.List;

@AutoFactory
public class SubredditWidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

  private Context context;
  private final RedditNowDao redditNowDao;
  private List<SubredditEntity> subreddits;

  public SubredditWidgetRemoteViewFactory(@Provided Context context,
      @Provided RedditNowDatabase redditNowDatabase) {
    this.context = context;
    redditNowDao = redditNowDatabase.redditNowDao();
  }

  @Override
  public void onCreate() {
    // do nothing
  }

  @Override
  public void onDataSetChanged() {
    subreddits = redditNowDao.getAllSubredditsSync();
  }

  @Override
  public void onDestroy() {
    // do nothing
  }

  @Override
  public int getCount() {
    return subreddits.size();
  }

  @Override
  public RemoteViews getViewAt(int position) {
    SubredditEntity subredditEntity = subreddits.get(position);

    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.item_widget);
    remoteViews.setTextViewText(R.id.widget_name,
        context.getString(R.string.vh_subreddit_name, subredditEntity.name));
    remoteViews.setTextViewText(R.id.widget_members,
        context.getString(R.string.vh_subreddit_members, subredditEntity.numberOfSubscribers));

    Intent intent = new Intent();
    intent.putExtra(SubredditActivity.EXTRA_SUB_NAME, subredditEntity.name);
    remoteViews.setOnClickFillInIntent(R.id.widget_row, intent);

    return remoteViews;
  }

  @Override
  public RemoteViews getLoadingView() {
    return new RemoteViews(context.getPackageName(), R.layout.item_loading_row);
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }
}
