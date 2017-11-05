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
import kasper.android.custom_twitter.models.packets.AnswerEditUserBio;
import kasper.android.custom_twitter.models.packets.AnswerPostTweet;
import kasper.android.custom_twitter.models.packets.RequestEditUserBio;
import kasper.android.custom_twitter.models.packets.RequestPostTweet;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class EditProfileActivity extends AppCompatActivity {

    private EditText tweetContentET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        this.tweetContentET = (EditText) findViewById(R.id.activity_post_tweet_content_edit_text);
    }

    public void onBackBtnClicked(View view) {
        this.finish();
    }

    public void onOkBtnClicked(View view) {

        if (this.tweetContentET.getText().toString().length() > 0) {

            final RequestEditUserBio requestEditUserBio = new RequestEditUserBio();
            requestEditUserBio.newBio = tweetContentET.getText().toString();

            MyApp.getInstance().getNetworkHelper().pushTCP(requestEditUserBio, new OnRequestAnsweredListener() {
                @Override
                public void onRequestAnswered(final BaseAnswer rawAnswer) {

                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {

                                    if (rawAnswer.answerStatus == AnswerStatus.OK) {

                                        Realm realm = Realm.getDefaultInstance();

                                        realm.beginTransaction();

                                        realm.where(MyData.class).findFirst().getHuman().setUserBio(requestEditUserBio.newBio);

                                        realm.commitTransaction();

                                        realm.close();

                                        Toast.makeText(EditProfileActivity.this, "توضیح پروفایل با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show();
                                        EditProfileActivity.this.finish();
                                    } else {
                                        Toast.makeText(EditProfileActivity.this, "خطایی در ویرایش توضیح پروفایل رخ داد", Toast.LENGTH_SHORT).show();
                                    }
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
        else {

            Toast.makeText(this, "توضیح پروفایل نمی تواند خالی باشد", Toast.LENGTH_SHORT).show();
        }
    }
}