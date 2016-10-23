package pl.kapmat.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import pl.kapmat.model.Role;
import pl.kapmat.model.User;

import java.util.List;

/**
 * User DAO interface
 *
 * Created by Kapmat on 2016-10-23.
 */
@Transactional
public interface UserDAO extends CrudRepository<User, Integer> {

	User findByLogin(String login);

	List<User> findByRole(Role role);
}
