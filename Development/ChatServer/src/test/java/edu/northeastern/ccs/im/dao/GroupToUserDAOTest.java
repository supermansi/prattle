package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.northeastern.ccs.im.model.User;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;
import org.mockito.*;
import org.mockito.internal.matchers.Null;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupToUserDAOTest {

  private static GroupToUserDAO groupToUserDAO;
  private Groups group1;
  private boolean isException;
  private User createdUser;

  @Mock
  private ConnectionManager mockManager;
  @Mock
  private Connection mockConnection;
  @Mock
  private PreparedStatement mockStatement;
  @Mock
  private ResultSet mockResultSet;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException, SQLException {
    groupToUserDAO = GroupToUserDAO.getInstance();
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionManager());
    MockitoAnnotations.initMocks(this);
    assertNotNull(mockManager);
    groupToUserDAO.connectionManager = mockManager;
    groupToUserDAO = GroupToUserDAO.getInstance();
    group1 = new Groups("testGroupBlah", "admin1");
    isException = false;
    createdUser = new User(2,"blah","blah","blah","blah@gmail.com","blah");

    //when(mockGroupDAO.getInstance()).thenReturn(null);
    when(mockManager.getConnection()).thenReturn(mockConnection);
    when(mockConnection.prepareStatement(any(String.class), any(Integer.class))).thenReturn(mockStatement);
    when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStatement);
    when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
    when(mockStatement.executeQuery()).thenReturn(mockResultSet);
  }

  @Test
  public void testAddUser() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.checkGroupExists(1)).thenReturn(true);

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.isUserExists(2)).thenReturn(true);

    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true);
    Class clazz = GroupToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(groupToUserDAO, mockGroupDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(groupToUserDAO, mockUserDAO);
    groupToUserDAO.addUserToGroup(2, 1);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserFail() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.checkGroupExists(1)).thenReturn(false);

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.isUserExists(2)).thenReturn(false);

    Class clazz = GroupToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(groupToUserDAO, mockGroupDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(groupToUserDAO, mockUserDAO);
    groupToUserDAO.addUserToGroup(2, 1);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserFail2() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.checkGroupExists(1)).thenReturn(true);

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.isUserExists(2)).thenReturn(false);

    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    Class clazz = GroupToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(groupToUserDAO, mockGroupDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(groupToUserDAO, mockUserDAO);
    groupToUserDAO.addUserToGroup(2, 1);
  }

  @Test(expected = SQLException.class)
  public void testAddUserConnectionEx() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.checkGroupExists(1)).thenReturn(true);

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.isUserExists(2)).thenReturn(true);

    doThrow(new SQLException()).when(mockConnection).prepareStatement(any());
    Class clazz = GroupToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(groupToUserDAO, mockGroupDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(groupToUserDAO, mockUserDAO);
    groupToUserDAO.addUserToGroup(2, 1);
  }

  @Test(expected = SQLException.class)
  public void testAddUserFailStatement() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.checkGroupExists(1)).thenReturn(true);

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.isUserExists(2)).thenReturn(true);

    doThrow(new SQLException()).when(mockStatement).executeUpdate();
    Class clazz = GroupToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(groupToUserDAO, mockGroupDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(groupToUserDAO, mockUserDAO);
    groupToUserDAO.addUserToGroup(2, 1);
  }

  @Test
  public void testCheckIfUserInGroup() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    assertTrue(groupToUserDAO.checkIfUserInGroup(1,2));
  }

  @Test
  public void testCheckIfUserInGroupFalse() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    assertFalse(groupToUserDAO.checkIfUserInGroup(1,1));
  }

  @Test(expected = SQLException.class)
  public void testCheckIfUserInGroupException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any());
    groupToUserDAO.checkIfUserInGroup(1,2);
  }

  @Test(expected = SQLException.class)
  public void testCheckIfUserInGroupStatement() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockStatement).executeQuery();
    groupToUserDAO.checkIfUserInGroup(1,2);
  }

  @Test
  public void testDeleteUserFromAllGroups() throws SQLException {
    when(mockStatement.executeUpdate()).thenReturn(1);
    groupToUserDAO.deleteUserFromAllGroups(1);
  }

  @Test(expected = SQLException.class)
  public void testDeleteUserFromAllConnection() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement((any()));
    groupToUserDAO.deleteUserFromAllGroups(1);
  }

  @Test(expected = SQLException.class)
  public void testDeleteFromAllStatement() throws SQLException {
    doThrow(new SQLException()).when(mockStatement).executeUpdate();
    groupToUserDAO.deleteUserFromAllGroups(1);
  }

  @Test
  public void testDeleteUserFromGroup() throws SQLException {
    GroupToUserDAO mockDAO = Mockito.spy(groupToUserDAO);
    doReturn(true).when(mockDAO).checkIfUserInGroup(1,2);
    mockDAO.deleteUserFromGroup(1,2);
  }

  @Test
  public void testDeleteUserFromGroupFail() throws SQLException {
    GroupToUserDAO mockDAO = Mockito.spy(groupToUserDAO);
    doReturn(false).when(mockDAO).checkIfUserInGroup(1,2);
    mockDAO.deleteUserFromGroup(1,2);
  }

  @Test(expected = SQLException.class)
  public void testDeleteFromGroupConnectionException() throws SQLException {
    GroupToUserDAO mockDAO = Mockito.spy(groupToUserDAO);
    doReturn(true).when(mockDAO).checkIfUserInGroup(1,2);
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any());
    mockDAO.deleteUserFromGroup(1,2);
  }

  @Test(expected = SQLException.class)
  public void testDeleteFromGroupStatementException() throws SQLException {
    GroupToUserDAO mockDAO = Mockito.spy(groupToUserDAO);
    doReturn(true).when(mockDAO).checkIfUserInGroup(1,2);
    doThrow(new SQLException()).when(mockStatement).executeUpdate();
    mockDAO.deleteUserFromGroup(1,2);
  }

  @Test
  public void testGetAllUsersInGroup() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123,"Group","admin1 admin2"));

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUserID(any(Integer.class))).thenReturn(new User("r","r","r","r","r"));

    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    Class clazz = GroupToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(groupToUserDAO, mockGroupDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(groupToUserDAO, mockUserDAO);

    List<String> groups = groupToUserDAO.getAllUsersInGroup("Group");
    assertEquals(2, groups.size());
  }

  @Test(expected = SQLException.class)
  public void testGetAllUsersException() throws SQLException, NoSuchFieldException, IllegalAccessException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));

    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123,"Group","admin1 admin2"));

    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    Class clazz = GroupToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(groupToUserDAO, mockGroupDAO);

    List<String> groups = groupToUserDAO.getAllUsersInGroup("Group");
    assertEquals(2, groups.size());
  }

  @Test(expected = SQLException.class)
  public void testGetAllUsersException2() throws SQLException, NoSuchFieldException, IllegalAccessException {
    doThrow(new SQLException()).when(mockStatement).executeQuery();

    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123,"Group","admin1 admin2"));

    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    Class clazz = GroupToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(groupToUserDAO, mockGroupDAO);

    List<String> groups = groupToUserDAO.getAllUsersInGroup("Group");
    assertEquals(2, groups.size());
  }

  @Test(expected = NullPointerException.class)
  public void testGetAllUsersNullSet() throws SQLException, NoSuchFieldException, IllegalAccessException {
    when(mockStatement.executeQuery()).thenReturn(null);

    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123,"Group","admin1 admin2"));

    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    Class clazz = GroupToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(groupToUserDAO, mockGroupDAO);

    List<String> groups = groupToUserDAO.getAllUsersInGroup("Group");
    assertEquals(2, groups.size());
  }

  @Test
  public void testGetGroupMemberCount() throws SQLException {
    when(mockResultSet.getInt(1)).thenReturn(3);
    when(mockResultSet.next()).thenReturn(true);
    assertEquals(3,groupToUserDAO.getGroupMemberCount(22));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetGroupMemberCountZero() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    assertEquals(0,groupToUserDAO.getGroupMemberCount(22));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetGroupMemberCountExceptionResultSet() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    when(mockStatement.executeQuery()).thenReturn(mockResultSet);
    assertEquals(0,groupToUserDAO.getGroupMemberCount(22));
  }

  @Test(expected = SQLException.class)
  public void testGetGroupMemberCountExceptionConnection() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    assertEquals(0,groupToUserDAO.getGroupMemberCount(22));
  }

  @Test(expected = SQLException.class)
  public void testGetAllUsersByGroupExceptionConnection() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    assertEquals(new ConcurrentHashMap<String,List<String>>(),groupToUserDAO.getAllUsersByGroup());
  }

  @Test(expected = SQLException.class)
  public void testGetAllUsersByGroupExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockStatement).executeQuery();
    assertEquals(new ConcurrentHashMap<String,List<String>>(),groupToUserDAO.getAllUsersByGroup());
  }

  @Test
  public void testGetAllUsersByGroup() throws SQLException {
    ConcurrentMap<String,List<String>> map = new ConcurrentHashMap<>();
    List<String> group1 = new ArrayList<>();
    List<String> group2 = new ArrayList<>();
    group1.add("a1");
    group1.add("a2");
    group1.add("a3");
    group2.add("m1");
    group2.add("m2");
    map.put("Group1",group1);
    map.put("Group2",group2);
    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString(1)).thenReturn("Group1").thenReturn("Group1").thenReturn("Group1").thenReturn("Group1").thenReturn("Group2").thenReturn("Group2").thenReturn("Group2");
    when(mockResultSet.getString(2)).thenReturn("a1").thenReturn("a2").thenReturn("a3").thenReturn("m1").thenReturn("m2");
    assertEquals(map,groupToUserDAO.getAllUsersByGroup());
  }

  @Test
  public void testGetAllUsersByGroupEmpty() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    assertEquals(new ConcurrentHashMap<String,List<String>>(),groupToUserDAO.getAllUsersByGroup());
  }

  @Test
  public void testFollowThreadNotification() throws SQLException {
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    assertEquals(1, groupToUserDAO.getFollowThreadNotification("admin").size());
  }

  @Test(expected = SQLException.class)
  public void testFollowThreadQueryEx() throws SQLException {
    doThrow(new SQLException()).when(mockStatement).executeQuery();
    assertEquals(1, groupToUserDAO.getFollowThreadNotification("admin").size());
  }

  @Test(expected = SQLException.class)
  public void testFollowThreadQStatementEx() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(String.class), any(Integer.class));
    assertEquals(1, groupToUserDAO.getFollowThreadNotification("admin").size());
  }

}
