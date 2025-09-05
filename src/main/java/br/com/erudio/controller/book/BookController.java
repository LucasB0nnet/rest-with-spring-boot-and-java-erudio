package br.com.erudio.controller.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.erudio.controller.docs.BookControllerDocs;
import br.com.erudio.data.dto.V1.BookDTO;
import br.com.erudio.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/book/v1")
@Tag(name = "Book", description = "EndPoints for managing books")
public class BookController implements BookControllerDocs {

	@Autowired
	private BookService bookService;

	@Override
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public BookDTO findById(@PathVariable("id") Long id) {
		BookDTO dto = bookService.findById(id);
		return dto;
	}

	@Override
	@GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "12") Integer size,
			@RequestParam(value = "direction", defaultValue = "asc") String direction,
			@RequestParam(value = "filter", defaultValue = "author") String filter) {

		var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Direction.ASC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, filter.toLowerCase()));
		return ResponseEntity.ok(bookService.findAll(pageable, filter));
	}

	@Override
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public BookDTO create(@RequestBody BookDTO dto) {
		return bookService.create(dto);
	}

	@Override
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public BookDTO update(@RequestBody BookDTO bookDTO) {
		return bookService.update(bookDTO);
	}

	@Override
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		bookService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
