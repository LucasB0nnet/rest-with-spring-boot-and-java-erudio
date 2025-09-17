package br.com.erudio.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.erudio.controller.person.PersonController;
import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.data.dto.V2.PersonDTOV2;
import br.com.erudio.exception.BadRequestException;
import br.com.erudio.exception.FileStorageException;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.exception.ResourceNotFoundException;
import br.com.erudio.file.export.contract.PersonExport;
import br.com.erudio.file.export.factory.FileExportFactory;
import br.com.erudio.file.importer.contract.FileImporter;
import br.com.erudio.file.importer.factory.FileImporterFactory;
import br.com.erudio.mapper.ObjectMapper;
import br.com.erudio.mapper.custom.PersonMapper;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;

@Service
public class PersonService {

	private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private PersonMapper mapper;

	@Autowired
	private PagedResourcesAssembler<PersonDTOV1> assembler;

	@Autowired
	private FileImporterFactory fileImporter;

	@Autowired
	private FileExportFactory fileExport;

	public Resource exportingPerson(Long id, String acceptHeader) {
		logger.info("Exporting data of  one person!");

		var entity = personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var dto = ObjectMapper.parseObject(entity, PersonDTOV1.class);
		try {
			PersonExport export = this.fileExport.getExport(acceptHeader);
			return export.exportPerson(dto);
		} catch (Exception e) {
			logger.error("Erro ao exportar pessoa para " + acceptHeader, e);
			throw new FileStorageException("Error during file export!", e);
		}
	}

	public PersonDTOV1 findById(Long id) {
		logger.info("finding one person!");

		var entity = personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var dto = ObjectMapper.parseObject(entity, PersonDTOV1.class);
		addHateoasLinks(dto);
		return dto;
	}

	public PagedModel<EntityModel<PersonDTOV1>> findAll(Pageable pageable) {
		logger.info("finding all people!");
		var people = personRepository.findAll(pageable);
		return buildPagedModel(pageable, people);

	}

	public PagedModel<EntityModel<PersonDTOV1>> findPersonByName(String firstName, Pageable pageable) {
		logger.info("finding people by First Name!");
		var people = personRepository.findPersonByName(firstName, pageable);
		return buildPagedModel(pageable, people);

	}

	public Resource exportPage(Pageable pageable, String acceptHeader) {
		logger.info("Exporting a people page!");

		var people = personRepository.findAll(pageable)
				.map(person -> ObjectMapper.parseObject(person, PersonDTOV1.class)).getContent();

		try {
			PersonExport export = this.fileExport.getExport(acceptHeader);
			return export.exportPeople(people);
		} catch (Exception e) {
			throw new FileStorageException("Error during file export!", e.getCause());
		}
	}

	public PersonDTOV1 create(PersonDTOV1 person) {
		if (Objects.isNull(person))
			throw new RequiredObjectIsNullException();
		logger.info("Creating person!");
		var entity = ObjectMapper.parseObject(person, Person.class);
		var dto = ObjectMapper.parseObject(personRepository.save(entity), PersonDTOV1.class);
		addHateoasLinks(dto);
		return dto;
	}

	public PersonDTOV2 createV2(PersonDTOV2 person) {
		logger.info("Creating person V2!");
		var entity = mapper.convertDTOToEntity(person);
		return mapper.convertEntityToDTO(personRepository.save(entity));
	}

	public List<PersonDTOV1> massCreation(MultipartFile multipartFile) {// arquivo enviado via upload em uma requisição
																		// HTTP (como num formulário ou chamada de API).
		logger.info("Importing people from file!");
		if (multipartFile.isEmpty())
			throw new BadRequestException("Please set a valid file!");// valida se o arquivo é vazio ou nulo

		try (InputStream inputStream = multipartFile.getInputStream()) {// Faz a abertura do arquivo e a leitura dos
																		// dados
			String fileName = Optional.ofNullable(multipartFile.getOriginalFilename())// le o nome do arquivo e o
																						// guardcaso nullo lança exceção
					.orElseThrow(() -> new BadRequestException("File name cannot be null!"));

			FileImporter importer = this.fileImporter.getImporter(fileName);// Faz a criação através da
																			// FileImporteFactory apartir do nome

			List<Person> entity = importer.importFile(inputStream).stream()// chega uma lista de dtos então percorre a
																			// lista
					.map(dto -> personRepository.save(ObjectMapper.parseObject(dto, Person.class)))// Faz um map e salva
																									// no DB modificando
																									// cada dto para
																									// entidade e
																									// retorna a lista
																									// com entidades
					.toList();

			return entity.stream()// Por Fim percorre a lista de entidades
					.map(person -> {
						var personDTO = ObjectMapper.parseObject(person, PersonDTOV1.class);// transforma todos em dtos
						addHateoasLinks(personDTO);// adiciona o link em cada dto
						return personDTO;// retorna a lista de PersonDto
					}).toList();
		} catch (Exception e) {
			throw new FileStorageException(e.getMessage());
		}

	}

	public PersonDTOV1 update(PersonDTOV1 person) {
		if (Objects.isNull(person))
			throw new RequiredObjectIsNullException();
		logger.info("Updating person!");
		var entity = ObjectMapper.parseObject(person, Person.class);
		Person personUpdate = personRepository.findById(entity.getId())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		personUpdate.setFirstName(entity.getFirstName());
		personUpdate.setLastName(entity.getLastName());
		personUpdate.setAddress(entity.getAddress());
		personUpdate.setGender(entity.getGender());

		return ObjectMapper.parseObject(personRepository.save(personUpdate), PersonDTOV1.class);
	}

	@Transactional
	public PersonDTOV1 disablePerson(Long id) {
		logger.info("Disable one person for id!");
		personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		personRepository.disablePerson(id);
		var entity = personRepository.findById(id).get();
		var dto = ObjectMapper.parseObject(entity, PersonDTOV1.class);
		addHateoasLinks(dto);
		return dto;
	}

	public void delete(Long id) {
		logger.info("Deleting one person for id!");
		Person personDelete = personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		personRepository.delete(personDelete);
	}

	private PagedModel<EntityModel<PersonDTOV1>> buildPagedModel(Pageable pageable, Page<Person> people) {
		var peopleWithLinks = people.map(person -> {
			var dto = ObjectMapper.parseObject(person, PersonDTOV1.class);
			addHateoasLinks(dto);
			return dto;
		});

		Link findAllLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class)
				.findAll(pageable.getPageNumber(), pageable.getPageSize(), String.valueOf(pageable.getSort())))
				.withSelfRel();
		return assembler.toModel(peopleWithLinks, findAllLink);
	}

	private void addHateoasLinks(PersonDTOV1 dto) {
		dto.add(linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findById(dto.getId())).withSelfRel()
				.withType("GET"));

		dto.add(linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findPersonByName("", 1, 12, "asc"))
				.withRel("findPersonByName").withType("GET"));

		dto.add(linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).delete(dto.getId())).withRel("delete")
				.withType("DELETE"));

		dto.add(linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findAll")
				.withType("GET"));

		dto.add(linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).create(dto)).withRel("create")
				.withType("POST"));

		dto.add(linkTo(WebMvcLinkBuilder.methodOn(PersonController.class)).slash("massCreation").withRel("massCreation")
				.withType("POST"));

		dto.add(linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).update(dto)).withRel("update")
				.withType("PUT"));

		dto.add(linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).disablePerson(dto.getId()))
				.withRel("disablePerson").withType("PATCH"));

		dto.add(linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).exportPage(1, 12, "asc", null))
				.withRel("exportPage").withType("GET").withTitle("Export People"));
	}

}
