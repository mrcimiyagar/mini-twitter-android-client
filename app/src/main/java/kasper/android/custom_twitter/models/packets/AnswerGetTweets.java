package kasper.android.custom_twitter.models.packets;

import java.util.ArrayList;

import kasper.android.custom_twitter.models.memory.Tweet;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class AnswerGetTweets extends BaseAnswer {

    public ArrayList<Tweet> tweets;
}