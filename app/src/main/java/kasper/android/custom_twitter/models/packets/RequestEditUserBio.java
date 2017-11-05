package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseRequest;

public class RequestEditUserBio extends BaseRequest {

    public String newBio;

    @Override
    public int getRequestCode() {
        return 11;
    }
}