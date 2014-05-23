package com.varicom.aop.domain;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeName;
@XmlRootElement(name = "userGetResponse")
@JsonTypeName( value = "userGetResponse" )
public class UserResponseGet {
	UserDTO user;
	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}
	

}
