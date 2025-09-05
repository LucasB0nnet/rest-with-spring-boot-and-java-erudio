package br.com.erudio.controller.docs;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import br.com.erudio.data.dto.V1.UploadFileResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "File EndPoint")
public interface FileControllerDocs {

	UploadFileResponseDTO uploadFile (MultipartFile file);
	
	List<UploadFileResponseDTO> uploadMultipleFile (MultipartFile[] files);
	
	ResponseEntity<Resource> downloadFile(String fileName,HttpServletRequest request);
}
