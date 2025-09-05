/**
 * 
 */
package br.com.erudio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import br.com.erudio.data.dto.V1.BookDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.mapper.ObjectMapper;
import br.com.erudio.model.Book;
import br.com.erudio.repository.BookRepository;
import br.com.erudio.unitetestes.mapper.mocks.MockBook;

/**
 * 
 */
@TestInstance(value = Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	MockBook input;

	@InjectMocks
	private BookService service;

	@Mock
	BookRepository bookRepository;
	
	@Mock
    private PagedResourcesAssembler<BookDTO> assembler;

	@BeforeEach
	void setUp() {
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreatedWithNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});

		String expetedeMessage = "It is not  allowed to persist a null object!";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expetedeMessage));
	}

	@Test
	void testUpdateWithNullPerson() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});

		String expetedeMessage = "It is not  allowed to persist a null object!";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expetedeMessage));
	}

	@Test
	void update() {
		Book book = input.mockEntity(1);
		Book persisted = book;
		persisted.setId(1L);

		BookDTO dto = input.mockDTO(1);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(bookRepository.save(book)).thenReturn(persisted);

		var result = service.update(dto);

		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/book/v1/1") && link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("POST")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("PUT")));


		assertEquals("Name1", result.getAuthor());
		assertNotNull(result.getLaunchDate());
		assertEquals(250, result.getPrice());
		assertEquals("Title1", result.getTitle());
	}

	@Test
	void findById() {
		Book book = input.mockEntity(1);
		book.setId(1L);
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
	
		var result = service.findById(1L);
	
		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());
	
		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/book/v1/1") && link.getType().equals("GET")));
	
		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("GET")));
	
		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("POST")));
	
		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("PUT")));
	

	
		assertEquals("Name1", result.getAuthor());
		assertNotNull(result.getLaunchDate());
		assertEquals(250, result.getPrice());
		assertEquals("Title1", result.getTitle());
	}

	@Test
	void create() {
		Book book = input.mockEntity(1);
	
		book.setId(1L);
		BookDTO dto = input.mockDTO(1);
		doReturn(book).when(bookRepository).save(Mockito.any(Book.class));

		var result = service.create(dto);

		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/book/v1/1") && link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("POST")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("PUT")));



		assertEquals("Name1", result.getAuthor());
		assertNotNull(result.getLaunchDate());
		assertEquals(250, result.getPrice());
		assertEquals("Title1", result.getTitle());
	}

	@Test
	void delete() {
		Book book = input.mockEntity(1);
		book.setId(1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		service.delete(1L);

		verify(bookRepository, times(1)).findById(anyLong());
		verify(bookRepository, times(1)).delete(any(Book.class));
		verifyNoMoreInteractions(bookRepository);
	}

	@Test
	void findAll() {

	    
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "firstName"));
        List<Book> list = input.mockEntityList();
        Page<Book> page = new PageImpl<>(list, pageable, list.size());

   
        Page<BookDTO> dtoPage = page.map(book -> ObjectMapper.parseObject(book, BookDTO.class));

        when(bookRepository.findAll(pageable)).thenReturn(page);

        
        PagedModel<EntityModel<BookDTO>> pagedModel = PagedModel.of(
                dtoPage.stream().map(EntityModel::of).toList(),
                new PagedModel.PageMetadata(dtoPage.getSize(), dtoPage.getNumber(), dtoPage.getTotalElements())
        );

        when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(pagedModel);

        // Act
        PagedModel<EntityModel<BookDTO>> result = service.findAll(pageable, null);

        // Assert
        assertNotNull(result);
        assertEquals(14, result.getContent().size());

        EntityModel<BookDTO> entityModel = result.getContent().stream().toList().get(1);
        BookDTO bookOne = entityModel.getContent();
        
        
        
        assertNotNull(bookOne);
		assertNotNull(bookOne.getId());
		assertNotNull(bookOne.getLinks());

		assertNotNull(bookOne.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/book/v1/2") && link.getType().equals("GET")));

		assertNotNull(bookOne.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("GET")));

		assertNotNull(bookOne.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("POST")));

		assertNotNull(bookOne.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("PUT")));

        assertNotNull(bookOne);
        assertEquals("Name1", bookOne.getAuthor());
        assertEquals("Title1", bookOne.getTitle());
        assertEquals(250.00, bookOne.getPrice());
 

		
	 	EntityModel<BookDTO> entityModel2 = result.getContent().stream().toList().get(2);
	 	BookDTO bookTWo = entityModel2.getContent();
		
		assertNotNull(bookTWo);
		assertNotNull(bookTWo.getId());
		assertNotNull(bookTWo.getLinks());

		assertNotNull(bookTWo.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/book/v1/2") && link.getType().equals("GET")));

		assertNotNull(bookTWo.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("GET")));

		assertNotNull(bookTWo.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("POST")));

		assertNotNull(bookTWo.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/book/v1") && link.getType().equals("PUT")));


		assertEquals("Name2", bookTWo.getAuthor());
		assertNotNull(bookTWo.getLaunchDate());
		assertEquals(250, bookTWo.getPrice());
		assertEquals("Title2", bookTWo.getTitle());
		
	}

}
