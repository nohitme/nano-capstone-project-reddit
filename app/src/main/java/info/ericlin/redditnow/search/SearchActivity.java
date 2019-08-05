package info.ericlin.redditnow.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.analytics.FirebaseAnalytics;
import dagger.android.support.DaggerAppCompatActivity;
import info.ericlin.redditnow.EventBusUtils;
import info.ericlin.redditnow.R;
import info.ericlin.redditnow.recyclerview.LoadingDummyViewHolder;
import info.ericlin.redditnow.recyclerview.RedditListAdapter;
import info.ericlin.redditnow.recyclerview.SearchResultViewHolder;
import info.ericlin.redditnow.subreddit.SubredditActivity;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SearchActivity extends DaggerAppCompatActivity {

  @BindView(R.id.search_toolbar)
  Toolbar toolbar;

  @BindView(R.id.search_recycler_view)
  RecyclerView recyclerView;

  @BindView(R.id.search_progress_bar)
  View progressView;

  @Inject
  ViewModelProvider.Factory viewModelFactory;

  @Inject
  EventBus eventBus;

  @Inject
  FirebaseAnalytics firebaseAnalytics;

  private SearchViewModel searchViewModel;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);

    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolbar.setNavigationOnClickListener(t -> finish());
    toolbar.setContentInsetStartWithNavigation(0);

    toolbar.inflateMenu(R.menu.menu_search_view);
    MenuItem menuItem = toolbar.getMenu().findItem(R.id.menu_item_search);
    SearchView searchView = (SearchView) menuItem.getActionView();
    setUpSearchView(searchView);

    RedditListAdapter redditListAdapter = new RedditListAdapter(eventBus);
    recyclerView.setAdapter(redditListAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
        DividerItemDecoration.VERTICAL);
    recyclerView.addItemDecoration(dividerItemDecoration);

    searchViewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);
    searchViewModel.getListLiveData().observe(this, redditListAdapter::submitList);
    searchViewModel.getInitialLoadingLiveData().observe(this, this::setLoadingState);

    EventBusUtils.registerEventBusWithLifecycle(eventBus, this, this);
  }

  private void setLoadingState(boolean isLoading) {
    if (isLoading) {
      recyclerView.setVisibility(View.GONE);
      progressView.setVisibility(View.VISIBLE);
    } else {
      recyclerView.setVisibility(View.VISIBLE);
      progressView.setVisibility(View.GONE);
    }
  }

  private void setUpSearchView(@NonNull SearchView searchView) {
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        if (query.trim().isEmpty()) {
          Toast.makeText(SearchActivity.this, R.string.search_empty_query_warning,
              Toast.LENGTH_SHORT)
              .show();
          return false;
        }

        performQuery(query);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });

    searchView.setIconifiedByDefault(false);
    searchView.setFocusable(true);
    searchView.setIconified(false);
    searchView.setQueryHint(getString(R.string.search_query_hint));
    searchView.requestFocus();
  }

  private void performQuery(String query) {
    Bundle bundle = new Bundle();
    bundle.putString("search_term", query);
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
    searchViewModel.searchSubreddits(query);
  }

  @Subscribe
  public void onClickSubredditEvent(SearchResultViewHolder.OnClickSubredditSearchEvent event) {
    Intent intent = SubredditActivity.newIntent(this, event.subredditName());
    startActivity(intent);
  }

  @Subscribe
  public void onUpdateSubscriptionEvent(
      SearchResultViewHolder.UpdateSubredditSubscriptionEvent event) {
    searchViewModel.updateSubredditSubscription(event.subredditName(), event.isCurrentlySubscribed());
  }

  @Subscribe
  public void onLoadingViewBoundEvent(LoadingDummyViewHolder.OnLoadingViewBoundEvent event) {
    searchViewModel.loadNextPage();
  }
}
