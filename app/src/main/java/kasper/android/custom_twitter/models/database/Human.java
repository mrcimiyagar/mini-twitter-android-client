package kasper.android.custom_twitter.models.database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Human extends RealmObject {

    private long humanId;
    private String userTitle;
    private int postsCount;
    private int followersCount;
    private int followingCount;
    private RealmList<Human> followers;
    private RealmList<Human> following;
    private RealmList<Tweet> tweets;

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

    public long getHumanId() {
        return humanId;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
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

    public RealmList<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(RealmList<Tweet> tweets) {
        this.tweets = tweets;
    }
}