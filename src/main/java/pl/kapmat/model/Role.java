package pl.kapmat.model;

/**
 * User roles
 *
 * Created by Kapmat on 2016-09-21.
 */

public enum Role {
	ADMIN, USER;

	public static Role getRoleByName(String name) {
		switch (name.toUpperCase()) {
			case "ADMIN":
				return Role.ADMIN;
			case "USER":
				return Role.USER;
			default:
				return Role.USER;
		}
	}
}
