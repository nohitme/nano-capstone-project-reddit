package info.ericlin.redditnow.dagger;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Provides;
import info.ericlin.redditnow.main.MainViewModel;
import info.ericlin.redditnow.main.MainViewModelFactory;
import javax.inject.Provider;
import javax.inject.Singleton;

@Module
abstract class ViewModelModule {

  @Provides
  @Singleton
  static ViewModelProvider.Factory factory(
      Provider<MainViewModelFactory> mainViewModelFactoryProvider) {

    return new ViewModelProvider.Factory() {
      @NonNull
      @Override
      public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == MainViewModel.class) {
          return (T) mainViewModelFactoryProvider.get().create();
        }

        throw new IllegalArgumentException("unknown model class: " + modelClass);
      }
    };
  }
}
