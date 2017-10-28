package kasper.android.custom_twitter.adapters;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.activities.TweetsActivity;
import kasper.android.custom_twitter.models.memory.Tweet;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetHolder> {

    private AppCompatActivity activity;
    private ArrayList<Tweet> tweets;

    public TweetAdapter(AppCompatActivity activity, ArrayList<Tweet> tweets) {
        this.activity = activity;
        this.tweets = tweets;
        this.notifyDataSetChanged();
    }

    @Override
    public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TweetHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profile_tweet, parent, false));
    }

    @Override
    public void onBindViewHolder(TweetHolder holder, int position) {
        final Tweet tweet = this.tweets.get(position);
        holder.authorTV.setText(tweet.getAuthor().getUserTitle());
        holder.contentTV.setText(tweet.getContent());
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.startActivityForResult(new Intent(activity, TweetsActivity.class).
                        putExtra("parent-id", tweet.getTweetId()).putExtra("page-id"
                        , tweet.getPageId()), 5);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    class TweetHolder extends RecyclerView.ViewHolder {

        TextView authorTV;
        TextView contentTV;
        ImageButton likeBtn;
        ImageButton commentBtn;

        TweetHolder(View itemView) {
            super(itemView);
            this.authorTV = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_author_text_view);
            this.contentTV = (TextView) itemView.findViewById(R.id.adapter_profile_tweet_content_text_view);
            this.likeBtn = (ImageButton) itemView.findViewById(R.id.adapter_tweet_like_button);
            this.commentBtn = (ImageButton) itemView.findViewById(R.id.adapter_tweet_comment_button);
        }
    }
}