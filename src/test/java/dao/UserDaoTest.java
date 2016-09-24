package dao;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kapmat.dao.Dao;
import pl.kapmat.model.Role;
import pl.kapmat.model.User;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test basic DAO operations on single User.
 *
 * Created by Kapmat on 2016-09-24.
 */
public class UserDaoTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoTest.class);

	private static final String LOGIN = "ser2315d";
	private static final String PASSWORD = "pass";
	private static final Role ROLE = Role.ADMIN;

	private static final String SECOND_LOGIN = "bada12ww";
	private static final String SECOND_PASSWORD = "secPass";
	private static final Role SECOND_ROLE = Role.USER;

	private static final String UPDATE_PASSWORD = "updatePas";

	private Dao dao = new Dao();

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
		int firstId = dao.save(user);
		int secondId = dao.save(secondUser);

		LOGGER.info("Get users list");
		List<User> userList = dao.getALL(User.class);
		boolean firstUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(LOGIN));
		boolean secondUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(SECOND_LOGIN));
		assertTrue(firstUserExistInDb && secondUserExistInDb);

		LOGGER.info("Get first user");
		User firstUserFromDB = dao.get(User.class, firstId);
		assertTrue(firstUserFromDB.getLogin().equals(LOGIN));
		assertTrue(firstUserFromDB.getPassword().equals(PASSWORD));
		assertTrue(firstUserFromDB.getRole().equals(ROLE));

		LOGGER.info("Update second user");
		secondUser.setPassword(UPDATE_PASSWORD);
		dao.update(secondUser);

		LOGGER.info("Get second user");
		User secondUserFromDB = dao.get(User.class, secondId);
		assertTrue(secondUserFromDB.getLogin().equals(SECOND_LOGIN));
		assertTrue(secondUserFromDB.getPassword().equals(UPDATE_PASSWORD));

		LOGGER.info("Delete users");
		dao.delete(user);
		dao.delete(secondUser);

		//Check if users exist in database after using delete methods
		List<User> newUserList = dao.getALL(User.class);
		firstUserExistInDb = newUserList.stream().anyMatch(u -> u.getLogin().equals(LOGIN));
		secondUserExistInDb = newUserList.stream().anyMatch(u -> u.getLogin().equals(SECOND_LOGIN));
		assertTrue(!firstUserExistInDb && !secondUserExistInDb);

		//Check if users exist in database after using saveList method
		dao.saveList(userList);
		userList = dao.getALL(User.class);
		firstUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(LOGIN));
		secondUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(SECOND_LOGIN));
		assertTrue(firstUserExistInDb && secondUserExistInDb);

		//Check if users exist in database after using deleteList method
		dao.deleteList(userList);
		userList = dao.getALL(User.class);
		firstUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(LOGIN));
		secondUserExistInDb = userList.stream().anyMatch(u -> u.getLogin().equals(SECOND_LOGIN));
		assertTrue(!firstUserExistInDb && !secondUserExistInDb);
	}
}
