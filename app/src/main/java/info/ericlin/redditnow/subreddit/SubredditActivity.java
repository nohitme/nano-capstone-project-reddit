package info.ericlin.redditnow.subreddit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.common.base.Strings;
import dagger.android.support.DaggerAppCompatActivity;
import info.ericlin.redditnow.EventBusUtils;
import info.ericlin.redditnow.ExternalRedditIntentBuilder;
import info.ericlin.redditnow.GlideApp;
import info.ericlin.redditnow.R;
import info.ericlin.redditnow.recyclerview.LoadingDummyViewHolder;
import info.ericlin.redditnow.recyclerview.PostViewHolder;
import info.ericlin.redditnow.recyclerview.RedditListAdapter;
import info.ericlin.redditnow.recyclerview.UiUtils;
import info.ericlin.redditnow.room.SubredditEntity;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import timber.log.Timber;

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
    checkState(getIntent().hasExtra(EXTRA_SUB_NAME));

    setContentView(R.layout.activity_subreddit);
    ButterKnife.bind(this);

    String subredditName = getIntent().getStringExtra(EXTRA_SUB_NAME);
    toolbar.setTitle(getString(R.string.vh_subreddit_name, subredditName));
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolbar.setNavigationOnClickListener(t -> finish());

    subredditViewModel =
        ViewModelProviders.of(this, viewModelFactory).get(SubredditViewModel.class);

    if (savedInstanceState == null) {
      subredditViewModel.init(subredditName);
    }

    swipeRefreshLayout.setOnRefreshListener(() -> subredditViewModel.init(subredditName));

    RedditListAdapter redditListAdapter = new RedditListAdapter(eventBus);
    recyclerView.setAdapter(redditListAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    subredditViewModel.getListLiveData().observe(this, redditListAdapter::submitList);
    subredditViewModel.getInitialLoadingLiveData().observe(this, swipeRefreshLayout::setRefreshing);
    subredditViewModel.getSubredditEntityLiveData().observe(this, this::updateHeader);

    EventBusUtils.registerEventBusWithLifecycle(eventBus, this, this);
  }

  private void updateHeader(SubredditEntity subredditEntity) {
    int color = ContextCompat.getColor(this, R.color.colorAccent);
    if (!Strings.isNullOrEmpty(subredditEntity.keyColor)) {
      try {
        color = Color.parseColor(subredditEntity.keyColor);
      } catch (Exception e) {
        Timber.w(e, "failed to parse color");
      }
    }

    color = UiUtils.lighten(color, 0.5f);
    ColorDrawable colorDrawable = new ColorDrawable(color);

    String imageUrl = UiUtils.getSubredditBannerImageUrl(subredditEntity);

    GlideApp.with(this)
        .load(imageUrl)
        .centerCrop()
        .error(colorDrawable)
        .into(imageView);
  }

  @Subscribe
  public void onLoadingViewBoundEvent(LoadingDummyViewHolder.OnLoadingViewBoundEvent event) {
    subredditViewModel.loadNextPage();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onClickPostEvent(PostViewHolder.OnClickPostEvent event) {
    Intent intent = externalRedditIntentBuilder.buildIntent(event.post().url);
    if (intent != null) {
      startActivity(intent);
    }
  }
}
