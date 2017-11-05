package kasper.android.custom_twitter.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmList;
import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.models.database.Auth;
import kasper.android.custom_twitter.models.database.Human;
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.packets.AnswerLogin;
import kasper.android.custom_twitter.models.packets.AnswerRegister;
import kasper.android.custom_twitter.models.packets.RequestLogin;
import kasper.android.custom_twitter.models.packets.RequestRegister;
import kasper.android.custom_twitter.models.packets.base.AnswerStatus;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class RegisterActivity extends AppCompatActivity {

    RelativeLayout container;
    EditText userTitleET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        this.container = (RelativeLayout) findViewById(R.id.activity_register_container);
        this.userTitleET = (EditText) findViewById(R.id.activity_register_user_title_edit_text);

        MyApp.getInstance().getNetworkHelper().connectToServer(new Runnable() {
            @Override
            public void run() {

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Realm realm = Realm.getDefaultInstance();

                                MyData myData = realm.where(MyData.class).findFirst();

                                Auth auth = myData.getAuth();

                                long userId = auth.getUserId();
                                String passKey = auth.getPassKey();

                                realm.close();

                                if (userId >= 0 && passKey.length() > 0) {

                                    final RequestLogin requestLogin = new RequestLogin();
                                    requestLogin.userId = userId;
                                    requestLogin.passKey = passKey;

                                    MyApp.getInstance().getNetworkHelper().pushTCP(requestLogin, new OnRequestAnsweredListener() {
                                        @Override
                                        public void onRequestAnswered(final BaseAnswer rawAnswer) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    if (rawAnswer.answerStatus == AnswerStatus.OK) {

                                                        AnswerLogin answerLogin = (AnswerLogin) rawAnswer;

                                                        Realm realm = Realm.getDefaultInstance();
                                                        realm.beginTransaction();

                                                        Human human = realm.where(MyData.class).findFirst().getHuman();

                                                        human.setHumanId(requestLogin.userId);
                                                        human.setUserTitle(answerLogin.userTitle);
                                                        human.setPostsCount(answerLogin.postsCount);
                                                        human.setUserBio(answerLogin.userBio);

                                                        realm.commitTransaction();

                                                        realm.close();

                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                    } else {

                                                        RegisterActivity.this.container.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {

                                    RegisterActivity.this.container.setVisibility(View.VISIBLE);
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

    public void onRegisterBtnClicked(View view) {

        if (userTitleET.getText().toString().length() > 0) {

            RequestRegister requestRegister = new RequestRegister();
            requestRegister.userTitle = userTitleET.getText().toString();

            MyApp.getInstance().getNetworkHelper().pushTCP(requestRegister, new OnRequestAnsweredListener() {
                @Override
                public void onRequestAnswered(final BaseAnswer rawAnswer) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            AnswerRegister answerRegister = (AnswerRegister) rawAnswer;

                            Realm realm = Realm.getDefaultInstance();

                            realm.beginTransaction();

                            MyData myData = realm.where(MyData.class).findFirst();

                            Auth auth = myData.getAuth();

                            auth.setUserId(answerRegister.userId);
                            auth.setPassKey(answerRegister.passKey);

                            realm.commitTransaction();

                            realm.close();

                            final RequestLogin requestLogin = new RequestLogin();
                            requestLogin.userId = answerRegister.userId;
                            requestLogin.passKey = answerRegister.passKey;

                            MyApp.getInstance().getNetworkHelper().pushTCP(requestLogin, new OnRequestAnsweredListener() {
                                @Override
                                public void onRequestAnswered(final BaseAnswer rawAnswer) {

                                    AnswerLogin answerLogin = (AnswerLogin) rawAnswer;

                                    Realm realm = Realm.getDefaultInstance();

                                    realm.beginTransaction();

                                    Human human = realm.where(MyData.class).findFirst().getHuman();

                                    human.setHumanId(requestLogin.userId);
                                    human.setUserTitle(answerLogin.userTitle);
                                    human.setPostsCount(answerLogin.postsCount);
                                    human.setUserBio(answerLogin.userBio);

                                    realm.commitTransaction();

                                    realm.close();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (rawAnswer.answerStatus == AnswerStatus.OK) {

                                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            }
                                            else {

                                                RegisterActivity.this.container.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
        else {
            Toast.makeText(this, "Please fill user title field.", Toast.LENGTH_SHORT).show();
        }
    }
}