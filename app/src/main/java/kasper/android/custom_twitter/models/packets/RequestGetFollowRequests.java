package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestGetFollowRequests extends BaseRequest {

    @Override
    public int getRequestCode() {
        return 15;
    }
}