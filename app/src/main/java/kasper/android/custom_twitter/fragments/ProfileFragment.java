package kasper.android.custom_twitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.realm.Realm;
import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.activities.PostTweetActivity;
import kasper.android.custom_twitter.adapters.ProfileAdapter;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.extras.LinearDecoration;
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.database.Tweet;
import kasper.android.custom_twitter.models.packets.AnswerGetHumanById;
import kasper.android.custom_twitter.models.packets.AnswerGetTweets;
import kasper.android.custom_twitter.models.packets.RequestGetHumanById;
import kasper.android.custom_twitter.models.packets.RequestGetTweets;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    long humanId;
    String userTitle;
    int postsCount;
    int followersCount;
    int followingsCount;

    private RecyclerView contentRV;
    private FloatingActionButton writeFAB;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_profile, container, false);

        this.contentRV = contentView.findViewById(R.id.fragment_profile_content_recycler_view);
        this.writeFAB = contentView.findViewById(R.id.fragment_profile_write_fab);

        this.contentRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        this.contentRV.addItemDecoration(new LinearDecoration(0, (int)(64 * getResources().getDisplayMetrics().density)));

        Realm realm = Realm.getDefaultInstance();

        MyData myData = realm.where(MyData.class).findFirst();

        humanId = myData.getHuman().getHumanId();
        userTitle = myData.getHuman().getUserTitle();
        postsCount = myData.getHuman().getPostsCount();
        followersCount = myData.getHuman().getFollowers().size();
        followingsCount = myData.getHuman().getFollowing().size();

        this.contentRV.setAdapter(new ProfileAdapter((AppCompatActivity)getActivity(), true, false, humanId
                , userTitle, postsCount, followersCount, followingsCount, new ArrayList<kasper.android
                .custom_twitter.models.memory.Tweet>()));

        this.writeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(getActivity(), PostTweetActivity.class)
                        .putExtra("page-id", humanId).putExtra("parent-id", -1), 4);
            }
        });

        this.contentRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    writeFAB.hide();
                }
                else {
                    writeFAB.show();
                }
            }
        });

        return contentView;
    }

    private void readMyTweets() {

        RequestGetHumanById requestGetHumanById = new RequestGetHumanById();
        requestGetHumanById.humanId = humanId;

        MyApp.getInstance().getNetworkHelper().pushTCP(requestGetHumanById, new OnRequestAnsweredListener() {
            @Override
            public void onRequestAnswered(BaseAnswer rawAnswer) {

                AnswerGetHumanById answerGetHumanById = (AnswerGetHumanById) rawAnswer;

                humanId = answerGetHumanById.human.getHumanId();
                postsCount = answerGetHumanById.human.getPostsCount();
                followersCount = answerGetHumanById.human.getFollowersCount();
                followingsCount = answerGetHumanById.human.getFollowingCount();

                RequestGetTweets requestGetTweets = new RequestGetTweets();
                requestGetTweets.targetUserId = humanId;
                requestGetTweets.targetParentId = -1;

                MyApp.getInstance().getNetworkHelper().pushTCP(requestGetTweets, new OnRequestAnsweredListener() {
                    @Override
                    public void onRequestAnswered(final BaseAnswer rawAnswer) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (rawAnswer.answerStatus == AnswerStatus.OK) {

                                    AnswerGetTweets answerGetTweets = (AnswerGetTweets) rawAnswer;

                                    contentRV.setAdapter(new ProfileAdapter((AppCompatActivity)getActivity(), true, false, humanId
                                            , userTitle, postsCount, followersCount, followingsCount, answerGetTweets.tweets));
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();

        this.readMyTweets();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4) {

            if (resultCode == RESULT_OK) {

                kasper.android.custom_twitter.models.memory.Tweet tweet = (kasper.android.custom_twitter
                        .models.memory.Tweet) data.getExtras().getSerializable("tweet");

                readMyTweets();
            }
        }
    }
}