package com.varicom.aop.domain;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "user")
public class UserDTO {
	
	private String username;
	private String email;

	public String getUsername() { 
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
