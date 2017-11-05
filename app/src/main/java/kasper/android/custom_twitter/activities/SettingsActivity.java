package kasper.android.custom_twitter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;

import io.realm.Realm;
import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.packets.AnswerSwitchProfileMode;
import kasper.android.custom_twitter.models.packets.RequestSwitchProfileMode;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class SettingsActivity extends AppCompatActivity {

    private RadioButton profilePrivateRB;
    private RadioButton profilePublicRB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.profilePublicRB = (RadioButton) findViewById(R.id.activity_settings_profile_public_radio_button);
        this.profilePrivateRB = (RadioButton) findViewById(R.id.activity_settings_profile_private_radio_button);

        Realm realm = Realm.getDefaultInstance();

        MyData myData = realm.where(MyData.class).findFirst();

        profilePublicRB.setChecked(!myData.getHuman().isPrivate());
        profilePrivateRB.setChecked(myData.getHuman().isPrivate());

        profilePublicRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notifyServerAboutProfileMode(false);

                profilePrivateRB.setChecked(false);
            }
        });

        profilePrivateRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notifyServerAboutProfileMode(true);

                profilePublicRB.setChecked(false);
            }
        });

        realm.close();
    }

    private void notifyServerAboutProfileMode(boolean isPrivate) {

        RequestSwitchProfileMode requestSwitchProfileMode = new RequestSwitchProfileMode();
        requestSwitchProfileMode.isPrivate = isPrivate;

        MyApp.getInstance().getNetworkHelper().pushTCP(requestSwitchProfileMode, new OnRequestAnsweredListener() {
            @Override
            public void onRequestAnswered(BaseAnswer rawAnswer) {

                AnswerSwitchProfileMode answerSwitchProfileMode = (AnswerSwitchProfileMode) rawAnswer;

                if (answerSwitchProfileMode.answerStatus == AnswerStatus.OK) {

                    Realm realm = Realm.getDefaultInstance();

                    realm.beginTransaction();

                    realm.where(MyData.class).findFirst().getHuman().setPrivate(answerSwitchProfileMode.newState);

                    realm.commitTransaction();

                    realm.close();
                }
            }
        });
    }

    public void onBackBtnClicked(View view) {
        this.finish();
    }
}