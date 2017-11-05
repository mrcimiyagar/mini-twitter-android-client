package kasper.android.custom_twitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.Gson;

import io.realm.Realm;
import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.adapters.ProfileAdapter;
import kasper.android.custom_twitter.adapters.TweetAdapter;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.extras.LinearDecoration;
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.packets.AnswerGetFeed;
import kasper.android.custom_twitter.models.packets.RequestGetFeed;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

import static android.app.Activity.RESULT_OK;

public class FeedFragment extends Fragment {

    private RecyclerView postsRV;
    private ImageButton refreshBtn;
    private long myId;

    public FeedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_feed, container, false);

        postsRV = contentView.findViewById(R.id.fragment_feed_posts_recycler_view);
        refreshBtn = contentView.findViewById(R.id.fragment_feed_refresh_button);

        Realm realm = Realm.getDefaultInstance();

        myId = realm.where(MyData.class).findFirst().getHuman().getHumanId();

        realm.close();

        postsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        postsRV.addItemDecoration(new LinearDecoration((int)(8 * getResources().getDisplayMetrics().density)
                , (int)(8 * getResources().getDisplayMetrics().density)));

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                readTweetsFromServer();
            }
        });

        return contentView;
    }

    @Override
    public void onResume() {

        super.onResume();

        readTweetsFromServer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {

            if (resultCode == RESULT_OK) {

                if (data.getExtras().getString("dialog-result").equals("yes")) {
                    ((TweetAdapter) postsRV.getAdapter()).notifyOnYesNoDialogOkResult();
                }
            }
        }
    }

    private void readTweetsFromServer() {

        RequestGetFeed requestGetFeed = new RequestGetFeed();

        MyApp.getInstance().getNetworkHelper().pushTCP(requestGetFeed, new OnRequestAnsweredListener() {
            @Override
            public void onRequestAnswered(BaseAnswer rawAnswer) {

                final AnswerGetFeed answerGetFeed = (AnswerGetFeed) rawAnswer;

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                postsRV.setAdapter(new TweetAdapter((AppCompatActivity) getActivity()
                                        , FeedFragment.this, myId, -1, answerGetFeed.tweets));
                            } catch (Exception ignored) {

                            }
                        }
                    });
                } catch (Exception ignored) {

                }
            }
        });
    }
}