package edu.northeastern.ccs.im.dao;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.User;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.model.Groups;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupsDAOTest {

  private static GroupDAO groupDAO;
  private Groups group;
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

//  @AfterClass
//  public static void afterClass() throws NoSuchFieldException, IllegalAccessException {
//    groupDAO = GroupDAO.getInstance();
//    Class clazz = GroupDAO.class;
//    Field connectionManager = clazz.getDeclaredField("connectionManager");
//    connectionManager.setAccessible(true);
//    connectionManager.set(groupDAO, new ConnectionManager());
//  }

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException, SQLException {
    groupDAO = GroupDAO.getInstance();
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionManager());
    MockitoAnnotations.initMocks(this);
    group1 = new Groups("g1", "admin1");
    assertNotNull(mockManager);
    groupDAO.connectionManager = mockManager;

    when(mockManager.getConnection()).thenReturn(mockConnection);
    when(mockConnection.prepareStatement(any(String.class), any(Integer.class))).thenReturn(mockStatement);
    when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStatement);
    when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
    when(mockStatement.executeQuery()).thenReturn(mockResultSet);
  }

  @Test
  public void testCreateGroup() throws SQLException {
    when(mockResultSet.getInt(1)).thenReturn(789);
    when(mockResultSet.next()).thenReturn(true);

    Groups newGroup = groupDAO.createGroup(group1);
    assertEquals(789, newGroup.getGrpID());
    assertEquals("g1", newGroup.getGrpName());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateGroupException() throws SQLException {
      when(mockResultSet.next()).thenReturn(false);
      groupDAO.createGroup(group1);
  }

  @Test(expected = SQLException.class)
  public void testCreateResultSetException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    doThrow(new SQLException()).when(mockStatement).executeQuery();
    groupDAO.createGroup(group1);
  }

  @Test(expected = SQLException.class)
  public void testCreateGroupNull() throws SQLException {
      doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
      groupDAO.createGroup(group1);
  }


  @Test
  public void testGroupExistsName() throws SQLException {
      when(mockResultSet.next()).thenReturn(true);
      assertTrue(groupDAO.checkGroupExists("g1"));
  }

  @Test(expected = SQLException.class)
  public void testGroupExistsNameException() throws SQLException {
      doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
      doThrow(new SQLException()).when(mockStatement).executeQuery();
      groupDAO.checkGroupExists("g1");
  }

  @Test
  public void testGroupExistsID() throws SQLException {
      when(mockResultSet.next()).thenReturn(true);
      assertTrue(groupDAO.checkGroupExists(789));
  }

  @Test(expected = SQLException.class)
  public void testGroupExistsIDExceptio() throws SQLException {
      doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
      doThrow(new SQLException()).when(mockStatement).executeQuery();
      groupDAO.checkGroupExists(1);
  }

  @Test
  public void testValidateAdmin() throws SQLException {
      when(mockResultSet.next()).thenReturn(true);
      assertTrue(groupDAO.validateGroupAdmin("g1", 5));
  }

    @Test
    public void testValidateAdminFalse() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);
        assertFalse(groupDAO.validateGroupAdmin("g1", 1));
    }

  @Test(expected = SQLException.class)
  public void testValidateAdminException() throws SQLException{
      doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
      doThrow(new SQLException()).when(mockStatement).executeQuery();
      groupDAO.validateGroupAdmin("g1", 1);
  }


  @Test
  public void testGroupByName() throws SQLException {
      when(mockResultSet.next()).thenReturn(true);
      when(mockResultSet.getString("grpName")).thenReturn("g1");
      Groups testGroup = groupDAO.getGroupByGroupName("g1");
      assertEquals("g1", testGroup.getGrpName());
  }

  @Test(expected = SQLException.class)
  public void testGroupByNameException() throws SQLException {
      doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
      groupDAO.getGroupByGroupName("g1");
  }

  @Test
  public void testGroupByID() throws SQLException {
      when(mockResultSet.next()).thenReturn(true);
      when(mockResultSet.getInt("grpID")).thenReturn(1);
      Groups testGroup = groupDAO.getGroupByGroupID(1);
      assertEquals(1, testGroup.getGrpID());
  }

  @Test(expected = SQLException.class)
  public void testGroupByIDException() throws SQLException {
      doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
      groupDAO.getGroupByGroupID(1);
  }

  @Test
    public void testDeleteGroupByID() throws SQLException {
      when(mockResultSet.getInt(1)).thenReturn(789);
      when(mockResultSet.next()).thenReturn(true);
      Groups newGroup = groupDAO.createGroup(group1);
      when(mockStatement.executeUpdate()).thenReturn(1);
      groupDAO.deleteGroupByID(789);
      when(mockResultSet.next()).thenReturn(false);
      assertFalse(groupDAO.checkGroupExists(789));
  }

  @Test(expected = SQLException.class)
  public void testDeleteException() throws SQLException {
      doThrow(new SQLException()).when(mockConnection).prepareStatement(any(String.class));
      groupDAO.deleteGroupByID(1);
  }

  @Test
    public void testGetGroup()throws InvocationTargetException, IllegalAccessException, SQLException {
      when(mockResultSet.next()).thenReturn(true);
      when(mockResultSet.getInt("grpID")).thenReturn(1);
      when(mockResultSet.getString("grpName")).thenReturn("g1");
      when(mockResultSet.getInt("adminID")).thenReturn(1);

      Class<GroupDAO> clazz = GroupDAO.class;
      Method method[] = clazz.getDeclaredMethods();
      Method met = null;
      for (Method m : method) {
          if (m.getName().contains("getGroups")) {
              met = m;
          }
      }
      met.setAccessible(true);
      met.invoke(groupDAO, mockStatement);
  }

  @Test(expected = InvocationTargetException.class)
  public void testGetGroupNull()throws InvocationTargetException, IllegalAccessException, SQLException {
      when(mockResultSet.next()).thenReturn(false);
      Class<GroupDAO> clazz = GroupDAO.class;
      Method method[] = clazz.getDeclaredMethods();
      Method met = null;
      for (Method m : method) {
          if (m.getName().contains("getGroups")) {
              met = m;
          }
      }
      met.setAccessible(true);
      met.invoke(groupDAO, mockStatement);
  }

  @Test
  public void testUpdateAdmins() throws SQLException {
      when(mockResultSet.next()).thenReturn(true);
      when(mockStatement.executeUpdate()).thenReturn(1);
      groupDAO.updateAdmin(group1.getGrpName(), "admin1");
  }
}