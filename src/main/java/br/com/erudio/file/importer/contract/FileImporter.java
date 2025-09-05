package br.com.erudio.file.importer.contract;

import java.io.InputStream;
import java.util.List;

import br.com.erudio.data.dto.V1.PersonDTOV1;

public interface FileImporter {

	List<PersonDTOV1> importFile(InputStream inputStream) throws Exception;
}
