package br.com.erudio.file.importer.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.file.importer.contract.FileImporter;

@Component
public class XlsxImporter implements FileImporter{

	@Override
	public List<PersonDTOV1> importFile(InputStream inputStream) throws Exception {
		
		try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)){//representa a planilha Excel no formato .xlsx recebe ela toda!
			XSSFSheet sheet = workbook.getSheetAt(0); //ABA da planilha no caso como só tem uma vai ser a 0
			Iterator<Row> iterator = sheet.iterator(); //Cria um iterador de linhas da aba da planilha. Isso permite percorrer cada linha uma por uma.
			
			if(iterator.hasNext()) iterator.next();//Se houver pelo menos uma linha, a primeira linha será ignorada aqui, pula o cabeçalho
			
			return parseRowsToPersonDTOList(iterator);
		}
	}

	private List<PersonDTOV1> parseRowsToPersonDTOList(Iterator<Row> iterator) {
		List<PersonDTOV1> people = new ArrayList<PersonDTOV1>();
		while(iterator.hasNext()) { //confirma se tem uma proxima linha se não termna o laço
			Row row = iterator.next();//pega a proxima linha e transforma em um row que é como um array de colunas(nome, sobrenome, endereço e sexo)
			if(isRowValid(row)) { //Verifica cada linha se é nula ou vazia
				people.add(parseRowToPersonDTO(row));//Passando pela validação transforma a linha em um Objeto Person e adiciona a lista criada (people)
			}
		}
		return people;
	}

	private PersonDTOV1 parseRowToPersonDTO(Row row) {
		PersonDTOV1 personDTOV1 = new PersonDTOV1();
		personDTOV1.setFirstName(row.getCell(0).getStringCellValue());
		personDTOV1.setLastName(row.getCell(1).getStringCellValue());
		personDTOV1.setAddress(row.getCell(2).getStringCellValue());
		personDTOV1.setGender(row.getCell(3).getStringCellValue());
		personDTOV1.setEnabled(true);
		return personDTOV1;
	}

	private boolean isRowValid(Row row) {
		return row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK;
	}

}
