package br.com.erudio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageConfig {

	private String upload_dir;

	public FileStorageConfig() {
	}

	public String getUpload_dir() {
		return upload_dir;
	}

	public void setUpload_dir(String upload_dir) {
		this.upload_dir = upload_dir;
	}
	
	
}
