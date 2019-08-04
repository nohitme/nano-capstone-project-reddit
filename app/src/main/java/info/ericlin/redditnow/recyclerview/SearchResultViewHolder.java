package info.ericlin.redditnow.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.ericlin.redditnow.R;
import info.ericlin.redditnow.room.SubredditEntity;
import info.ericlin.redditnow.search.SearchResultItem;
import timber.log.Timber;

public class SearchResultViewHolder extends RedditViewHolder<SearchResultItem> {

    @BindView(R.id.subreddit_name)
    TextView subredditName;

    @BindView(R.id.subreddit_num_subs)
    TextView subredditNumSubs;

    @BindView(R.id.subreddit_subscribed)
    ImageView subredditSubscribed;

    SearchResultViewHolder(@NonNull View itemView, @NonNull EventBus eventBus) {
        super(itemView, eventBus);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void bind(@NonNull SearchResultItem item) {
        itemView.setOnClickListener(v -> getEventBus().post(OnClickSubredditSearchEvent.create(item.name())));

        Context context = itemView.getContext();
        subredditName.setText(context.getString(R.string.vh_subreddit_name, item.name()));
        subredditNumSubs.setText(context.getString(R.string.vh_subreddit_members, item.numberOfSubscribers()));

        updateSubscriptionStatus(item.name(), item.isSubscribed());
    }

    private void updateSubscriptionStatus(String subredditName, boolean isCurrentlySubscribed) {
        Context context = itemView.getContext();
        if (isCurrentlySubscribed) {
            subredditSubscribed.setImageResource(R.drawable.ic_remove_circle_black_24dp);
            subredditSubscribed.setContentDescription(context.getString(R.string.subreddit_unfollow));
        } else {
            subredditSubscribed.setImageResource(R.drawable.ic_add_circle_black_24dp);
            subredditSubscribed.setContentDescription(context.getString(R.string.subreddit_follow));
        }

        subredditSubscribed.setOnClickListener(view -> getEventBus().post(UpdateSubredditSubscriptionEvent.create(subredditName, isCurrentlySubscribed)));
    }

    @Override
    protected void unbind() {
        // do nothing
    }

    @AutoValue
    public abstract static class OnClickSubredditSearchEvent {

        public abstract String subredditName();

        public static OnClickSubredditSearchEvent create(String subredditName) {
            return new AutoValue_SearchResultViewHolder_OnClickSubredditSearchEvent(subredditName);
        }
    }

    @AutoValue
    public abstract static class UpdateSubredditSubscriptionEvent {

        public abstract String subredditName();

        public abstract boolean isCurrentlySubscribed();

        public static UpdateSubredditSubscriptionEvent create(String subredditName, boolean isCurrentlySubscribed) {
            return new AutoValue_SearchResultViewHolder_UpdateSubredditSubscriptionEvent(subredditName, isCurrentlySubscribed);
        }
    }
}
