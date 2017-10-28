package kasper.android.custom_twitter.callbacks;

import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public interface OnRequestAnsweredListener {
    void onRequestAnswered(BaseAnswer rawAnswer);
}