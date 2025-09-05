package br.com.erudio.file.importer.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.file.importer.contract.FileImporter;

@Component
public class CsvImporter implements FileImporter{

	@Override
	public List<PersonDTOV1> importFile(InputStream inputStream) throws Exception {
	    CSVFormat format = CSVFormat.Builder.create()
	            .setHeader()                      // Indica que a primeira linha do CSV é o cabeçalho (nomes das colunas)
	            .setSkipHeaderRecord(true)       // Pula a primeira linha (o cabeçalho) ao ler os dados
	            .setIgnoreEmptyLines(true)       // Ignora linhas vazias
	            .setTrim(true)                   // Remove espaços em branco extras dos campos
	            .build();                        // Constrói o objeto CSVFormat

		Iterable<CSVRecord> records =  format.parse(new InputStreamReader(inputStream)); // Iterable é usado pois não carrega tudo em memória então é mais perfo me arquivos grandes do que o List
		return parseRecordsToPersonDTOs(records);
	}

	private List<PersonDTOV1> parseRecordsToPersonDTOs(Iterable<CSVRecord> records) {
		List<PersonDTOV1> people = new ArrayList<PersonDTOV1>();
		for(CSVRecord record : records) {
			PersonDTOV1 personDTOV1 = new PersonDTOV1();
			personDTOV1.setFirstName(record.get("first_name"));
			personDTOV1.setLastName(record.get("last_name"));
			personDTOV1.setAddress(record.get("address"));
			personDTOV1.setGender(record.get("gender"));
			personDTOV1.setEnabled(true);
			people.add(personDTOV1);
		}
		return people;
	}

}
