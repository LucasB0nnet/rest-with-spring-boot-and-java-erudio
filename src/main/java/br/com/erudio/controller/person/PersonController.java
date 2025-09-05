package br.com.erudio.controller.person;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.erudio.controller.docs.PersonControllerDocs;
import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.file.export.mediatypes.MediaTypes;
import br.com.erudio.service.PersonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/person/v1")
@Tag(name = "People", description = "EndPoints for managing people")
public class PersonController implements PersonControllerDocs {

	@Autowired
	private PersonService service;

	@Override
	@GetMapping(value = "/export/{id}", produces = {MediaTypes.APPLICATION_PDF_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public ResponseEntity<Resource> export(@PathVariable("id") Long id, HttpServletRequest request) {
		
		String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
		
		Resource file = service.exportingPerson(id, acceptHeader);
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(acceptHeader))
				.header(HttpHeaders.CONTENT_DISPOSITION, 
						"attachment; filename=person.pdf")
				.body(file);
	}
	
	@Override
	@GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE })
	public PersonDTOV1 findById(@PathVariable("id") Long id) {
		var person = service.findById(id);
		person.setBirthDay(new Date());
		return person;
	}

	@Override
	@GetMapping(value = "/all", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
	public ResponseEntity<PagedModel<EntityModel<PersonDTOV1>>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
													 @RequestParam(value = "size", defaultValue = "12") Integer size,
													 @RequestParam(value = "direction", defaultValue = "asc") String direction){
		
		var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC: Direction.ASC; 
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
		return ResponseEntity.ok(service.findAll(pageable));	
	}
	
	@Override
	@GetMapping(value = "/findPersonByName/{firstName}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
	public ResponseEntity<PagedModel<EntityModel<PersonDTOV1>>> findPersonByName(@PathVariable("firstName") String firstName, 
													@RequestParam(value = "page", defaultValue = "0") Integer page, 
													@RequestParam(value = "size", defaultValue = "12") Integer size, 
													@RequestParam(value = "direction", defaultValue = "asc") String direction) {
		
		var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Direction.ASC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
		return ResponseEntity.ok(service.findPersonByName(firstName, pageable));
	}
	
	@Override
	@GetMapping(value = "/exportPage", produces = {MediaTypes.APPLICATION_XLSX_VALUE, MediaTypes.APPLICATION_CSV_VALUE, MediaTypes.APPLICATION_PDF_VALUE})
	public ResponseEntity<Resource> exportPage(@RequestParam(value = "page", defaultValue = "0") Integer page,
			 								   @RequestParam(value = "size", defaultValue = "12") Integer size,
			 								   @RequestParam(value = "direction", defaultValue = "asc") String direction,
			 								   HttpServletRequest request) {
		
		var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC: Direction.ASC; 
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
		
		String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
		
		Resource file = service.exportPage(pageable, acceptHeader);
		
		Map<String, String> extensionMap = Map.of(
				MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx",
				MediaTypes.APPLICATION_CSV_VALUE, ".csv",
				MediaTypes.APPLICATION_PDF_VALUE, ".pdf");
		
		
		var contentType = acceptHeader != null ? acceptHeader : "application/octet=stream";
		var fileExtention = extensionMap.getOrDefault(acceptHeader, "");
		var fileName = "people_exported" + fileExtention;
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, 
						"attachment; filename=\"" + fileName + "\"")
				.body(file);
	}
	
	@Override
	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_YAML_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_YAML_VALUE})
	public PersonDTOV1 create(@RequestBody PersonDTOV1 person) {
		return service.create(person);
	}

	@Override
	@PostMapping(value = "/importerAll", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonDTOV1> importerAll(@RequestParam("file") MultipartFile file) {
		return service.massCreation(file);
	}
	
	@Override
	@PutMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE })
	public PersonDTOV1 update(@RequestBody PersonDTOV1 person) {
		return service.update(person);
	}

	@Override
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@Override
	@PatchMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE })
	public PersonDTOV1 disablePerson(@PathVariable("id") Long id) {
		return service.disablePerson(id);

	}
	
}
