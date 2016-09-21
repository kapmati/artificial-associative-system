/**
 * Created by Kapmat on 2016-09-21.
 */
package pl.kapmat.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private int id;

	@Column(name = "login", nullable = false, unique = true)
	private String login;

	@Column(name = "password")
	private String password;

	@Column(name = "role")
	private Role role;

	public User() {

	}

	public User(String login, String password, Role role) {
		this.login = login;
		this.password = password;
		this.role = role;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", login='" + login + '\'' +
				", password='" + password + '\'' +
				", role=" + role +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		User user = (User) o;

		if (id != user.id) {
			return false;
		}
		if (!login.equals(user.login)) {
			return false;
		}
		if (!password.equals(user.password)) {
			return false;
		}
		return role == user.role;

	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + login.hashCode();
		result = 31 * result + password.hashCode();
		result = 31 * result + role.hashCode();
		return result;
	}
}
