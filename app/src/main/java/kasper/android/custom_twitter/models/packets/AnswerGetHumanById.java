package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.memory.Human;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class AnswerGetHumanById extends BaseAnswer {

    public Human human;
    public int requestCounts;
}