package kasper.android.custom_twitter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import io.realm.Realm;
import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.adapters.ProfileAdapter;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.memory.Human;
import kasper.android.custom_twitter.models.memory.Tweet;
import kasper.android.custom_twitter.models.packets.AnswerGetHumanById;
import kasper.android.custom_twitter.models.packets.AnswerGetTweets;
import kasper.android.custom_twitter.models.packets.RequestGetHumanById;
import kasper.android.custom_twitter.models.packets.RequestGetTweets;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class ProfileActivity extends AppCompatActivity {

    long humanId;
    String userTitle;
    int postsCount;
    int followersCount;
    int followingsCount;
    boolean isMe;
    boolean isFollowed;

    private RecyclerView contentRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.contentRV = findViewById(R.id.activity_profile_content_recycler_view);

        this.contentRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        humanId = getIntent().getExtras().getLong("human-id");

        Realm realm = Realm.getDefaultInstance();

        final MyData myData = realm.where(MyData.class).findFirst();

        long myHumanId = myData.getHuman().getHumanId();

        isMe = myHumanId == humanId;

        if (isMe) {
            userTitle = myData.getHuman().getUserTitle();
            postsCount = myData.getHuman().getPostsCount();
            followersCount = myData.getHuman().getFollowers().size();
            followingsCount = myData.getHuman().getFollowing().size();

            readTweets();
        }
        else {
            RequestGetHumanById requestGetHumanById = new RequestGetHumanById();
            requestGetHumanById.humanId = humanId;

            MyApp.getInstance().getNetworkHelper().pushTCP(requestGetHumanById, new OnRequestAnsweredListener() {
                @Override
                public void onRequestAnswered(BaseAnswer rawAnswer) {

                    final AnswerGetHumanById answerGetHumanById = (AnswerGetHumanById) rawAnswer;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Human human = answerGetHumanById.human;

                            if (human != null) {

                                Realm realm = Realm.getDefaultInstance();

                                MyData myData = realm.where(MyData.class).findFirst();

                                isFollowed = myData.getHuman().getFollowing().where().equalTo("humanId", humanId).count() > 0;

                                realm.close();

                                userTitle = human.getUserTitle();
                                postsCount = human.getPostsCount();
                                followersCount = human.getFollowersCount();
                                followingsCount = human.getFollowingCount();

                                readTweets();
                            }
                        }
                    });
                }
            });
        }

        realm.close();
    }

    private void readTweets() {

        RequestGetTweets requestGetTweets = new RequestGetTweets();
        requestGetTweets.targetUserId = humanId;
        requestGetTweets.targetParentId = -1;

        MyApp.getInstance().getNetworkHelper().pushTCP(requestGetTweets, new OnRequestAnsweredListener() {
            @Override
            public void onRequestAnswered(final BaseAnswer rawAnswer) {

                ProfileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (rawAnswer.answerStatus == AnswerStatus.OK) {

                            AnswerGetTweets answerGetTweets = (AnswerGetTweets) rawAnswer;

                            contentRV.setAdapter(new ProfileAdapter(ProfileActivity.this, isMe, isFollowed, humanId
                                    , userTitle, postsCount, followersCount, followingsCount, answerGetTweets.tweets));
                        }
                    }
                });
            }
        });
    }

    public void onBackBtnClicked(View view) {
        this.finish();
    }
}