package kasper.android.custom_twitter.models.database;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Auth extends RealmObject {

    private long userId;
    private String passKey;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPassKey() {
        return passKey;
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }
}