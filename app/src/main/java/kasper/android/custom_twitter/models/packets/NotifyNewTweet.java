package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.memory.Tweet;
import kasper.android.custom_twitter.models.packets.base.BaseNotify;

public class NotifyNewTweet extends BaseNotify {

    private Tweet tweet;

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }
}