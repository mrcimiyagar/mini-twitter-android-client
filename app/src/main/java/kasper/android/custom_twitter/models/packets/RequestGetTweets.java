package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestGetTweets extends BaseRequest {

    public long targetUserId;
    public int targetParentId;

    @Override
    public int getRequestCode() {
        return 6;
    }
}