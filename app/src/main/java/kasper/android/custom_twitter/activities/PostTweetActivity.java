package kasper.android.custom_twitter.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.memory.Tweet;
import kasper.android.custom_twitter.models.packets.AnswerPostTweet;
import kasper.android.custom_twitter.models.packets.RequestPostTweet;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class PostTweetActivity extends AppCompatActivity {

    private long pageId;
    private int parentId;

    private EditText tweetContentET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_tweet);

        this.pageId = getIntent().getExtras().getLong("page-id");
        this.parentId = getIntent().getExtras().getInt("parent-id");

        this.tweetContentET = (EditText) findViewById(R.id.activity_post_tweet_content_edit_text);
    }

    public void onBackBtnClicked(View view) {
        setResult(RESULT_CANCELED);
        this.finish();
    }

    public void onOkBtnClicked(View view) {

        if (this.tweetContentET.getText().toString().length() > 0) {

            RequestPostTweet requestPostTweet = new RequestPostTweet();
            requestPostTweet.pageId = pageId;
            requestPostTweet.parentId = parentId;
            requestPostTweet.tweetContent = tweetContentET.getText().toString();

            MyApp.getInstance().getNetworkHelper().pushTCP(requestPostTweet, new OnRequestAnsweredListener() {
                @Override
                public void onRequestAnswered(final BaseAnswer rawAnswer) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (rawAnswer.answerStatus == AnswerStatus.OK) {

                                AnswerPostTweet answerPostTweet = (AnswerPostTweet) rawAnswer;

                                Tweet tweet = answerPostTweet.tweet;

                                Toast.makeText(PostTweetActivity.this, "Tweet posted successfully.", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK, new Intent().putExtra("tweet", tweet));
                                PostTweetActivity.this.finish();
                            }
                            else {
                                Toast.makeText(PostTweetActivity.this, "There was error posting tweet. " + rawAnswer.answerStatus, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
        else {

            Toast.makeText(this, "Tweet content can not be empty", Toast.LENGTH_SHORT).show();
        }
    }
}