package br.com.erudio.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import br.com.erudio.controller.book.BookController;
import br.com.erudio.data.dto.V1.BookDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.exception.ResourceNotFoundException;
import br.com.erudio.mapper.ObjectMapper;
import br.com.erudio.model.Book;
import br.com.erudio.repository.BookRepository;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private PagedResourcesAssembler<BookDTO> assembler;

	Logger log = LoggerFactory.getLogger(BookService.class);

	public BookDTO findById(Long id) {
		log.info("Finding Book by your ID!");
		Book bookEntity = bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book not foud by this ID: " + id));
		var dto = ObjectMapper.parseObject(bookEntity, BookDTO.class);
		addHateoasLinks(dto);
		return dto;
		
	}

	public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable, String filter) {
		log.info("Finding all books!");
		var entity = bookRepository.findAll(pageable);
		var bookWithLinks = entity.map(book -> {
			var dto = ObjectMapper.parseObject(book, BookDTO.class);
			addHateoasLinks(dto);
			return dto;
		});
			
		Link findAllLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BookController.class)
				.findAll(pageable.getPageNumber(),
				pageable.getPageSize(),
				String.valueOf(pageable.getSort()),
				String.valueOf(filter))).withSelfRel();
		return assembler.toModel(bookWithLinks, findAllLink);
	}

	public BookDTO create(BookDTO bookDTO) {
		if (Objects.isNull(bookDTO))
			throw new RequiredObjectIsNullException();
		log.info("Creating Book!");
		Book bookEntity = ObjectMapper.parseObject(bookDTO, Book.class);
		BookDTO bookDto = ObjectMapper.parseObject(bookRepository.save(bookEntity), BookDTO.class);
		addHateoasLinks(bookDto);
		return bookDto;
	}

	public BookDTO update(BookDTO bookDTO) {
		if (Objects.isNull(bookDTO))
			throw new RequiredObjectIsNullException();
		log.info("Updating this Book!");
		Book bookEntity = ObjectMapper.parseObject(bookDTO, Book.class);
		Book bookUpdate = bookRepository.findById(bookEntity.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Book not found by this ID: " + bookEntity.getId()));
		bookUpdate.setAuthor(bookEntity.getAuthor());
		bookUpdate.setLaunchDate(bookEntity.getLaunchDate());
		bookUpdate.setPrice(bookEntity.getPrice());
		bookUpdate.setTitle(bookEntity.getTitle());

		var dto = ObjectMapper.parseObject(bookRepository.save(bookUpdate), BookDTO.class);
		addHateoasLinks(dto);
		return dto;
	}
	
	public void delete(Long id) {
		log.info("Deleting Book!");
		Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found by this ID: " + id));
		bookRepository.delete(book);
	}

	private void addHateoasLinks(BookDTO dto) {
		dto.add(linkTo(WebMvcLinkBuilder.methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
		
		dto.add(linkTo(WebMvcLinkBuilder.methodOn(BookController.class).findAll(1, 12, "asc", "author")).withRel("findAll").withType("GET"));
		
		dto.add(linkTo(WebMvcLinkBuilder.methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
		
		dto.add(linkTo(WebMvcLinkBuilder.methodOn(BookController.class).update(dto)).withRel("update").withType("PUT"));
	}

}
