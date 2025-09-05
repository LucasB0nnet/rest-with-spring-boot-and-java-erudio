package br.com.erudio.data.dto.V1;

import java.io.Serializable;
import java.util.Objects;

public class UploadFileResponseDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String fileName;
	private String fileDownloadUri;
	private String fileType;
	private Long size;
	
	public UploadFileResponseDTO(String fileName, String fileDownloadUri, String fileType, Long size) {
		super();
		this.fileName = fileName;
		this.fileDownloadUri = fileDownloadUri;
		this.fileType = fileType;
		this.size = size;
	}

	public UploadFileResponseDTO() {
		super();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public void setFileDownloadUri(String fileDownloadUri) {
		this.fileDownloadUri = fileDownloadUri;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fileDownloadUri, fileName, fileType, size);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadFileResponseDTO other = (UploadFileResponseDTO) obj;
		return Objects.equals(fileDownloadUri, other.fileDownloadUri) && Objects.equals(fileName, other.fileName)
				&& Objects.equals(fileType, other.fileType) && Objects.equals(size, other.size);
	}

	
}
