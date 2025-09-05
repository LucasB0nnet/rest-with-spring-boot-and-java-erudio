package br.com.erudio.unitetestes.mapper.mocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.erudio.data.dto.V1.BookDTO;
import br.com.erudio.model.Book;

public class MockBook {

	public Book mockEntity() {
		return mockEntity(0);
	}

	public BookDTO mockDTO() {
		return mockDTO(0);
	}

	public List<Book> mockEntityList() {
		List<Book> book = new ArrayList<Book>();
		for (int i = 0; i < 14; i++) {
			book.add(mockEntity(i));
		}
		return book;
	}

	public List<BookDTO> mockDTOList() {
		List<BookDTO> bookDTO = new ArrayList<>();
		for (int i = 0; i < 14; i++) {
			bookDTO.add(mockDTO(i));
		}
		return bookDTO;
	}

	public Book mockEntity(Integer number) {
		Book book = new Book();
		book.setId(number.longValue());
		book.setAuthor("Name" + number);
		book.setLaunchDate(new Date(number));
		book.setPrice(250.00);
		book.setTitle("Title" + number);
		return book;
	}

	public BookDTO mockDTO(Integer number) {
		BookDTO bookDto = new BookDTO();
		bookDto.setId(number.longValue());
		bookDto.setAuthor("Name" + number);
		bookDto.setLaunchDate(new Date());
		bookDto.setPrice(250.00);
		bookDto.setTitle("Title" + number);
		return bookDto;
	}

}
