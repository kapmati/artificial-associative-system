/**
 * Created by Kapmat on 2016-09-24.
 */
package pl.kapmat.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);

	private static SessionFactory sessionFactory;

	private static SessionFactory buildSessionFactory() {
		try {
			Configuration configuration = new Configuration();
			configuration.configure("hibernate.cfg.xml");
			LOGGER.info("Hibernate configuration loaded");

			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties())
					.build();
			LOGGER.info("Hibernate serviceRegistry created");

			return configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable e) {
			LOGGER.error("Initial SessionFactory creation failed. " + e);
			throw new ExceptionInInitializerError(e);
		}
	}

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = buildSessionFactory();
		}
		return sessionFactory;
	}
}
