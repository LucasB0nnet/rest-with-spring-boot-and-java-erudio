package br.com.erudio.file.export.contract;

import java.util.List;

import org.springframework.core.io.Resource;

import br.com.erudio.data.dto.V1.PersonDTOV1;

public interface PersonExport {

	Resource exportPeople(List<PersonDTOV1> people) throws Exception;
	
	Resource exportPerson(PersonDTOV1 person) throws Exception;
}
