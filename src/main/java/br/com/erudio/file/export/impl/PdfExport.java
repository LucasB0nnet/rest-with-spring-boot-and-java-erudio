package br.com.erudio.file.export.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.file.export.contract.PersonExport;
import br.com.erudio.service.QrCodeService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class PdfExport implements PersonExport  {
	
	@Autowired
	private QrCodeService codeService;

	@Override
	public Resource exportPeople(List<PersonDTOV1> people) throws Exception {
		
		// Carrega o arquivo de template do relatório (people.jrxml) da pasta "resources/templates".
	    // Esse arquivo contém a definição do layout do relatório (campos, tabelas, títulos etc).
	    InputStream inputStream = getClass().getResourceAsStream("/templates/people.jrxml");
	    
	    if (inputStream == null) {
	        // Se o arquivo não for encontrado, lança um erro.
	        throw new RuntimeException("Template file not found : /templates/people.jrxml");
	    }

	    // Compila o arquivo .jrxml em um objeto JasperReport que pode ser usado para gerar relatórios.
	    JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

	    // Cria um "data source" (fonte de dados) baseado na lista de pessoas recebida como parâmetro.
	    // É como se o Jasper fosse ler os dados dessa lista para preencher o relatório.
	    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(people);

	    // Mapa de parâmetros adicionais que podem ser enviados para o relatório.
	    // (ex: passar um título, uma data ou qualquer variável que o relatório use).
	    // Aqui está vazio, mas poderia ter valores.
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    // Preenche o relatório com os dados da lista (dataSource) e os parâmetros informados.
	    // Isso gera um objeto JasperPrint, que é o relatório pronto para exportação.
	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

	    // Agora vamos exportar o relatório em PDF.
	    // Usamos um ByteArrayOutputStream para escrever o PDF em memória (sem precisar salvar em disco).
	    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
	    	
	        // Converte o JasperPrint (relatório pronto) para PDF e escreve no outputStream.
	        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

	        // Retorna o PDF como um recurso em memória (ByteArrayResource),
	        // que pode ser enviado como resposta em uma API ou salvo depois.
	        return new ByteArrayResource(outputStream.toByteArray());
		}

	}

	@Override
	public Resource exportPerson(PersonDTOV1 person) throws Exception {
		// Carrega o arquivo de template do relatório (people.jrxml) da pasta "resources/templates".
	    // Esse arquivo contém a definição do layout do relatório (campos, tabelas, títulos etc).
	    InputStream mainTemplatesStream = getClass().getResourceAsStream("/templates/person.jrxml");	    
	    if (mainTemplatesStream == null) {
	        // Se o arquivo não for encontrado, lança um erro.
	        throw new RuntimeException("Template file not found : /templates/person.jrxml");
	    }
	    
	    InputStream mainSubTemplatesStream = getClass().getResourceAsStream("/templates/books.jrxml");
	    if (mainSubTemplatesStream == null) {
	        // Se o arquivo não for encontrado, lança um erro.
	        throw new RuntimeException("Template file not found : /templates/books.jrxml");
	    }

	    // Compila o arquivo .jrxml em um objeto JasperReport que pode ser usado para gerar relatórios.
	    JasperReport jasperReport = JasperCompileManager.compileReport(mainTemplatesStream);
	    JasperReport jasperSubReport = JasperCompileManager.compileReport(mainSubTemplatesStream);

	    //TODO GENERATE QR Code
	    InputStream qrCode = codeService.generateQrCode(person.getProfileUrl(), 200, 200);
	    
	    // Cria um "data source" (fonte de dados) baseado na lista de pessoas recebida como parâmetro.
	    // É como se o Jasper fosse ler os dados dessa lista para preencher o relatório.
	    JRBeanCollectionDataSource maindataSource = new JRBeanCollectionDataSource(Collections.singletonList(person));
	    JRBeanCollectionDataSource subdataSource = new JRBeanCollectionDataSource(person.getBooks());

	    String path = getClass().getResource("/templates/books.jasper").getPath();
	    
	    // Mapa de parâmetros adicionais que podem ser enviados para o relatório.
	    // (ex: passar um título, uma data ou qualquer variável que o relatório use).
	    // Aqui está vazio, mas poderia ter valores.
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("BOOK_SUB_DIR", jasperSubReport);       // Sub-relatório compilado
	    parameters.put("SUB_REPORT_DATA_SOURCE", subdataSource);  // Data source do sub-relatório
	    parameters.put("QR_CODE_IMAGE", qrCode);
	    parameters.put("PERSON_ID", person.getId());

	    // Preenche o relatório com os dados da lista (dataSource) e os parâmetros informados.
	    // Isso gera um objeto JasperPrint, que é o relatório pronto para exportação.
	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, maindataSource);

	    // Agora vamos exportar o relatório em PDF.
	    // Usamos um ByteArrayOutputStream para escrever o PDF em memória (sem precisar salvar em disco).
	    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
	    	
	        // Converte o JasperPrint (relatório pronto) para PDF e escreve no outputStream.
	        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

	        // Retorna o PDF como um recurso em memória (ByteArrayResource),
	        // que pode ser enviado como resposta em uma API ou salvo depois.
	        return new ByteArrayResource(outputStream.toByteArray());
		}
	}

	

}
