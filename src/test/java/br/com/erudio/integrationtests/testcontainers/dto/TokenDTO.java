package br.com.erudio.integrationtests.testcontainers.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class TokenDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userName;

	private Boolean authenticated;

	private Date createdToken;

	private Date experationToken;

	private String accessToken;

	private String refreshToken;

	public TokenDTO() {
	}

	public TokenDTO(String userName, Boolean authenticated, Date createdToken, Date experationToken, String accessToken,
			String refreshToken) {
		super();
		this.userName = userName;
		this.authenticated = authenticated;
		this.createdToken = createdToken;
		this.experationToken = experationToken;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(Boolean authenticated) {
		this.authenticated = authenticated;
	}

	public Date getCreatedToken() {
		return createdToken;
	}

	public void setCreatedToken(Date createdToken) {
		this.createdToken = createdToken;
	}

	public Date getExperationToken() {
		return experationToken;
	}

	public void setExperationToken(Date experationToken) {
		this.experationToken = experationToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accessToken, authenticated, createdToken, experationToken, refreshToken, userName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TokenDTO other = (TokenDTO) obj;
		return Objects.equals(accessToken, other.accessToken) && authenticated == other.authenticated
				&& Objects.equals(createdToken, other.createdToken)
				&& Objects.equals(experationToken, other.experationToken)
				&& Objects.equals(refreshToken, other.refreshToken) && Objects.equals(userName, other.userName);
	}

}
