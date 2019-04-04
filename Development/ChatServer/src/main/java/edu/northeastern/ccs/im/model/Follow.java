package edu.northeastern.ccs.im.model;

public class Follow {

    private int id;
    private String follower;
    private String following;

    public Follow(String follower, String following) {
        this.follower = follower;
        this.following = following;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

}
