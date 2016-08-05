package com.smily.enums;

public enum ROLE {
	ADMIN("ADMIN"), USER("USER");

	private String role;

	ROLE(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "ROLE_" + role;
	}

}