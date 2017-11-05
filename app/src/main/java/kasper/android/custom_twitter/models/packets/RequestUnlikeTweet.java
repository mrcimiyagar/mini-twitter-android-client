package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestUnlikeTweet extends BaseRequest {

    public long pageId;
    public long tweetNodeId;

    @Override
    public int getRequestCode() {
        return 13;
    }
}