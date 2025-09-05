package br.com.erudio.file.export.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import br.com.erudio.exception.BadRequestException;
import br.com.erudio.file.export.contract.PersonExport;
import br.com.erudio.file.export.impl.CsvExport;
import br.com.erudio.file.export.impl.PdfExport;
import br.com.erudio.file.export.impl.XlsxExport;
import br.com.erudio.file.export.mediatypes.MediaTypes;

@Component
public class FileExportFactory {

	private Logger log = LoggerFactory.getLogger(FileExportFactory.class);
	
	@Autowired
	private ApplicationContext context;
	
	public PersonExport getExport(String acceptHeader) throws Exception{
		if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)) {
			return context.getBean(XlsxExport.class);
		}
		else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE)) {
			return context.getBean(CsvExport.class);
		}
		else if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_PDF_VALUE)) {
			return context.getBean(PdfExport.class);
		}
		else {
			throw new BadRequestException("Invalid File Name");
		}
	}
}
