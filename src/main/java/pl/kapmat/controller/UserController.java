package pl.kapmat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.kapmat.dao.UserDAO;
import pl.kapmat.model.Role;
import pl.kapmat.model.User;

import java.util.List;

/**
 * User controller
 *
 * Created by Kapmat on 2016-10-23.
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserDAO userDAO;

	@RequestMapping("/create")
	@ResponseBody
	public String create(String login, String password, Role role) {
		User user = null;
		try {
			user = new User(login, password, role);
			userDAO.save(user);
		} catch (Exception e) {
			return "Error creating the user: " + e.toString();
		}
		return "User successfully created! ID: " + user.getId();
	}

	@RequestMapping("/update")
	@ResponseBody
	public String updateUser(int id, String login, String password, Role role) {
		try {
			User user = userDAO.findOne(id);
			user.setLogin(login);
			user.setPassword(password);
			user.setRole(role);
			userDAO.save(user);
		} catch (Exception e) {
			return "Error updating the user: " + e.toString();
		}
		return "User successfully updated!";
	}

	@RequestMapping("/delete")
	@ResponseBody
	public String delete(int id) {
		try {
			userDAO.delete(id);
		} catch (EmptyResultDataAccessException e) {
			return "Error deleting the user: " + e.toString();
		}
		return "User successfully deleted!";
	}

	@RequestMapping("/getUserByLogin")
	@ResponseBody
	public String getUserByLogin(String login) {
		User user = userDAO.findByLogin(login);
		if (user == null) {
			return "User '" + login + "' not found";
		}
		return user.toStringWithoutPassword();
	}

	@RequestMapping("/getUsersByRole")
	@ResponseBody
	public String getUsersByRole(String role) {
		Role r = null;
		switch (role.toUpperCase()) {
			case "ADMIN":
				r = Role.ADMIN;
				break;
			case "USER":
				r = Role.USER;
				break;
		}

		List<User> users = userDAO.findByRole(r);
		if (users.size() == 0) {
			return "Users with '" + role + "' role not found";
		}
		StringBuffer stringBuffer = new StringBuffer();
		users.forEach(user -> stringBuffer.append(user.toStringWithoutPassword()).append("<br>"));
		return stringBuffer.toString();
	}
}
