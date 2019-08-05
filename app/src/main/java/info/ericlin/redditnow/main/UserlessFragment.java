package info.ericlin.redditnow.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.firebase.analytics.FirebaseAnalytics;
import dagger.android.support.DaggerFragment;
import info.ericlin.redditnow.R;
import javax.inject.Inject;
import net.dean.jraw.android.SharedPreferencesTokenStore;

/**
 * A fragment that shows a text explanation and a button to prompt users to log in.
 *
 * It is shown when there is no log in information stored in {@link SharedPreferencesTokenStore} as
 * users just installed the app or log out from existing account.
 */
public class UserlessFragment extends DaggerFragment {

  private static final int REQUEST_CODE = 38121;

  @Inject
  FirebaseAnalytics firebaseAnalytics;

  @Nullable
  private Unbinder unbinder;

  @Nullable
  private OnRedditLoginSuccessListener onRedditLoginSuccessListener;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnRedditLoginSuccessListener) {
      onRedditLoginSuccessListener = (OnRedditLoginSuccessListener) context;
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_userless, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) {
      unbinder.unbind();
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    onRedditLoginSuccessListener = null;
  }

  @OnClick(R.id.userless_authenticate_button)
  void onButtonClicked() {
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null);
    Intent intent = new Intent(requireContext(), RedditOAuthActivity.class);
    startActivityForResult(intent, REQUEST_CODE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE) {
      if (onRedditLoginSuccessListener != null && resultCode == Activity.RESULT_OK) {
        onRedditLoginSuccessListener.onLoginSuccess();
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  /**
   * Callback for Reddit login success.
   */
  public interface OnRedditLoginSuccessListener {
    void onLoginSuccess();
  }
}
