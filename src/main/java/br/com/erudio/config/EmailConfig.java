package br.com.erudio.config;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {

	 private String host;
	 private Integer port;
	 private String userName;
	 private String password;
	 private String from;
	 private boolean ssl;
	 
	 public EmailConfig() {
		super();
	 }

	 public String getHost() {
		 return host;
	 }

	 public void setHost(String host) {
		 this.host = host;
	 }

	 public Integer getPort() {
		 return port;
	 }

	 public void setPort(Integer port) {
		 this.port = port;
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

	 public String getFrom() {
		 return from;
	 }

	 public void setFrom(String from) {
		 this.from = from;
	 }

	 public boolean isSsl() {
		 return ssl;
	 }

	 public void setSsl(boolean ssl) {
		 this.ssl = ssl;
	 }

	 @Override
	 public int hashCode() {
		return Objects.hash(from, host, password, port, ssl, userName);
	 }

	 @Override
	 public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailConfig other = (EmailConfig) obj;
		return Objects.equals(from, other.from) && Objects.equals(host, other.host)
				&& Objects.equals(password, other.password) && Objects.equals(port, other.port) && ssl == other.ssl
				&& Objects.equals(userName, other.userName);
	 }
	 
	 
}
