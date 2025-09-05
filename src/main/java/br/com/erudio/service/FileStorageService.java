package br.com.erudio.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.erudio.config.FileStorageConfig;
import br.com.erudio.exception.FileNotFoundException;
import br.com.erudio.exception.FileStorageException;

@Service
public class FileStorageService {
	
	private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
	
	 // Caminho onde os arquivos serão armazenados no sistema de arquivos
    private final Path fileStorageLocation;

    // Construtor da classe FileStorageService que recebe uma configuração como parâmetro
    public FileStorageService(FileStorageConfig config) {
        // Obtém o diretório de upload especificado na configuração, transforma em caminho absoluto e normaliza
        Path path = Paths.get(config.getUpload_dir())
                .toAbsolutePath() // transforma o caminho em absoluto
                .normalize();     // normaliza o caminho (remove redundâncias como "." ou "..")

        // Atribui o caminho ao atributo da classe
        this.fileStorageLocation = path;

        try {
        	log.info("Creating Directories.");
            // Cria os diretórios no caminho especificado, se ainda não existirem
            Files.createDirectories(this.fileStorageLocation);
        } 
        catch (Exception e) {
        	log.error("Could not create the directory where files will be stored!");
            // Lança uma exceção personalizada caso não consiga criar os diretórios
            throw new FileStorageException("Could not create the directory where files will be stored!", e);
        }
    }

    // Método para armazenar um arquivo recebido via upload
    public String storeFile(MultipartFile file) {
        // Limpa o nome do arquivo original para evitar problemas com caminhos maliciosos
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Verifica se o nome do arquivo contém uma sequência de caminho inválida (ex: "..")
            if(fileName.contains("..")) {
            	log.error("Sorry, file name contains an invalid path sequence: " + fileName);
                // Lança uma exceção se o nome for suspeito (tentativa de acessar diretórios pai)
                throw new FileStorageException("Sorry, file name contains an invalid path sequence: " + fileName);
            }

            log.info("Saving Files in Disk.");
            // Resolve o caminho final do arquivo dentro do diretório de upload
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            // Copia o conteúdo do arquivo para o local de destino, substituindo se já existir
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Retorna o nome do arquivo armazenado
            return fileName;
        } 
        catch (Exception e) {
        	log.error("Could not store file " + fileName + ". Please try again!");
            // Lança uma exceção personalizada se ocorrer qualquer erro durante o armazenamento
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
    }
    
    public Resource loadFileAsResource(String fileName) {
    	try {
    		Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
    		Resource resource = new UrlResource(filePath.toUri());
    		if(resource.exists()) {
    			return resource;
    		}
    		else {
    			log.error("File Not Found " + fileName);
        		throw new FileNotFoundException("File Not Found " + fileName);
    		}
    	}
    	catch(Exception e ){
    		log.error("File Not Found " + fileName);
    		throw new FileNotFoundException("File Not Found " + fileName, e);
    	}
    }
}
