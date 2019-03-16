package edu.northeastern.ccs.im.services;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupServicesTest {
	
	GroupServices groupServices;

	@Before
	public void setup(){



	}

	@Test
	public void testGroupServiceMethods(){

		GroupServices.createGroup("groupJosh", "admin");
		GroupServices.addUserToGroup("groupJosh", "admin", "j");
		GroupServices.validateUserExistsInGroup("j", "groupJosh");
		List<String> test = new ArrayList<>();
		test.add("admin");
		test.add("j");
		GroupServices.getAllUsersInGroup("groupJosh");
		GroupServices.removeUserFromGroup("groupJosh", "admin", "j");
	}

	@Test
	public void deleteGroupFailure(){
        GroupServices.createGroup("groupJosh", "admin");
        assertEquals(false,GroupServices.deleteGroup("groupJosh", null));


	}

	@After
	public void cleanUp() {
		GroupServices.deleteGroup("groupJosh","admin");
	}

}
