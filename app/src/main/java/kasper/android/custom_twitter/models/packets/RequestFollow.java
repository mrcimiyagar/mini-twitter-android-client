package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestFollow extends BaseRequest {

    public long targetUserId;

    @Override
    public int getRequestCode() {
        return 3;
    }
}