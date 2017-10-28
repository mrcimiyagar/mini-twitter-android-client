package kasper.android.custom_twitter.models.packets;

import kasper.android.custom_twitter.models.packets.base.BaseNotify;

public class NotifyUnFollow extends BaseNotify {

    private long unFollowerId;

    public long getUnFollowerId() {
        return unFollowerId;
    }

    public void setUnFollowerId(long unFollowerId) {
        this.unFollowerId = unFollowerId;
    }
}