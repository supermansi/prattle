package edu.northeastern.ccs.im.models;

import edu.northeastern.ccs.im.model.Follow;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FollowTest {

    Follow follow = new Follow("r", "j");

    @Test
    public void testId() {
        follow.setId(1);
        assertEquals(1, follow.getId());
    }

    @Test
    public void testFollower() {
        follow.setFollower("mansi");
        assertEquals("mansi", follow.getFollower());
    }

    @Test
    public void testFollowing() {
        follow.setFollowing("mansi");
        assertEquals("mansi", follow.getFollowing());
    }
}
