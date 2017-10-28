package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestUnFollow extends BaseRequest {

    public long targetUserId;

    @Override
    public int getRequestCode() {
        return 4;
    }
}