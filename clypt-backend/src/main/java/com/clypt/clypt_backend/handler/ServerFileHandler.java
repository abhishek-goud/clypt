package com.clypt.clypt_backend.handler;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;
import com.clypt.clypt_backend.entity.UrlMapping;
import com.clypt.clypt_backend.io.Delete;
import com.clypt.clypt_backend.responses.CodeResponse;
import com.clypt.clypt_backend.services.UrlMappingService;
import com.clypt.clypt_backend.strategy.upload.UploadStrategy;
import com.clypt.clypt_backend.strategy.upload.UploadStrategySelector;
import com.clypt.clypt_backend.utils.EncryptionUtil;

@Service
@ConditionalOnProperty(name = "file.handler", havingValue = "server")
public class ServerFileHandler implements FileHandler {

	@Value("${base.directory}")
	private String BASE_DIRECTORY;

	@Autowired
	private UploadStrategySelector uploadStrategySelector;

	@Autowired
	private Delete deleteService;

	@Autowired
	private UrlMappingService urlMappingService;

	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	@Override
	public CodeResponse upload(MultipartFile[] multipartFiles, String folderName) {
		List<String> fileUrls;
		String uniqueCode = generateUniqueCode();

		Path folderPath = Paths.get(BASE_DIRECTORY, folderName, uniqueCode);

		try {
			// create directories for the new files.
			Files.createDirectories(folderPath);

			UploadStrategy strategy = uploadStrategySelector.selectStrategy(multipartFiles.length);
			fileUrls = strategy.uploadFiles(multipartFiles, folderPath, uniqueCode);
			String fileExtension = strategy.getFileType();

			// save file URLs with the unique code.
			urlMappingService.save(uniqueCode, fileUrls, fileExtension);

			log.info("Uploaded from ServerFileHandler");
			return new CodeResponse(uniqueCode);

		} catch (Exception e) {
			log.error("Failed to upload file from ServerFileHandler");
			throw new RuntimeException("Failed to upload files");
		}

	}

	@Override
	public void delete(String uniqueCode, List<String> fileUrls) {
		try {
			deleteService.deleteFiles(fileUrls);

			// delete the uniqueCode directory.
			Path codePath = Paths.get(BASE_DIRECTORY, "anonymous", uniqueCode);
			Files.deleteIfExists(codePath);
			
		    //delete the zip file.
            Path zipFilePath = Paths.get(BASE_DIRECTORY, uniqueCode + ".zip");
            Files.deleteIfExists(zipFilePath);
			log.info("Deleted from ServerFileHandler");
		} catch (Exception e) {
			log.error("Failed to delete from ServerFileHandler");
			throw new RuntimeException("Failed to delete files for code: " + uniqueCode);
		}

	}

	@Override
	public Path getFiles(String uniqueCode) {
	    try {
	    	
	        // path where the zip file will be saved
	        Path zipFilePath = Paths.get(BASE_DIRECTORY, uniqueCode + ".zip");

	        //check if zip file exist
	        if (Files.exists(zipFilePath)) {
	            log.info("Zip file already exists for code {}", uniqueCode);
	            return zipFilePath;
	        }

	      
	        UrlMapping urlMapping = urlMappingService.get(uniqueCode);
	        List<String> fileUrls = urlMapping.getUrls();

	        
	        if (fileUrls == null || fileUrls.isEmpty()) {
	            throw new RuntimeException("No files found for the provided code");
	        }

	        //generate decryption key using the unique code
	        byte[] secretKey = EncryptionUtil.generateKeyFromUniqueCode(uniqueCode);

	        // create the zip file in the base directory
	        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
	             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

	            // Loop through each fileUrl
	            for (String fileUrl : fileUrls) {

	                Path filePath = Paths.get(fileUrl);

	                // Read the encrypted file data as bytes
	                byte[] encryptedFileBytes = Files.readAllBytes(filePath);
	                // Decrypt the file bytes
	                byte[] decryptedBytes = EncryptionUtil.decrypt(encryptedFileBytes, secretKey);

	                // Add the decrypted file to the zip
	                ZipEntry zipEntry = new ZipEntry(filePath.getFileName().toString());
	                zipOut.putNextEntry(zipEntry);
	                zipOut.write(decryptedBytes);
	                zipOut.closeEntry();
	            }
	        }

	        log.info("Zip file created for code {}", uniqueCode);
	        System.out.println("getFiles from ServerFileHandler");
	        return zipFilePath;

	    } catch (Exception e) {
	        throw new RuntimeException("No files found for the code");
	    }
	}

	@Override
	public String getFileType(String uniqueCode) {
		// TODO Auto-generated method stub
		return null;
	}

	private String generateUniqueCode() {
		System.out.println("generateUniqueCode from ServerFileHandler");
		return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
	}

}
