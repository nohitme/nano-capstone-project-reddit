package info.ericlin.redditnow.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import info.ericlin.redditnow.ExternalRedditIntentBuilder;
import info.ericlin.redditnow.R;
import info.ericlin.redditnow.recyclerview.PostViewHolder;
import info.ericlin.redditnow.recyclerview.RedditListAdapter;
import info.ericlin.redditnow.recyclerview.RedditListItem;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Displays a list of subreddits that the user follows and top N posts of each subreddit.
 */
public class MainFragment extends DaggerFragment {

  @Nullable
  private Unbinder unbinder;

  @BindView(R.id.main_toolbar)
  Toolbar toolbar;

  @BindView(R.id.main_recycler_view)
  RecyclerView recyclerView;

  @BindView(R.id.main_swipe_refresh)
  SwipeRefreshLayout swipeRefreshLayout;

  @Inject
  ViewModelProvider.Factory viewModelFactory;

  @Inject
  EventBus eventBus;

  @Inject
  ExternalRedditIntentBuilder externalRedditIntentBuilder;

  private MainViewModel mainViewModel;
  private RedditListAdapter redditListAdapter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);
    eventBus.register(this);

    toolbar.setTitle(R.string.app_name);
    toolbar.setTitleTextColor(Color.WHITE);

    redditListAdapter = new RedditListAdapter(eventBus);
    recyclerView.setAdapter(redditListAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    itemTouchHelper.attachToRecyclerView(recyclerView);

    mainViewModel.getRedditListItemLiveData()
        .observe(getViewLifecycleOwner(), redditListAdapter::submitList);

    mainViewModel.isLoadingLiveData()
        .observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing);
    swipeRefreshLayout.setOnRefreshListener(() -> mainViewModel.updateHomeFeed());
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) {
      unbinder.unbind();
    }
    eventBus.unregister(this);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onClickPostEvent(PostViewHolder.OnClickPostEvent event) {
    Intent intent = externalRedditIntentBuilder.buildIntent(event.post().url);
    if (intent != null) {
      startActivity(intent);
    }
  }

  private final ItemTouchHelper itemTouchHelper =
      new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, 0) {

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder) {
          if (viewHolder instanceof PostViewHolder) {
            return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
          }

          return 0;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
          return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
          int adapterPosition = viewHolder.getAdapterPosition();
          RedditListItem redditListItem = redditListAdapter.getItem(adapterPosition);
          mainViewModel.markPostAsRead(redditListItem.getId());
        }
      });
}
