package pl.kapmat.model;

/**
 * User roles
 *
 * @author Mateusz Kaproń
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
