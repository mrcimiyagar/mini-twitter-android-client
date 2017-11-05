package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestAcceptFollowRequest extends BaseRequest {

    public long targetRequestId;

    @Override
    public int getRequestCode() {
        return 16;
    }
}