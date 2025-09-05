package br.com.erudio.integrationtests.testcontainers.dto.wrapper;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.erudio.integrationtests.testcontainers.dto.BookDTO;

public class BookEmbeddedDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	@JsonProperty("book")
	private List<BookDTO> books;

	public BookEmbeddedDTO() {
		super();
	}

	public List<BookDTO> getBooks() {
		return books;
	}

	public void setBooks(List<BookDTO> books) {
		this.books = books;
	}
	
	
}
