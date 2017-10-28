package kasper.android.custom_twitter.fragments;

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

import java.util.ArrayList;
import java.util.Hashtable;

import io.realm.Realm;
import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.adapters.TweetAdapter;
import kasper.android.custom_twitter.extras.LinearDecoration;
import kasper.android.custom_twitter.models.database.Feed;
import kasper.android.custom_twitter.models.memory.Tweet;

public class FeedFragment extends Fragment {

    private RecyclerView postsRV;
    private ImageButton refreshBtn;

    public FeedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_feed, container, false);

        postsRV = contentView.findViewById(R.id.fragment_feed_posts_recycler_view);
        refreshBtn = contentView.findViewById(R.id.fragment_feed_refresh_button);

        postsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        postsRV.addItemDecoration(new LinearDecoration((int)(8 * getResources().getDisplayMetrics().density)
                , (int)(8 * getResources().getDisplayMetrics().density)));

        refreshFromDatabase();

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refreshFromDatabase();
            }
        });

        return contentView;
    }

    private void refreshFromDatabase() {

        Realm realm = Realm.getDefaultInstance();

        Feed feed = realm.where(Feed.class).findFirst();

        ArrayList<Tweet> mTweets = new ArrayList<>();

        Hashtable<Long, kasper.android.custom_twitter.models.memory.Human> cachedHumans = new Hashtable<>();

        for (kasper.android.custom_twitter.models.database.Tweet dTweet : feed.getTweets()) {
            Tweet tweet = new Tweet();
            tweet.setTweetId(dTweet.getTweetId());
            tweet.setPageId(dTweet.getPageId());
            tweet.setParentId(dTweet.getParentId());

            if (cachedHumans.containsKey(dTweet.getAuthor().getHumanId())) {
                tweet.setAuthor(cachedHumans.get(dTweet.getAuthor().getHumanId()));
            }
            else {
                kasper.android.custom_twitter.models.memory.Human human = new kasper.android.custom_twitter.models.memory.Human();
                human.setHumanId(dTweet.getAuthor().getHumanId());
                human.setUserTitle(dTweet.getAuthor().getUserTitle());
                tweet.setAuthor(human);

                cachedHumans.put(human.getHumanId(), human);
            }

            tweet.setContent(dTweet.getContent());
            tweet.setTime(dTweet.getTime());

            mTweets.add(tweet);
        }

        realm.close();

        postsRV.setAdapter(new TweetAdapter((AppCompatActivity) getActivity(), mTweets));
    }
}