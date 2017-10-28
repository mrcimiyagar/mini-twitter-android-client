package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestSearchUserTitle extends BaseRequest {

    public String query;

    @Override
    public int getRequestCode() {
        return 7;
    }
}