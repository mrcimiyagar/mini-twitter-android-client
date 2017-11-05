package kasper.android.custom_twitter.models.packets;

import java.util.ArrayList;

import kasper.android.custom_twitter.models.memory.Human;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class AnswerGetFollowRequests extends BaseAnswer {

    public ArrayList<Human> humans;
}