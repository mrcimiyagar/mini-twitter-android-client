package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestDeleteTweet extends BaseRequest {

    public long tweetNodeId;

    @Override
    public int getRequestCode() {
        return 20;
    }
}