package info.ericlin.redditnow.subreddit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import info.ericlin.redditnow.ExternalRedditIntentBuilder;
import info.ericlin.redditnow.R;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;

import static com.google.common.base.Preconditions.checkState;

public class SubredditActivity extends DaggerAppCompatActivity {

  private static final String EXTRA_SUB_NAME = "EXTRA_SUB_NAME";

  @BindView(R.id.subreddit_toolbar)
  Toolbar toolbar;

  @BindView(R.id.subreddit_recycler_view)
  RecyclerView recyclerView;

  @BindView(R.id.subreddit_swipe_refresh)
  SwipeRefreshLayout swipeRefreshLayout;

  @BindView(R.id.subreddit_toolbar_image)
  ImageView imageView;

  @Inject
  ViewModelProvider.Factory viewModelFactory;

  @Inject
  EventBus eventBus;

  @Inject
  ExternalRedditIntentBuilder externalRedditIntentBuilder;
  private SubredditViewModel subredditViewModel;

  @NonNull
  public static Intent newIntent(@NonNull Context context, @NonNull String subredditName) {
    Intent intent = new Intent(context, SubredditActivity.class);
    intent.putExtra(EXTRA_SUB_NAME, subredditName);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_subreddit);
    ButterKnife.bind(this);

    checkState(getIntent().hasExtra(EXTRA_SUB_NAME));
    subredditViewModel =
        ViewModelProviders.of(this, viewModelFactory).get(SubredditViewModel.class);

    if (savedInstanceState == null) {
      String subredditName = getIntent().getStringExtra(EXTRA_SUB_NAME);
      subredditViewModel.loadSubreddit(subredditName);
    }
  }
}
