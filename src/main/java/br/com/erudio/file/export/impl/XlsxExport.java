package br.com.erudio.file.export.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.file.export.contract.PersonExport;

@Component
public class XlsxExport implements PersonExport  {

	@Override
	public Resource exportPeople(List<PersonDTOV1> people) throws Exception {
		// Cria um novo Workbook (planilha Excel em memória).
	    // Aqui está sendo usada a implementação XSSFWorkbook (formato .xlsx)
		try(Workbook workbook = new XSSFWorkbook()){
			
			// Cria uma nova aba (sheet) na planilha chamada "People".
			Sheet sheet = workbook.createSheet("People");
			
			// Cria a primeira linha (índice 0) que será o cabeçalho
			Row headerRow = sheet.createRow(0);
			
			 // Define os nomes das colunas.
			String[] headers = {"ID", "First Name", "Last Name", "Address", "Gender", "Enabled"};
			
			 // Loop para criar as células do cabeçalho.
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);// Cria célula na posição i.
				cell.setCellValue(headers[i]); // Define o texto da célula
				cell.setCellStyle(createHeaderCellStyle(workbook));// Aplica estilo (negrito, centralizado).
				
			}
			
			int rowIndex = 1 ;// Define que as próximas linhas começarão na linha 1 (logo abaixo do cabeçalho).
			
			// Percorre a lista de pessoas e escreve cada uma como uma linha da planilha
			for(PersonDTOV1 person : people) {
				Row row = sheet.createRow(rowIndex++);// Cria uma nova linha.
				row.createCell(0).setCellValue(person.getId());
				row.createCell(1).setCellValue(person.getFirstName());
				row.createCell(2).setCellValue(person.getLastName());
				row.createCell(3).setCellValue(person.getAddress());
				row.createCell(4).setCellValue(person.getGender());
				row.createCell(5).setCellValue(person.getEnabled() != null && person.getEnabled() ? "Yes" : "No");//Escreve se é yes ou não em vez de true ou false
			}
			
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);//Deixa tabulado com o tamanho do header
			}
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();//Cria um fluxo de saída
			workbook.write(outputStream);// Escreve o conteúdo do workbook no fluxo de saída.
			
			// Retorna o conteúdo como recurso em memória, pronto para download.
			return new ByteArrayResource(outputStream.toByteArray());
		}
	}

	private CellStyle createHeaderCellStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName(font.getFontName());
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
		
		
	}

	@Override
	public Resource exportPerson(PersonDTOV1 person) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
