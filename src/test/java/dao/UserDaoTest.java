package dao;

import org.junit.*;
import org.junit.runner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.*;
import pl.kapmat.dao.UserDAO;

import pl.kapmat.model.Role;
import pl.kapmat.model.User;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test basic DAO operations on single User.
 *
 * Created by Kapmat on 2016-09-24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserDaoTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoTest.class);

	private static final String LOGIN = "ser2315d";
	private static final String PASSWORD = "pass";
	private static final Role ROLE = Role.ADMIN;

	private static final String SECOND_LOGIN = "bada12ww";
	private static final String SECOND_PASSWORD = "secPass";
	private static final Role SECOND_ROLE = Role.USER;

	private static final String UPDATE_PASSWORD = "updatePas";

	@Autowired
	private UserDAO userDAO;

	@Test
	public void test() {
		LOGGER.info("Create users");
		User user = new User();
		user.setLogin(LOGIN);
		user.setPassword(PASSWORD);
		user.setRole(ROLE);

		User secondUser = new User();
		secondUser.setLogin(SECOND_LOGIN);
		secondUser.setPassword(SECOND_PASSWORD);
		secondUser.setRole(SECOND_ROLE);

		LOGGER.info("Insert users");
		User firstU = userDAO.save(user);
		User secondU = userDAO.save(secondUser);

		LOGGER.info("Get users list");
		List<User> userList = (List<User>) userDAO.findAll();
		boolean firstUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(LOGIN));
		boolean secondUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(SECOND_LOGIN));
		assertTrue(firstUserExistInDb && secondUserExistInDb);

		LOGGER.info("Get first user");
		User firstUserFromDB = userDAO.findOne(firstU.getId());
		assertTrue(firstUserFromDB.getLogin().equals(LOGIN));
		assertTrue(firstUserFromDB.getPassword().equals(PASSWORD));
		assertTrue(firstUserFromDB.getRole().equals(ROLE));

		LOGGER.info("Update second user");
		secondUser.setPassword(UPDATE_PASSWORD);
		userDAO.save(secondUser);

		LOGGER.info("Get second user");
		User secondUserFromDB = userDAO.findOne(secondU.getId());
		assertTrue(secondUserFromDB.getLogin().equals(SECOND_LOGIN));
		assertTrue(secondUserFromDB.getPassword().equals(UPDATE_PASSWORD));

		LOGGER.info("Delete users");
		userDAO.delete(user);
		userDAO.delete(secondUser);

		//Check if users exist in database after using delete methods
		List<User> newUserList = (List<User>) userDAO.findAll();
		firstUserExistInDb = newUserList.stream().anyMatch(u -> u.getLogin().equals(LOGIN));
		secondUserExistInDb = newUserList.stream().anyMatch(u -> u.getLogin().equals(SECOND_LOGIN));
		assertTrue(!firstUserExistInDb && !secondUserExistInDb);

		//Check if users exist in database after using saveList method
		userDAO.save(userList);
		userList = (List<User>) userDAO.findAll();
		firstUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(LOGIN));
		secondUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(SECOND_LOGIN));
		assertTrue(firstUserExistInDb && secondUserExistInDb);

		//Check if users exist in database after using deleteList method
		userDAO.delete(userList);
		userList = (List<User>) userDAO.findAll();
		firstUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(LOGIN));
		secondUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(SECOND_LOGIN));
		assertTrue(!firstUserExistInDb && !secondUserExistInDb);
	}
}
