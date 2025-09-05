package br.com.erudio.integrationtests.testcontainers.dto.wrapper;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WrapperBookDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("_embedded")
	private BookEmbeddedDTO embeddedDTO;

	public WrapperBookDTO() {
	}

	public BookEmbeddedDTO getEmbeddedDTO() {
		return embeddedDTO;
	}

	public void setEmbeddedDTO(BookEmbeddedDTO embeddedDTO) {
		this.embeddedDTO = embeddedDTO;
	}

}
