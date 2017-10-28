package kasper.android.custom_twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.activities.HumansActivity;
import kasper.android.custom_twitter.activities.TweetsActivity;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.models.database.Human;
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.memory.Tweet;
import kasper.android.custom_twitter.models.packets.AnswerFollow;
import kasper.android.custom_twitter.models.packets.RequestFollow;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppCompatActivity activity;
    private boolean isMe;
    private boolean isFollowed;
    private long humanId;
    private String userTitle;
    private int postsCount;
    private int followersCount;
    private int followingsCount;
    private ArrayList<Tweet> tweets;

    public ProfileAdapter(AppCompatActivity activity, boolean isMe, boolean isFollowed, long humanId, String userTitle, int postsCount, int followersCount, int followingsCount, ArrayList<Tweet> tweets) {
        this.activity = activity;
        this.isMe = isMe;
        this.isFollowed = isFollowed;
        this.humanId = humanId;
        this.userTitle = userTitle;
        this.postsCount = postsCount;
        this.followersCount = followersCount;
        this.followingsCount = followingsCount;
        this.tweets = tweets;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new ProfileHead(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_head, parent, false));
        } else {
            return new ProfileTweet(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_tweet, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (position == 0) {
            ((ProfileHead) holder).userTitleTV.setText(userTitle);
            ((ProfileHead) holder).postsCountTV.setText(postsCount + "");
            ((ProfileHead) holder).followersCountTV.setText(followersCount + "");
            ((ProfileHead) holder).followingsCountTV.setText(followingsCount + "");

            ((ViewGroup)((ProfileHead) holder).followersCountTV.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), HumansActivity.class)
                            .putExtra("is-followers-page", true).putExtra("human-id", humanId));
                }
            });

            ((ViewGroup)((ProfileHead) holder).followingsCountTV.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), HumansActivity.class)
                            .putExtra("is-followers-page", false).putExtra("human-id", humanId));
                }
            });

            if (isMe || isFollowed) {
                ((ProfileHead) holder).followBtn.hide();
            }
            else {
                ((ProfileHead) holder).followBtn.show();
                ((ProfileHead) holder).followBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        RequestFollow requestFollow = new RequestFollow();
                        requestFollow.targetUserId = humanId;

                        MyApp.getInstance().getNetworkHelper().pushTCP(requestFollow, new OnRequestAnsweredListener() {
                            @Override
                            public void onRequestAnswered(BaseAnswer rawAnswer) {

                                AnswerFollow answerFollow = (AnswerFollow) rawAnswer;

                                if (answerFollow.answerStatus == AnswerStatus.OK) {

                                    Realm realm = Realm.getDefaultInstance();

                                    MyData myData = realm.where(MyData.class).findFirst();

                                    realm.beginTransaction();

                                    Human human = new Human();
                                    human.setHumanId(humanId);
                                    myData.getHuman().getFollowing().add(human);

                                    realm.commitTransaction();

                                    realm.close();

                                    followersCount++;
                                    isFollowed = true;

                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            ((ProfileHead) holder).followBtn.hide();
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        }
        else {
            final Tweet tweet = this.tweets.get(position - 1);
            ((ProfileTweet)holder).authorTV.setText(tweet.getAuthor().getUserTitle());
            ((ProfileTweet)holder).contentTV.setText(tweet.getContent());
            ((ProfileTweet)holder).likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            ((ProfileTweet)holder).commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    activity.startActivityForResult(new Intent(activity, TweetsActivity.class).
                            putExtra("parent-id", tweet.getTweetId()).putExtra("page-id"
                            , tweet.getPageId()), 5);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tweets.size() + 1;
    }

    private class ProfileHead extends RecyclerView.ViewHolder {

        TextView userTitleTV;
        TextView postsCountTV;
        TextView followersCountTV;
        TextView followingsCountTV;
        FloatingActionButton followBtn;

        ProfileHead(View itemView) {
            super(itemView);
            this.userTitleTV = (TextView) itemView.findViewById(R.id.adapter_profile_head_user_title_text_view);
            this.postsCountTV = (TextView) itemView.findViewById(R.id.adapter_profile_head_posts_count_text_view);
            this.followersCountTV = (TextView) itemView.findViewById(R.id.adapter_profile_head_followers_count_text_view);
            this.followingsCountTV = (TextView) itemView.findViewById(R.id.adapter_profile_head_followings_count_text_view);
            this.followBtn = (FloatingActionButton) itemView.findViewById(R.id.adapter_profile_head_follow_button);
        }
    }

    private class ProfileTweet extends RecyclerView.ViewHolder {

        TextView authorTV;
        TextView contentTV;
        ImageButton likeBtn;
        ImageButton commentBtn;

        ProfileTweet(View itemView) {
            super(itemView);
            this.authorTV = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_author_text_view);
            this.contentTV = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_content_text_view);
            this.likeBtn = (ImageButton) itemView.findViewById(R.id.adapter_tweet_like_button);
            this.commentBtn = (ImageButton) itemView.findViewById(R.id.adapter_tweet_comment_button);
        }
    }
}