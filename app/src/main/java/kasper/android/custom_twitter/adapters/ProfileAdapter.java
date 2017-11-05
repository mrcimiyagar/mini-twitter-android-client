package kasper.android.custom_twitter.adapters;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.activities.EditProfileActivity;
import kasper.android.custom_twitter.activities.HumansActivity;
import kasper.android.custom_twitter.activities.RequestsActivity;
import kasper.android.custom_twitter.activities.SettingsActivity;
import kasper.android.custom_twitter.activities.TweetsActivity;
import kasper.android.custom_twitter.activities.YesNoActivity;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.models.database.Human;
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.memory.Tweet;
import kasper.android.custom_twitter.models.packets.AnswerFollow;
import kasper.android.custom_twitter.models.packets.RequestDeleteTweet;
import kasper.android.custom_twitter.models.packets.RequestFollow;
import kasper.android.custom_twitter.models.packets.RequestLikeTweet;
import kasper.android.custom_twitter.models.packets.RequestUnlikeTweet;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;
import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppCompatActivity activity;
    private Fragment fragment;
    private long myId;
    private boolean isMe;
    private boolean isFollowed;
    private boolean isRequested;
    private boolean isPrivate;
    private int requestsCount;
    private long humanId;
    private String userTitle;
    private String userBio;
    private int postsCount;
    private int followersCount;
    private int followingsCount;
    private ArrayList<Tweet> tweets;

    private int deleteReservedPosition;

    public ProfileAdapter(AppCompatActivity activity, Fragment fragment, long myId, boolean isMe
            , boolean isFollowed, boolean isRequested, boolean isPrivate, int requestsCount, long humanId
            , String userTitle, String userBio, int postsCount, int followersCount
            , int followingsCount, ArrayList<Tweet> tweets) {
        this.activity = activity;
        this.fragment = fragment;
        this.myId = myId;
        this.isMe = isMe;
        this.isFollowed = isFollowed;
        this.isRequested = isRequested;
        this.isPrivate = isPrivate;
        this.requestsCount = requestsCount;
        this.humanId = humanId;
        this.userTitle = userTitle;
        this.userBio = userBio;
        this.postsCount = postsCount;
        this.followersCount = followersCount;
        this.followingsCount = followingsCount;
        this.tweets = tweets;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new ProfilePanel(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_panel, parent, false));
        } else if (viewType == 1) {
            return new ProfileHead(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_head, parent, false));
        } else if (viewType == 2){
            return new ProfilePrivate(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_private, parent, false));
        } else {
            return new ProfileTweet(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_tweet, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (isMe) {
            if (position == 0) {
                return 0;
            }
            else if (position == 1) {
                return 1;
            }
            else {
                return 3;
            }
        }
        else {
            if (position == 0) {
                return 1;
            }
            else {
                if (!isFollowed && isPrivate) {
                    return 2;
                }
                else {
                    return 3;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        if (viewType == 0) {

            ((ProfilePanel)holder).editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    activity.startActivity(new Intent(activity, EditProfileActivity.class));
                }
            });

            ((ProfilePanel)holder).requestsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    activity.startActivity(new Intent(activity, RequestsActivity.class));
                }
            });

            if (requestsCount > 0) {
                DrawableCompat.setTint(((ProfilePanel) holder).requestsBtn.getDrawable(), activity.getResources().getColor(R.color.colorAccent));
            }
            else {
                DrawableCompat.setTint(((ProfilePanel) holder).requestsBtn.getDrawable(), Color.BLACK);
            }

            ((ProfilePanel)holder).settingsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    activity.startActivity(new Intent(activity, SettingsActivity.class));
                }
            });
        }
        else if (viewType == 1) {

            ((ProfileHead) holder).userTitleTV.setText(userTitle);
            ((ProfileHead) holder).postsCountTV.setText(postsCount + "");
            ((ProfileHead) holder).followersCountTV.setText(followersCount + "");
            ((ProfileHead) holder).followingsCountTV.setText(followingsCount + "");
            ((ProfileHead) holder).userBioTV.setText(userBio.length() == 0 ? "سلام . من یک کاربر جدید هستم !" : userBio);

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
            else if (isRequested) {
                ((ProfileHead) holder).followBtn.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
            }
            else {
                ((ProfileHead) holder).followBtn.setBackgroundTintList(ColorStateList.valueOf(activity
                        .getResources().getColor(R.color.colorAccent)));
                ((ProfileHead) holder).followBtn.show();
                ((ProfileHead) holder).followBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!isMe && !isFollowed && !isRequested) {

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
                                        myData.getHuman().getRequested().add(human);

                                        realm.commitTransaction();

                                        realm.close();

                                        isRequested = true;

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                notifyItemChanged(0);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
        else if (viewType == 2) {

        }
        else if (viewType == 3) {

            final Tweet tweet = this.tweets.get(position - (isMe ? 2 : 1));
            ((ProfileTweet)holder).authorTV.setText(tweet.getAuthor().getUserTitle());
            if (myId == tweet.getAuthor().getHumanId()) {
                ((ProfileTweet) holder).deleteBtn.setVisibility(View.VISIBLE);
                ((ProfileTweet) holder).deleteBtn.setOnClickListener(new View.OnClickListener() {
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
                ((ProfileTweet) holder).deleteBtn.setVisibility(View.GONE);
            }
            ((ProfileTweet)holder).contentTV.setText(tweet.getContent());
            if (tweet.isLikedByMe()) {
                ((ProfileTweet) holder).likeBtn.setImageResource(R.drawable.liked);
            }
            else {
                ((ProfileTweet) holder).likeBtn.setImageResource(R.drawable.like);
            }
            ((ProfileTweet)holder).likeBtn.setOnClickListener(new View.OnClickListener() {
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

                    final int itemPosition = holder.getAdapterPosition() - (isMe ? 2 : 1);

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
            ((ProfileTweet) holder).likesCountTV.setText(tweet.getLikesCount() + "");
            ((ProfileTweet)holder).commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    activity.startActivityForResult(new Intent(activity, TweetsActivity.class).
                            putExtra("parent-id", tweet.getTweetId()).putExtra("page-id"
                            , tweet.getPageId()), 5);
                }
            });

            ArrayList<Tweet> comments = tweet.getTopComments();

            configCommentsVisibility((ProfileTweet) holder, comments.size());

            if (comments.size() >= 1) {
                Tweet comment = comments.get(0);
                ((ProfileTweet) holder).commentAuthorTV1.setText(comment.getAuthor().getUserTitle());
                ((ProfileTweet) holder).commentContentTV1.setText(comment.getContent());
            }

            if (comments.size() >= 2) {
                Tweet comment = comments.get(1);
                ((ProfileTweet) holder).commentAuthorTV2.setText(comment.getAuthor().getUserTitle());
                ((ProfileTweet) holder).commentContentTV2.setText(comment.getContent());
            }

            if (comments.size() >= 3) {
                Tweet comment = comments.get(2);
                ((ProfileTweet) holder).commentAuthorTV3.setText(comment.getAuthor().getUserTitle());
                ((ProfileTweet) holder).commentContentTV3.setText(comment.getContent());
            }
        }
    }

    private void configCommentsVisibility(ProfileTweet holder, int commentsCount) {

        ((ProfileTweet) holder).commentContainer1.setVisibility(View.GONE);
        ((ProfileTweet) holder).commentAuthorTV1.setVisibility(View.GONE);
        ((ProfileTweet) holder).commentContentTV1.setVisibility(View.GONE);

        ((ProfileTweet) holder).commentContainer2.setVisibility(View.GONE);
        ((ProfileTweet) holder).commentAuthorTV2.setVisibility(View.GONE);
        ((ProfileTweet) holder).commentContentTV2.setVisibility(View.GONE);

        ((ProfileTweet) holder).commentContainer3.setVisibility(View.GONE);
        ((ProfileTweet) holder).commentAuthorTV3.setVisibility(View.GONE);
        ((ProfileTweet) holder).commentContentTV3.setVisibility(View.GONE);

        if (commentsCount >= 1) {

            ((ProfileTweet) holder).commentContainer1.setVisibility(View.VISIBLE);
            ((ProfileTweet) holder).commentAuthorTV1.setVisibility(View.VISIBLE);
            ((ProfileTweet) holder).commentContentTV1.setVisibility(View.VISIBLE);
        }

        if (commentsCount >= 2) {

            ((ProfileTweet) holder).commentContainer2.setVisibility(View.VISIBLE);
            ((ProfileTweet) holder).commentAuthorTV2.setVisibility(View.VISIBLE);
            ((ProfileTweet) holder).commentContentTV2.setVisibility(View.VISIBLE);
        }

        if (commentsCount >= 3) {

            ((ProfileTweet) holder).commentContainer3.setVisibility(View.VISIBLE);
            ((ProfileTweet) holder).commentAuthorTV3.setVisibility(View.VISIBLE);
            ((ProfileTweet) holder).commentContentTV3.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (isMe) {
            return tweets.size() + 2;
        }
        else {
            if (isPrivate && !isFollowed) {
                return 2;
            }
            else {
                return tweets.size() + 1;
            }
        }
    }

    public void notifyOnYesNoDialogOkResult() {

        Tweet tweet = tweets.get(deleteReservedPosition - (isMe ? 2 : 1));

        RequestDeleteTweet requestDeleteTweet = new RequestDeleteTweet();
        requestDeleteTweet.tweetNodeId = tweet.getNodeId();

        MyApp.getInstance().getNetworkHelper().pushTCP(requestDeleteTweet, new OnRequestAnsweredListener() {
            @Override
            public void onRequestAnswered(BaseAnswer rawAnswer) {

                tweets.remove(deleteReservedPosition - 1);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        notifyItemRemoved(deleteReservedPosition);
                    }
                });
            }
        });
    }

    private class ProfilePanel extends RecyclerView.ViewHolder {

        private ImageButton editBtn;
        private ImageButton requestsBtn;
        private ImageButton settingsBtn;

        ProfilePanel(View itemView) {
            super(itemView);
            this.editBtn = itemView.findViewById(R.id.adapter_profile_panel_edit_image_button);
            this.requestsBtn = itemView.findViewById(R.id.adapter_profile_panel_requests_image_button);
            this.settingsBtn = itemView.findViewById(R.id.adapter_profile_panel_settings_image_button);
        }
    }

    private class ProfileHead extends RecyclerView.ViewHolder {

        TextView userTitleTV;
        TextView postsCountTV;
        TextView followersCountTV;
        TextView followingsCountTV;
        TextView userBioTV;
        FloatingActionButton followBtn;

        ProfileHead(View itemView) {
            super(itemView);
            this.userTitleTV = (TextView) itemView.findViewById(R.id.adapter_profile_head_user_title_text_view);
            this.postsCountTV = (TextView) itemView.findViewById(R.id.adapter_profile_head_posts_count_text_view);
            this.followersCountTV = (TextView) itemView.findViewById(R.id.adapter_profile_head_followers_count_text_view);
            this.followingsCountTV = (TextView) itemView.findViewById(R.id.adapter_profile_head_followings_count_text_view);
            this.userBioTV = (TextView) itemView.findViewById(R.id.adapter_profile_head_user_bio_text_view);
            this.followBtn = (FloatingActionButton) itemView.findViewById(R.id.adapter_profile_head_follow_button);
        }
    }

    private class ProfileTweet extends RecyclerView.ViewHolder {

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

        ProfileTweet(View itemView) {

            super(itemView);

            this.authorTV = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_author_text_view);
            this.deleteBtn = (ImageButton) itemView.findViewById(R.id.adapter_profile_tweet_delete_image_button);
            this.contentTV = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_content_text_view);
            this.likeBtn = (ImageButton) itemView.findViewById(R.id.adapter_tweet_like_button);
            this.likesCountTV = (TextView) itemView.findViewById(R.id.adapter_tweet_likes_count_text_view);
            this.commentBtn = (ImageButton) itemView.findViewById(R.id.adapter_tweet_comment_button);

            this.commentContainer1 = (CardView) itemView.findViewById(R.id.adapter_profile_tweet_comment_container1);
            this.commentAuthorTV1 = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_comment_author_text_view1);
            this.commentContentTV1 = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_comment_content_text_view1);

            this.commentContainer2 = (CardView) itemView.findViewById(R.id.adapter_profile_tweet_comment_container2);
            this.commentAuthorTV2 = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_comment_author_text_view2);
            this.commentContentTV2 = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_comment_content_text_view2);

            this.commentContainer3 = (CardView) itemView.findViewById(R.id.adapter_profile_tweet_comment_container3);
            this.commentAuthorTV3 = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_comment_author_text_view3);
            this.commentContentTV3 = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_comment_content_text_view3);
        }
    }

    private class ProfilePrivate extends RecyclerView.ViewHolder {

        ProfilePrivate(View itemView) {
            super(itemView);
        }
    }
}