package edu.northeastern.ccs.im;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for the methods in the Message class.
 */
public class MessageTest {

    /**
     * Test to instantiate a quit message.
     */
    @Test
    public void makeQuitMessage() {

        Message q = Message.makeQuitMessage("user");
        assertEquals("BYE 4 user 2 --", q.toString());
        assertEquals(true, q.terminate());
        assertEquals("user",q.getName());
    }

    /**
     * Test to instantiate a broadcast message.
     */
    @Test
    public void makeBroadcastMessage() {
        Message b = Message.makeBroadcastMessage("user", "Hello World.");
        assertEquals("BCT 4 user 12 Hello World.", b.toString());
    }

    /**
     * Test to instantiate a hello message.
     */
    @Test
    public void makeHelloMessage() {
        Message h = Message.makeHelloMessage("user");
        assertEquals("HLO 2 -- 4 user", h.toString());
    }

    /**
     * Test making different message types with the makeMessage method.
     */
    @Test
    public void makeMessage() {
        Message m1 = Message.makeMessage("BYE", "user", "goodbye");
        Message m3 = Message.makeMessage("BCT", "user", "how are you?");
        Message m2 = Message.makeMessage("HLO", "user", "Hello World.");
        Message m4 = Message.makeMessage("hi", null, null);
        assertEquals("BYE 4 user 2 --", m1.toString());

        assertEquals("HLO 4 user 12 Hello World.", m2.toString());

        assertEquals("BCT 4 user 12 how are you?", m3.toString());
    }

    @Test
    public void testRegMessage(){
        Message rm = Message.makeMessage("REG", "josh", "reg");
        assertEquals(true, rm.isRegistration());
    }

    @Test
    public void testGroupMessage() {
        Message gm = Message.makeMessage("GRP", "MSD", "hey");
        assertEquals(true, gm.isGroupMessage());
        assertEquals(false, gm.isPrivateMessage());
    }

    @Test
    public void testPrivateMessage() {
        Message pm = Message.makeMessage("PVT", "josh", "hello");
        assertEquals(true, pm.isPrivateMessage());
        assertEquals(false, pm.isGroupMessage());
    }

    @Test
    public void testAckMessage() {
        Message am = Message.makeMessage("ACK", "R", "text");
        assertEquals(true, am.isAcknowledge());
        assertEquals(false, am.isNonAcknowledge());
    }

    @Test
    public void testNoAck(){
        Message na = Message.makeMessage("NAK", "A", "text");
        assertEquals(true, na.isNonAcknowledge());
        assertEquals(false, na.isAcknowledge());
        assertEquals(false, na.isRegistration());
    }

    @Test
    public void testUFN() {
        Message ufn = Message.makeMessage("UFN", "J", "R");
        assertEquals(true, ufn.isUpdateFirstName());
        assertEquals(false, ufn.isUpdateLastName());
    }

    @Test
    public void testULN() {
        Message uln = Message.makeMessage("ULN", "R", "M");
        assertEquals(true, uln.isUpdateLastName());
        assertEquals(false, uln.isUpdateFirstName());
    }

    @Test
    public void testUPW() {
        Message upw = Message.makeMessage("UPW", "A", "pass");
        assertEquals(true, upw.isUpdatePassword());
        assertEquals(false, upw.isUpdateEmail());
    }

    @Test
    public void testUEM() {
        Message uem = Message.makeMessage("UEM", "A", "123@gmail.com");
        assertEquals(true, uem.isUpdateEmail());
        assertEquals(false, uem.isUpdatePassword());
    }

    @Test
    public void testCGR() {
        Message cgm = Message.makeMessage("CGR", "A", "123@gmail.com");
        assertEquals(true, cgm.isCreateGroup());
        assertEquals(false, cgm.isDeleteGroup());
    }

    @Test
    public void testDeleteGroup() {
        Message dgr =Message.makeMessage("DGR", "A", "MSD");
        assertEquals(true, dgr.isDeleteGroup());
        assertEquals(false, dgr.isCreateGroup());
    }

    @Test
    public void testRemoveUser() {
        Message rmu = Message.makeMessage("RMU", "J", "A");
        assertEquals(true, rmu.isRemoveUser());
        assertEquals(false, rmu.isAddUserToGroup());
    }

    @Test
    public void testAddUser() {
        Message aus = Message.makeMessage("AUG", "R", "R");
        assertEquals(true, aus.isAddUserToGroup());
        assertEquals(false, aus.isRemoveUser());
    }

    @Test
    public void testRetrieveUser() {
        Message rus = Message.makeMessage("RTU", "K", "L");
        assertEquals(true, rus.isRetrieveUser());
        assertEquals(false, rus.isRetrieveGroup());
    }

    @Test
    public void testRetrieveGroup() {
        Message rgp = Message.makeMessage("RTG", "J", "MSD");
        assertEquals(true, rgp.isRetrieveGroup());
        assertEquals(false, rgp.isRetrieveUser());
    }

    @Test
    public void testDeleteUser() {
        Message dus = Message.makeMessage("DUS", "A", "A");
        assertEquals(true, dus.isDeactivateUser());
        assertEquals(false, dus.isCreateGroup());
    }

    @Test
    public void testUserExists() {
        Message uex = Message.makeMessage("UEX", "M", "M");
        assertEquals(true, uex.isUserExists());
        assertEquals(false, uex.isRegistration());
        assertEquals(false, uex.isDeactivateUser());
    }

    @Test
    public void testLastSeen() {
        Message lsn = Message.makeMessage("LSN", "J", "/getLastSeen J");
        assertEquals(true, lsn.isLastSeen());
        assertEquals(false, lsn.isCreateGroup());
        assertEquals(false, lsn.isUserExists());
    }


    @Test
    public void testMakeAdmin() {
        Message mam = Message.makeMessage("MAD", "A", "/makeAdmin MSD R");
        assertEquals(true, mam.isMakeAdmin());
        assertEquals(false, mam.isLastSeen());
        assertEquals(false, mam.isLeaveGroup());
    }

    @Test
    public void testLeaveGroup() {
        Message lgm = Message.makeMessage("LGR", "A", "/leaveGroup MSD");
        assertEquals(true, lgm.isLeaveGroup());
        assertEquals(false, lgm.isLastSeen());
        assertEquals(false, lgm.isMakeAdmin());
    }

    @Test
    public void testChangeGroupRestriction() {
        Message cgrm = Message.makeMessage("SGR", "M", "/setGroupRestriction MSD H");
        assertEquals(true, cgrm.isChangeGroupRestriction());
        assertEquals(false, cgrm.isCreateGroup());
    }

    @Test
    public void testRecallMessage() {
        Message rm = Message.makeMessage("RCL","J", "/recall");
        assertEquals(true, rm.isRecall());
        assertEquals(false, rm.isCreateGroup());
    }

    @Test
    public void testReadAttachmentMessage() {
        Message ram = Message.makeMessage("RAM","R", "filepath.txt");
        assertEquals(true, ram.isReadAttachmentMessage());
        assertEquals(false, ram.isLastSeen());
        assertEquals(false, ram.isRecall());
    }

    @Test
    public void testGetUsersInGroupMessage() {
        Message gum = Message.makeMessage("GGU","A", "/getUsersInGroup MSD");
        assertEquals(true, gum.isGetUsersInGroup());
        assertEquals(false, gum.isReadAttachmentMessage());
    }

    @Test
    public void testAttachmentMessage() {
        Message am = Message.makeMessage("ATT","R", "filepath.txt");
        assertEquals(true, am.isAttachmentMessage());
        assertEquals(false, am.isCreateGroup());
        assertEquals(false, am.isGetUsersInGroup());
    }

    /**
     * Test to instantiate a login message.
     */
    @Test
    public void makeSimpleLoginMessage() {
        Message lm = Message.makeSimpleLoginMessage("New User","Hello");
        assertEquals("HLO 8 New User 5 Hello", lm.toString());
    }

    @Test
    public void testGetGroupsUserBelongsTo() {
        Message gag = Message.makeMessage("GUG", "J", "/getMyGroups");
        assertEquals(true, gag.isGetAllGroupsUserBelongsTo());
        assertEquals(false, gag.isDND());
        assertEquals(false, gag.isAttachmentMessage());
    }

    @Test
    public void testDND() {
        Message dnd = Message.makeMessage("DND", "A", "/dnd t");
        assertEquals(true, dnd.isDND());
        assertEquals(false, dnd.isGetAllGroupsUserBelongsTo());
        assertEquals(false, dnd.isChangeGroupRestriction());
    }

    @Test
    public void testGetMessagesBetween() {
        Message gmb = Message.makeMessage("GMB", "M", "/getMessagesBetween 01/10/2019 02/01/2019");
        assertEquals(true, gmb.isGetMessagesBetween());
        assertEquals(false, gmb.isCreateThread());
    }

    @Test
    public void testCreateThread() {
        Message ctd = Message.makeMessage("TRD", "A", "/createThread #MSD");
        assertEquals(true, ctd.isCreateThread());
        assertEquals(false, ctd.isGetMessagesBetween());
    }

    @Test
    public void testPostOnThread() {
        Message pot = Message.makeMessage("POT", "M", "/postToThread #MSD hey all");
        assertEquals(true, pot.isPostOnThread());
        assertEquals(false, pot.isFollowUser());

    }

    @Test
    public void testFollowUser() {
        Message fus = Message.makeMessage("FUS", "Z", "/follow tttt");
        assertEquals(true, fus.isFollowUser());
        assertEquals(false, fus.isPostOnThread());
    }

    @Test
    public void testGetAllThreads() {
        Message gat = Message.makeMessage("GAT", "S", "/getAllThreads");
        assertEquals(true, gat.isGetAllThreads());
        assertEquals(false, gat.isGetThreadMessages());
    }

    @Test
    public void testGetThreadMessages() {
        Message gtm = Message.makeMessage("GTM", "D", "/getThreadMessages");
        assertEquals(true, gtm.isGetThreadMessages());
        assertEquals(false, gtm.isGetAllThreads());
    }

    @Test
    public void testUnfollowUser() {
        Message uus = Message.makeMessage("UUS", "X", "/unfollow tttt");
        assertEquals(true, uus.isUnfollowUser());
        assertEquals(false, uus.isForwardMessage());
        assertEquals(false, uus.isSecretMessage());
    }

    @Test
    public void testForwardMessage() {
        Message fwd = Message.makeMessage("FWD", "H", "/fwd 3 A qqqq");
        assertEquals(true, fwd.isForwardMessage());
        assertEquals(false, fwd.isUnfollowUser());
    }

    @Test
    public void testSecretMessage() {
        Message sms = Message.makeMessage("SMS", "z", "/secretMessage R hey");
        assertEquals(true, sms.isSecretMessage());
        assertEquals(false, sms.isForwardMessage());
    }


    /**
     * Test for the getName method.
     */
    @Test
    public void getName() {
        Message userM = Message.makeMessage("BCT", "user", "hello");
        assertEquals("user", userM.getName());
    }

    /**
     * Test for the getText method.
     */
    @Test
    public void getText() {
        Message text = Message.makeMessage("BCT", "user1", "Hello World.");
        assertEquals("Hello World.", text.getText());
    }

    /**
     * Test for the isBroadcastMessage method.
     */
    @Test
    public void isBroadcastMessage() {
        Message bm = Message.makeMessage("BCT", "user", "hello");
        Message im = Message.makeMessage("HLO", "user", null);
        assertEquals(true, bm.isBroadcastMessage());
        assertEquals(false, im.isBroadcastMessage());
    }

    /**
     * Test for the isInitialization method.
     */
    @Test
    public void isInitialization() {
        Message bm = Message.makeMessage("BCT", "user", "hello");
        Message im = Message.makeMessage("HLO", "user", null);
        assertEquals(true, im.isInitialization());
        assertEquals(false, bm.isInitialization());
    }

    /**
     * Test for the terminate method.
     */
    @Test
    public void terminate() {
        Message qm = Message.makeMessage("BYE", "user", "hello");
        Message im = Message.makeMessage("HLO", "user", null);
        assertEquals(true, qm.terminate());
        assertEquals(false, im.terminate());
    }

    /**
     * Test for the toString method.
     */
    @Test
    public void toStringTest() {
        Message qmnn = Message.makeMessage("BYE", "user", "goodbye");
        Message qmn = Message.makeMessage("BYE", null, "goodbye");
        Message hm = Message.makeMessage("HLO", "user", "Hello World.");
        Message bmnn = Message.makeMessage("BCT", "user", "how are you?");
        Message bmn = Message.makeMessage("BCT", "user", null);

        assertEquals("BYE 4 user 2 --", qmnn.toString());
        assertEquals("BYE 2 -- 2 --", qmn.toString());

        assertEquals("HLO 4 user 12 Hello World.", hm.toString());

        assertEquals("BCT 4 user 12 how are you?", bmnn.toString());
        assertEquals("BCT 4 user 2 --", bmn.toString());
    }
}