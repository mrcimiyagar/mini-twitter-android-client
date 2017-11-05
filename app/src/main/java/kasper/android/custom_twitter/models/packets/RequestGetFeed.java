package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestGetFeed extends BaseRequest {

    @Override
    public int getRequestCode() {
        return 18;
    }
}