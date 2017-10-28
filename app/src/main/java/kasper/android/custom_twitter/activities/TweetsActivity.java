package kasper.android.custom_twitter.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.adapters.TweetAdapter;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.extras.LinearDecoration;
import kasper.android.custom_twitter.models.packets.AnswerGetTweets;
import kasper.android.custom_twitter.models.packets.RequestGetTweets;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class TweetsActivity extends AppCompatActivity {

    private int parentId;
    private long pageId;

    private RecyclerView commentsRV;
    private FloatingActionButton tweetFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweets);

        this.parentId = getIntent().getExtras().getInt("parent-id");
        this.pageId = getIntent().getExtras().getLong("page-id");

        this.commentsRV = (RecyclerView) findViewById(R.id.activity_tweets_recycler_view);
        this.tweetFAB = (FloatingActionButton) findViewById(R.id.activity_tweets_tweet_fab);

        this.commentsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.commentsRV.addItemDecoration(new LinearDecoration((int)(8 * getResources().getDisplayMetrics().density)
                , (int)(8 * getResources().getDisplayMetrics().density)));

        this.commentsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    tweetFAB.hide();
                }
                else {
                    tweetFAB.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();

        this.readTweetsFromServer();
    }

    public void onBackBtnClicked(View view) {
        this.finish();
    }

    public void onRefreshBtnClicked(View view) {

        this.readTweetsFromServer();
    }

    public void onTweetBtnClicked(View view) {
        startActivityForResult(new Intent(this, PostTweetActivity.class).putExtra("page-id", pageId)
                .putExtra("parent-id", parentId), 4);
    }

    private void readTweetsFromServer() {

        RequestGetTweets requestGetTweets = new RequestGetTweets();
        requestGetTweets.targetParentId = parentId;
        requestGetTweets.targetUserId = pageId;

        MyApp.getInstance().getNetworkHelper().pushTCP(requestGetTweets, new OnRequestAnsweredListener() {
            @Override
            public void onRequestAnswered(BaseAnswer rawAnswer) {

                final AnswerGetTweets answerGetTweets = (AnswerGetTweets) rawAnswer;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        commentsRV.setAdapter(new TweetAdapter(TweetsActivity.this, answerGetTweets.tweets));
                    }
                });
            }
        });
    }
}