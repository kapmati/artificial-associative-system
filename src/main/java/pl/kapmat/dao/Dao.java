package pl.kapmat.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kapmat.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for all objects
 *
 * Created by Kapmat on 2016-09-24.
 */
public class Dao {

	private static final Logger LOGGER = LoggerFactory.getLogger(Dao.class);

	private Session session;

	public <T> int save(final T o) {
		int id;
		try {
			LOGGER.info("Insert to database: " + o.toString());
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			id = (int) session.save(o);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Error during insert: " + o.toString());
			session.getTransaction().rollback();
			throw e;
		}
		return id;
	}

	public <T> void saveList(final List<T> objectList) {
		try {
			LOGGER.info("Insert list to database: " + objectList.toString());
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			objectList.forEach(session::save);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Error during insert: " + objectList.toString());
			session.getTransaction().rollback();
			throw e;
		}
	}

	public <T> void update(final T o) {
		try {
			LOGGER.info("Update to database: " + o.toString());
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			session.update(o);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Error during update: " + o.toString());
			session.getTransaction().rollback();
			throw e;
		}
	}

	public void delete(final Object object) {
		try {
			LOGGER.info("Delete from database: " + object.toString());
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			session.delete(object);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Error during delete: " + object.toString());
			session.getTransaction().rollback();
			throw e;
		}
	}

	public <T> T get(final Class<T> type, final int id) {
		T object;
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			object = (T) session.get(type, id);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Error during get object: " + type.toString() + ", id: " + id);
			session.getTransaction().rollback();
			throw e;
		}
		return object;
	}

	public <T> List<T> getALL(final Class<T> type) {
		final List<T> objectList;
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			objectList = session.createCriteria(type).list();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Error during getAll objects: " + type.toString());
			session.getTransaction().rollback();
			throw e;
		}
		return objectList;
	}

	public <T> void deleteList(final List<T> objectList) {
		try {
			LOGGER.info("Delete object list from database: " + objectList.toString());
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			objectList.forEach(session::delete);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			LOGGER.error("Error during deleteList: " + objectList.toString());
			session.getTransaction().rollback();
			throw e;
		}
	}
}
