/** Copyright (c) 2019 Rohan Gori, Aditi Kacheria, Mansi Jain, Joshua Dick. All rights reserved.*/
package edu.northeastern.ccs.im.model;

/**
 * This class represents a follow object.
 */
public class Follow {

    private int id;
    private String follower;
    private String following;

    /**
     * Constructor for a follow object.
     *
     * @param follower the person following
     * @param following the person being followed
     */
    public Follow(String follower, String following) {
        this.follower = follower;
        this.following = following;
    }

    /**
     * Method to get the #id.
     *
     * @return the #id
     */
    public int getId() {
        return id;
    }

    /**
     * Method to set the id number.
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Method to get the follower.
     *
     * @return the follower
     */
    public String getFollower() {
        return follower;
    }

    /**
     * Method to set the follower.
     *
     * @param follower the username to be set
     */
    public void setFollower(String follower) {
        this.follower = follower;
    }

    /**
     * Method to the following.
     *
     * @return username who is following
     */
    public String getFollowing() {
        return following;
    }

    /**
     * Method to set following.
     *
     * @param following
     */
    public void setFollowing(String following) {
        this.following = following;
    }

}
