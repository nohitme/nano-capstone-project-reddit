package info.ericlin.redditnow.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import info.ericlin.redditnow.R;
import javax.inject.Inject;

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

  @Inject
  MainViewModelFactory mainViewModelFactory;

  private MainViewModel mainViewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mainViewModel = createMainViewModel();
  }

  @NonNull
  private MainViewModel createMainViewModel() {
    return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
      @NonNull
      @Override
      public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == MainViewModel.class) {
          return (T) mainViewModelFactory.create();
        }

        throw new IllegalArgumentException("unknown model class: " + modelClass);
      }
    }).get(MainViewModel.class);
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

    toolbar.setTitle(R.string.app_name);
    toolbar.setNavigationOnClickListener(v -> requireActivity().finish());
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) {
      unbinder.unbind();
    }
  }
}
