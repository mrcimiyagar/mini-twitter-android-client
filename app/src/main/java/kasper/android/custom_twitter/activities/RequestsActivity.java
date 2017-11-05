package kasper.android.custom_twitter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.adapters.FollowRequestAdapter;
import kasper.android.custom_twitter.adapters.SearchHumanAdapter;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.extras.LinearDecoration;
import kasper.android.custom_twitter.extras.SearchHumanDecoration;
import kasper.android.custom_twitter.models.packets.AnswerGetFollowRequests;
import kasper.android.custom_twitter.models.packets.AnswerGetFollowings;
import kasper.android.custom_twitter.models.packets.RequestGetFollowRequests;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class RequestsActivity extends AppCompatActivity {

    private RecyclerView requestsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        this.requestsRV = findViewById(R.id.activity_requests_recycler_view);
        this.requestsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.requestsRV.addItemDecoration(new LinearDecoration((int)(12 * getResources().getDisplayMetrics().density),
                (int)(4 * getResources().getDisplayMetrics().density)));

        readRequests();
    }

    public void onBackBtnClicked(View view) {
        this.finish();
    }

    private void readRequests() {

        RequestGetFollowRequests requestGetFollowRequests = new RequestGetFollowRequests();

        MyApp.getInstance().getNetworkHelper().pushTCP(requestGetFollowRequests, new OnRequestAnsweredListener() {
            @Override
            public void onRequestAnswered(BaseAnswer rawAnswer) {

                final AnswerGetFollowRequests answerGetFollowRequests = (AnswerGetFollowRequests) rawAnswer;

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                requestsRV.setAdapter(new FollowRequestAdapter(RequestsActivity.this, answerGetFollowRequests.humans));
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
}