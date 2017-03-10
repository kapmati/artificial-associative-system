package pl.kapmat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.kapmat.dao.UserDAO;
import pl.kapmat.model.Role;
import pl.kapmat.model.User;

import java.util.List;

/**
 * User controller
 *
 * @author Mateusz Kaproń
 */
@Controller
@RequestMapping("/user")
public class UserController {

	private UserDAO userDAO;

	@Autowired
	public UserController(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@RequestMapping(value = "/create", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<?> create(String login, String password, Role role) {
		User user = null;
		try {
			user = new User(login, password, role);
			userDAO.save(user);
		} catch (Exception e) {
			return new ResponseEntity<>("Error creating the user: " + e.toString(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>("User successfully created! ID: " + user.getId(), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> updateUser(int id, String login, String password, Role role) {
		try {
			User user = userDAO.findOne(id);
			user.setLogin(login);
			user.setPassword(password);
			user.setRole(role);
			userDAO.save(user);
		} catch (Exception e) {
			return new ResponseEntity<>("Error updating the user: " + e.toString(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>("User successfully updated!", HttpStatus.OK);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<?> delete(@PathVariable int id) {
		try {
			userDAO.delete(id);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<>("Error deleting the user: " + e.toString(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>("User successfully deleted!", HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/login/{login}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getUserByLogin(@PathVariable String login) {
		User user = userDAO.findByLogin(login);
		if (user == null) {
			return new ResponseEntity<>("User '" + login + "' not found", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/role/{role}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
		Role r = Role.getRoleByName(role);
		List<User> users = userDAO.findByRole(r);
		if (users.size() == 0) {
			return new ResponseEntity<>("Users with '" + role + "' role not found", HttpStatus.BAD_REQUEST);
		}
		StringBuffer stringBuffer = new StringBuffer();
		users.forEach(user -> stringBuffer.append(user.toStringWithoutPassword()).append("<br>"));
		return new ResponseEntity<>(stringBuffer, HttpStatus.OK);
	}
}
