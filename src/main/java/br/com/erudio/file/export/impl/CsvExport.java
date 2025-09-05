package br.com.erudio.file.export.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.file.export.contract.PersonExport;

@Component
public class CsvExport implements PersonExport {

	@Override
	public Resource exportPeople(List<PersonDTOV1> people) throws Exception {
		// Cria um fluxo de saída em memória (sem gravar diretamente em arquivo no disco).
	    // Ele vai armazenar os bytes que forem escritos.
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		// Cria um escritor que converte caracteres (Strings) para bytes,
	    // usando o padrão de codificação UTF-8.
	    // Assim, tudo que for escrito será guardado no outputStream em formato de texto UTF-8.
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
		
		// Configura o formato do CSV usando a biblioteca Apache Commons CSV.
	    // Aqui está sendo definido:
	    // - O cabeçalho das colunas: "ID", "First Name", "Last Name", "Address", "Gender", "Enabled"
	    // - Que o cabeçalho NÃO deve ser ignorado (ou seja, vai aparecer na primeira linha do arquivo).
		CSVFormat csvFormat = CSVFormat.Builder.create()
				.setHeader("ID", "First Name", "Last Name", "Address", "Gender", "Enabled")
				.setSkipHeaderRecord(false)// garante que o cabeçalho seja escrito no arquivo
				.build();
		
		try(CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)){
			for(PersonDTOV1 person : people) {
				// Escreve uma linha no CSV para cada objeto PersonDTOV1.
		        // Cada campo do objeto vira uma coluna no arquivo CSV.
				csvPrinter.printRecord(
						person.getId(),
						person.getFirstName(),
						person.getLastName(),
						person.getAddress(),
						person.getGender(),
						person.getEnabled());
			}
		}
		
		return new ByteArrayResource(outputStream.toByteArray());
	}

	@Override
	public Resource exportPerson(PersonDTOV1 person) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
