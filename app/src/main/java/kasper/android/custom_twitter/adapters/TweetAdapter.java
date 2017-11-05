package kasper.android.custom_twitter.adapters;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.activities.ProfileActivity;
import kasper.android.custom_twitter.activities.TweetsActivity;
import kasper.android.custom_twitter.activities.YesNoActivity;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.models.memory.Tweet;
import kasper.android.custom_twitter.models.packets.RequestDeleteTweet;
import kasper.android.custom_twitter.models.packets.RequestLikeTweet;
import kasper.android.custom_twitter.models.packets.RequestUnlikeTweet;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;
import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetHolder> {

    private AppCompatActivity activity;
    private Fragment fragment;
    private long myId;
    private long pageId;
    private ArrayList<Tweet> tweets;

    private int deleteReservedPosition;

    public TweetAdapter(AppCompatActivity activity, Fragment fragment, long myId, long pageId
            , ArrayList<Tweet> tweets) {
        this.activity = activity;
        this.fragment = fragment;
        this.myId = myId;
        this.pageId = pageId;
        this.tweets = tweets;
        this.notifyDataSetChanged();
    }

    @Override
    public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TweetHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_tweet, parent, false));
    }

    @Override
    public void onBindViewHolder(final TweetHolder holder, int position) {
        final Tweet tweet = this.tweets.get(position);
        holder.authorTV.setText(tweet.getAuthor().getUserTitle());
        if (myId == tweet.getAuthor().getHumanId() || myId == pageId) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    deleteReservedPosition = holder.getAdapterPosition();

                    if (fragment == null) {

                        activity.startActivityForResult(new Intent(activity, YesNoActivity.class)
                                .putExtra("dialog-title", "حذف توییت").putExtra("dialog-content",
                                        "آیا می خواهید این توییت را حذف کنید ؟"), 123);
                    }
                    else {

                        fragment.startActivityForResult(new Intent(activity, YesNoActivity.class)
                                .putExtra("dialog-title", "حذف توییت").putExtra("dialog-content",
                                        "آیا می خواهید این توییت را حذف کنید ؟"), 123);
                    }
                }
            });
        }
        else {
            holder.deleteBtn.setVisibility(View.GONE);
        }
        holder.contentTV.setText(tweet.getContent());
        holder.authorTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext()
                        , ProfileActivity.class).putExtra("human-id", tweet.getAuthor().getHumanId()));
            }
        });
        if (tweet.isLikedByMe()) {
            holder.likeBtn.setImageResource(R.drawable.liked);
        }
        else {
            holder.likeBtn.setImageResource(R.drawable.like);
        }
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BaseRequest request;

                if (!tweet.isLikedByMe()) {

                    request = new RequestLikeTweet();
                    ((RequestLikeTweet) request).pageId = tweet.getPageId();
                    ((RequestLikeTweet) request).tweetNodeId = tweet.getNodeId();
                }
                else {

                    request = new RequestUnlikeTweet();
                    ((RequestUnlikeTweet) request).pageId = tweet.getPageId();
                    ((RequestUnlikeTweet) request).tweetNodeId = tweet.getNodeId();
                }

                final int itemPosition = holder.getAdapterPosition();

                MyApp.getInstance().getNetworkHelper().pushTCP(request, new OnRequestAnsweredListener() {
                    @Override
                    public void onRequestAnswered(BaseAnswer rawAnswer) {

                        if (tweet.isLikedByMe()) {

                            tweets.get(itemPosition).setLikedByMe(false);
                            long likesCount = tweets.get(itemPosition).getLikesCount();
                            tweets.get(itemPosition).setLikesCount(likesCount - 1);
                        }
                        else {

                            tweets.get(itemPosition).setLikedByMe(true);
                            long likesCount = tweets.get(itemPosition).getLikesCount();
                            tweets.get(itemPosition).setLikesCount(likesCount + 1);
                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                notifyItemChanged(holder.getAdapterPosition());
                            }
                        });
                    }
                });
            }
        });
        holder.likesCountTV.setText(tweet.getLikesCount() + "");
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.startActivityForResult(new Intent(activity, TweetsActivity.class).
                        putExtra("parent-id", tweet.getTweetId()).putExtra("page-id"
                        , tweet.getPageId()), 5);
            }
        });

        ArrayList<Tweet> comments = tweet.getTopComments();

        configCommentsVisibility(holder, comments.size());

        if (comments.size() >= 1) {
            Tweet comment = comments.get(0);
            holder.commentAuthorTV1.setText(comment.getAuthor().getUserTitle());
            holder.commentContentTV1.setText(comment.getContent());
        }

        if (comments.size() >= 2) {
            Tweet comment = comments.get(1);
            holder.commentAuthorTV2.setText(comment.getAuthor().getUserTitle());
            holder.commentContentTV2.setText(comment.getContent());
        }

        if (comments.size() >= 3) {
            Tweet comment = comments.get(2);
            holder.commentAuthorTV3.setText(comment.getAuthor().getUserTitle());
            holder.commentContentTV3.setText(comment.getContent());
        }
    }

    private void configCommentsVisibility(TweetHolder holder, int commentsCount) {

        holder.commentContainer1.setVisibility(View.GONE);
        holder.commentAuthorTV1.setVisibility(View.GONE);
        holder.commentContentTV1.setVisibility(View.GONE);

        holder.commentContainer2.setVisibility(View.GONE);
        holder.commentAuthorTV2.setVisibility(View.GONE);
        holder.commentContentTV2.setVisibility(View.GONE);

        holder.commentContainer3.setVisibility(View.GONE);
        holder.commentAuthorTV3.setVisibility(View.GONE);
        holder.commentContentTV3.setVisibility(View.GONE);

        if (commentsCount >= 1) {

            holder.commentContainer1.setVisibility(View.VISIBLE);
            holder.commentAuthorTV1.setVisibility(View.VISIBLE);
            holder.commentContentTV1.setVisibility(View.VISIBLE);
        }

        if (commentsCount >= 2) {

            holder.commentContainer2.setVisibility(View.VISIBLE);
            holder.commentAuthorTV2.setVisibility(View.VISIBLE);
            holder.commentContentTV2.setVisibility(View.VISIBLE);
        }

        if (commentsCount >= 3) {

            holder.commentContainer3.setVisibility(View.VISIBLE);
            holder.commentAuthorTV3.setVisibility(View.VISIBLE);
            holder.commentContentTV3.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void notifyOnYesNoDialogOkResult() {

        Tweet tweet = tweets.get(deleteReservedPosition);

        RequestDeleteTweet requestDeleteTweet = new RequestDeleteTweet();
        requestDeleteTweet.tweetNodeId = tweet.getNodeId();

        MyApp.getInstance().getNetworkHelper().pushTCP(requestDeleteTweet, new OnRequestAnsweredListener() {
            @Override
            public void onRequestAnswered(BaseAnswer rawAnswer) {

                if (rawAnswer.answerStatus == AnswerStatus.OK) {

                    tweets.remove(deleteReservedPosition);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            notifyItemRemoved(deleteReservedPosition);
                        }
                    });
                }
            }
        });
    }

    class TweetHolder extends RecyclerView.ViewHolder {

        TextView authorTV;
        ImageButton deleteBtn;
        TextView contentTV;
        ImageButton likeBtn;
        TextView likesCountTV;
        ImageButton commentBtn;

        CardView commentContainer1;
        TextView commentAuthorTV1;
        TextView commentContentTV1;

        CardView commentContainer2;
        TextView commentAuthorTV2;
        TextView commentContentTV2;

        CardView commentContainer3;
        TextView commentAuthorTV3;
        TextView commentContentTV3;

        TweetHolder(View itemView) {
            super(itemView);
            this.authorTV = itemView.findViewById(R.id.adapter_profile_tweet_author_text_view);
            this.deleteBtn = (ImageButton) itemView.findViewById(R.id.adapter_profile_tweet_delete_image_button);
            this.contentTV = itemView.findViewById(R.id.adapter_profile_tweet_content_text_view);
            this.likeBtn = itemView.findViewById(R.id.adapter_tweet_like_button);
            this.likesCountTV = itemView.findViewById(R.id.adapter_tweet_likes_count_text_view);
            this.commentBtn = itemView.findViewById(R.id.adapter_tweet_comment_button);

            this.commentContainer1 = itemView.findViewById(R.id.adapter_profile_tweet_comment_container1);
            this.commentAuthorTV1 = itemView.findViewById(R.id.adapter_profile_tweet_comment_author_text_view1);
            this.commentContentTV1 = itemView.findViewById(R.id.adapter_profile_tweet_comment_content_text_view1);

            this.commentContainer2 = itemView.findViewById(R.id.adapter_profile_tweet_comment_container2);
            this.commentAuthorTV2 = itemView.findViewById(R.id.adapter_profile_tweet_comment_author_text_view2);
            this.commentContentTV2 = itemView.findViewById(R.id.adapter_profile_tweet_comment_content_text_view2);

            this.commentContainer3 = itemView.findViewById(R.id.adapter_profile_tweet_comment_container3);
            this.commentAuthorTV3 = itemView.findViewById(R.id.adapter_profile_tweet_comment_author_text_view3);
            this.commentContentTV3 = itemView.findViewById(R.id.adapter_profile_tweet_comment_content_text_view3);
        }
    }
}