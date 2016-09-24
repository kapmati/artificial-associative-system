import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kapmat.model.Role;
import pl.kapmat.model.User;
import pl.kapmat.util.HibernateUtil;

/**
 * Created by Kapmat on 2016-09-24.
 */

public class HibernateUserTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUserTest.class);

	private static final String LOGIN = "ser23123";
	private static final String PASSWORD = "pass";
	private static final Role ROLE = Role.ADMIN;

	@Test
	public void test() {
		LOGGER.info("Create user");
		User user = new User();
		user.setLogin(LOGIN);
		user.setPassword(PASSWORD);
		user.setRole(ROLE);
		LOGGER.info("User created");

		LOGGER.info("Get session");
		Session insertSession = HibernateUtil.getSessionFactory().getCurrentSession();
		LOGGER.info("Start transaction");
		insertSession.beginTransaction();
		LOGGER.info("Save user");
		insertSession.save(user);
		LOGGER.info("Commit");
		insertSession.getTransaction().commit();
		System.out.println(user.toString());

		LOGGER.info("Delete user");
		Session deleteSession = HibernateUtil.getSessionFactory().getCurrentSession();
		deleteSession.beginTransaction();
		deleteSession.delete(user);
		deleteSession.getTransaction().commit();

		LOGGER.info("Terminate session");
		HibernateUtil.getSessionFactory().close();
	}
}
