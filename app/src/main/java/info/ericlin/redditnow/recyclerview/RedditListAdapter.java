package info.ericlin.redditnow.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import com.google.common.base.Objects;
import info.ericlin.redditnow.R;
import info.ericlin.redditnow.room.PostEntity;
import info.ericlin.redditnow.room.SubredditEntity;
import org.greenrobot.eventbus.EventBus;

public class RedditListAdapter
    extends ListAdapter<RedditListItem, RedditViewHolder<RedditListItem>> {

  private static final int VIEW_TYPE_SUBREDDIT = R.layout.item_subreddit;
  private static final int VIEW_TYPE_POST = R.layout.item_post;
  private static final int VIEW_TYPE_EMPTY_ROW = R.layout.item_empty_row;
  //private static final int VIEW_TYPE_SUB_DETAILS = 3;

  private final EventBus eventBus;

  public RedditListAdapter(EventBus eventBus) {
    super(ITEM_CALLBACK);
    this.eventBus = eventBus;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  @Override
  public RedditViewHolder<RedditListItem> onCreateViewHolder(@NonNull ViewGroup parent,
      int viewType) {

    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View itemView = inflater.inflate(viewType, parent, false);

    switch (viewType) {
      case VIEW_TYPE_SUBREDDIT:
        SubredditViewHolder subredditViewHolder = new SubredditViewHolder(itemView, eventBus);
        return (RedditViewHolder) subredditViewHolder;
      case VIEW_TYPE_POST:
        PostViewHolder postViewHolder = new PostViewHolder(itemView, eventBus);
        return (RedditViewHolder) postViewHolder;
      case VIEW_TYPE_EMPTY_ROW:
        EmptyDummyViewHolder emptyDummyViewHolder = new EmptyDummyViewHolder(itemView, eventBus);
        return (RedditViewHolder) emptyDummyViewHolder;
      default:
        throw new IllegalArgumentException("unknown view type " + viewType);
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RedditViewHolder<RedditListItem> holder, int position) {
    RedditListItem item = getItem(position);
    holder.bind(item);
  }

  @Override
  public int getItemViewType(int position) {
    RedditListItem item = getItem(position);
    if (item instanceof SubredditEntity) {
      return VIEW_TYPE_SUBREDDIT;
    } else if (item instanceof PostEntity) {
      return VIEW_TYPE_POST;
    } else if (item instanceof EmptyDummyItem) {
      return VIEW_TYPE_EMPTY_ROW;
    }

    throw new IllegalArgumentException("unknown view type for position " + position);
  }

  @Override
  public RedditListItem getItem(int position) {
    return super.getItem(position);
  }

  private static final DiffUtil.ItemCallback<RedditListItem> ITEM_CALLBACK =
      new DiffUtil.ItemCallback<RedditListItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull RedditListItem oldItem,
            @NonNull RedditListItem newItem) {
          return Objects.equal(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull RedditListItem oldItem,
            @NonNull RedditListItem newItem) {
          return Objects.equal(oldItem, newItem);
        }
      };
}
