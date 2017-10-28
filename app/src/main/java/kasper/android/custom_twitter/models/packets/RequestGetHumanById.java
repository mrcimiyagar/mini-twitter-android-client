package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestGetHumanById extends BaseRequest {

    public long humanId;

    @Override
    public int getRequestCode() {
        return 8;
    }
}