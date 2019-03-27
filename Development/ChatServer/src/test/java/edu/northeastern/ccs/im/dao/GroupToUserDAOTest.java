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
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;
import org.mockito.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupToUserDAOTest {

  private static GroupToUserDAO groupToUserDAO;
  private Groups group1;
  private boolean isException;

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

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserFail() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.checkGroupExists(1)).thenReturn(false);

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

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserFail2() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.checkGroupExists(1)).thenReturn(true);

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUserID(2)).thenThrow(DatabaseConnectionException.class);
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
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    doThrow(new SQLException()).when(mockStatement).executeQuery();
    groupToUserDAO.checkIfUserInGroup(1,2);
  }

  @Test
  public void testDeleteUserFromAllGroups() throws SQLException {
    when(mockStatement.executeUpdate()).thenReturn(1);
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
}
