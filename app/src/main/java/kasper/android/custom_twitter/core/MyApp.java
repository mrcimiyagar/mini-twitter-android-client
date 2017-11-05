package kasper.android.custom_twitter.core;

import android.app.Application;

import io.realm.Realm;
import kasper.android.custom_twitter.helpers.NetworkHelper;
import kasper.android.custom_twitter.models.database.Auth;
import kasper.android.custom_twitter.models.database.Human;
import kasper.android.custom_twitter.models.database.MyData;

public class MyApp extends Application {

    private static MyApp instance;
    public static MyApp getInstance() {
        return instance;
    }

    private NetworkHelper networkHelper;
    public NetworkHelper getNetworkHelper() {
        return networkHelper;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;

        Realm.init(this);

        Realm realm = Realm.getDefaultInstance();

        MyData myData = realm.where(MyData.class).findFirst();

        if (myData == null) {

            realm.beginTransaction();

            myData = realm.createObject(MyData.class);

            Auth auth = realm.createObject(Auth.class);
            auth.setUserId(-1);
            auth.setPassKey("");

            myData.setAuth(auth);

            Human human = realm.createObject(Human.class);
            human.setHumanId(-1);
            human.setUserTitle("");
            human.setUserBio("");

            myData.setHuman(human);

            realm.commitTransaction();
        }

        realm.close();

        this.networkHelper = new NetworkHelper();
    }
}