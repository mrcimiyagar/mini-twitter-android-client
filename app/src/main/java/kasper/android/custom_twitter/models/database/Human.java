package kasper.android.custom_twitter.models.database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Human extends RealmObject {

    private long humanId;
    private String userTitle;
    private String userBio;
    private int postsCount;
    private RealmList<Human> followers;
    private RealmList<Human> following;
    private RealmList<Human> requested;
    private boolean isPrivate;

    public long getHumanId() {
        return humanId;
    }

    public void setHumanId(long humanId) {
        this.humanId = humanId;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public RealmList<Human> getFollowers() {
        return followers;
    }

    public void setFollowers(RealmList<Human> followers) {
        this.followers = followers;
    }

    public RealmList<Human> getFollowing() {
        return following;
    }

    public void setFollowing(RealmList<Human> following) {
        this.following = following;
    }

    public RealmList<Human> getRequested() {
        return requested;
    }

    public void setRequested(RealmList<Human> requested) {
        this.requested = requested;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}