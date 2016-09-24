package hibernate;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kapmat.model.Role;
import pl.kapmat.model.User;
import pl.kapmat.util.HibernateUtil;

/**
 * Test if it is possible to insert users with the same login to the database.
 *
 * Created by Kapmat on 2016-09-24.
 */

public class HibernateUserExistTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUserExistTest.class);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private Session session;

	private static final String LOGIN = "ser5439";
	private static final String PASSWORD = "pass";
	private static final Role ROLE = Role.ADMIN;

	private static final String SECOND_PASSWORD = "difP";
	private static final Role SECOND_ROLE = Role.USER;

	@Test
	public void test() {
		LOGGER.info("Create users");
		User user = new User();
		user.setLogin(LOGIN);
		user.setPassword(PASSWORD);
		user.setRole(ROLE);
		User secondUser = new User();
		secondUser.setLogin(LOGIN);
		secondUser.setPassword(SECOND_PASSWORD);
		secondUser.setRole(SECOND_ROLE);
		LOGGER.info("Users created");

		LOGGER.info("Get session");
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		LOGGER.info("Start transaction");
		session.beginTransaction();
		LOGGER.info("Save first user");
		session.save(user);

		LOGGER.info("Save second user");
		expectedException.expect(ConstraintViolationException.class);
		session.save(secondUser);
	}

	@After
	public void deleteUser() {
		LOGGER.info("Rollback: delete user");
		session.getTransaction().rollback();
	}
}
