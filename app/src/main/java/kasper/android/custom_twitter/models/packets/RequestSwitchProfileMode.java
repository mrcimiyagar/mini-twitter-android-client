package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestSwitchProfileMode extends BaseRequest {

    public boolean isPrivate;

    @Override
    public int getRequestCode() {
        return 19;
    }
}