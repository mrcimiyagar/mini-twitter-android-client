package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestRegister extends BaseRequest {

    public String userTitle;

    @Override
    public int getRequestCode() {
        return 1;
    }
}