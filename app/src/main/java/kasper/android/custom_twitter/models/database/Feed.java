package kasper.android.custom_twitter.models.database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Feed extends RealmObject {

    private RealmList<Tweet> tweets;
    private RealmList<Human> humans;

    public RealmList<Human> getHumans() {
        return humans;
    }

    public void setHumans(RealmList<Human> humans) {
        this.humans = humans;
    }

    public RealmList<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(RealmList<Tweet> tweets) {
        this.tweets = tweets;
    }
}