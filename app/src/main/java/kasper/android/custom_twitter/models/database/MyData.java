package kasper.android.custom_twitter.models.database;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class MyData extends RealmObject {

    private Auth auth;
    private Human human;

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public Human getHuman() {
        return human;
    }

    public void setHuman(Human human) {
        this.human = human;
    }
}