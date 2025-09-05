package br.com.erudio.data.dto.V1.security;

import java.io.Serializable;
import java.util.Objects;

public class AccountCredencialsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userName;
	
	private String password;
	
	private String fullName;
	
	public AccountCredencialsDTO() {
	}
	
	public AccountCredencialsDTO(String userName, String password, String fullName) {
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fullName, password, userName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountCredencialsDTO other = (AccountCredencialsDTO) obj;
		return Objects.equals(fullName, other.fullName) && Objects.equals(password, other.password)
				&& Objects.equals(userName, other.userName);
	}

	
	
}
