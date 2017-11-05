package kasper.android.custom_twitter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import java.util.ArrayList;

import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.adapters.SearchHumanAdapter;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.extras.SearchHumanDecoration;
import kasper.android.custom_twitter.models.memory.Human;
import kasper.android.custom_twitter.models.packets.AnswerGetFollowers;
import kasper.android.custom_twitter.models.packets.AnswerGetFollowings;
import kasper.android.custom_twitter.models.packets.RequestGetFollowers;
import kasper.android.custom_twitter.models.packets.RequestGetFollowings;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;
import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class HumansActivity extends AppCompatActivity {

    TextView titleTV;
    RecyclerView contentRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humans);

        final boolean followers = getIntent().getExtras().getBoolean("is-followers-page");
        final long humanId = getIntent().getExtras().getLong("human-id");

        titleTV = (TextView) findViewById(R.id.activity_humans_title_text_view);
        contentRV = (RecyclerView) findViewById(R.id.activity_humans_recycler_view);

        titleTV.setText(followers ? "دنبال کنندگان" : "دنبال شوندگان");

        contentRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        contentRV.addItemDecoration(new SearchHumanDecoration());
        contentRV.setAdapter(new SearchHumanAdapter(new ArrayList<Human>()));

        BaseRequest request;

        if (followers) {
            request = new RequestGetFollowers();
            ((RequestGetFollowers)request).humanId = humanId;
        }
        else {
            request = new RequestGetFollowings();
            ((RequestGetFollowings)request).humanId = humanId;
        }

        MyApp.getInstance().getNetworkHelper().pushTCP(request, new OnRequestAnsweredListener() {
            @Override
            public void onRequestAnswered(BaseAnswer rawAnswer) {

                final ArrayList<Human> result;

                if (followers) {
                    AnswerGetFollowers answerGetFollowers = (AnswerGetFollowers) rawAnswer;
                    result = answerGetFollowers.humans;
                }
                else {
                    AnswerGetFollowings answerGetFollowings = (AnswerGetFollowings) rawAnswer;
                    result = answerGetFollowings.humans;
                }

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                contentRV.setAdapter(new SearchHumanAdapter(result));
                            }
                            catch (Exception ignored) {

                            }
                        }
                    });
                }
                catch (Exception ignored) {

                }
            }
        });
    }

    public void onBackBtnPressed(View view) {
        this.finish();
    }
}