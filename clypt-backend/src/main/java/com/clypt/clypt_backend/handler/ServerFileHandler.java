package com.clypt.clypt_backend.handler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;
import com.clypt.clypt_backend.entity.UrlMapping;
import com.clypt.clypt_backend.exceptions.FileDeleteFailedException;
import com.clypt.clypt_backend.exceptions.FileUploadFailedException;
import com.clypt.clypt_backend.exceptions.TimeExpiredException;
import com.clypt.clypt_backend.responses.CodeResponse;
import com.clypt.clypt_backend.services.UrlMappingService;
import com.clypt.clypt_backend.strategy.delete.DeleteStrategy;
import com.clypt.clypt_backend.strategy.delete.DeleteStrategySelector;
import com.clypt.clypt_backend.strategy.upload.UploadStrategy;
import com.clypt.clypt_backend.strategy.upload.UploadStrategySelector;
import com.clypt.clypt_backend.utils.EncryptionUtil;

import jakarta.persistence.EntityNotFoundException;

/**
 * ServerFileHandler stores files locally on the server and handles upload,
 * retrieval, and deletion of files
 */

@Component
@ConditionalOnProperty(name = "file.handler", havingValue = "server")
public class ServerFileHandler implements FileHandler {

	@Value("${base.directory}")
	private String BASE_DIRECTORY;

	@Value("${encryption.enabled}")
	private String isEncrypted;

	@Autowired
	private UploadStrategySelector uploadStrategySelector;

	@Autowired
	private UrlMappingService urlMappingService;

	@Autowired
	private DeleteStrategySelector deleteStrategySelector;

	private final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

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

			// save file URLs with the unique code.
			urlMappingService.save(uniqueCode, fileUrls, new ArrayList<>());

			log.info("Uploaded from ServerFileHandler");
			return new CodeResponse(uniqueCode);

		} catch (Exception e) {
			log.error("Failed to upload file from ServerFileHandler");
			throw new FileUploadFailedException("Failed to upload the files");
		}

	}

	@Override
	public void delete(String uniqueCode, List<String> fileUrls) {
		try {
			DeleteStrategy strategy = deleteStrategySelector.selectStrategy(fileUrls.size());

			strategy.deleteFiles(fileUrls);

			// delete the uniqueCode directory.
			Path codePath = Paths.get(BASE_DIRECTORY, "anonymous", uniqueCode);
			Files.deleteIfExists(codePath);

			// delete the zip file.
			Path zipFilePath = Paths.get(BASE_DIRECTORY, uniqueCode + ".zip");
			Files.deleteIfExists(zipFilePath);
			log.info("Deleted from ServerFileHandler");
		} catch (Exception e) {
			log.error("Failed to delete from ServerFileHandler");
			throw new FileDeleteFailedException("Failed to delete files for code: " + uniqueCode);
		}

	}

	@Override
	public Path getFiles(String uniqueCode) {
		try {
			UrlMapping urlMapping = urlMappingService.get(uniqueCode);
			List<String> fileUrls = urlMapping.getUrls();

			if (fileUrls == null || fileUrls.isEmpty()) {
				throw new FileNotFoundException("No files found for the provided code");
			}
			
			// path where the zip file will be saved
			Path zipFilePath = Paths.get(BASE_DIRECTORY, uniqueCode + ".zip");

			// check if zip file exist
			if (Files.exists(zipFilePath)) {
				log.info("Zip file already exists for code {}", uniqueCode);
				return zipFilePath;
			}

			// generate decryption key using the unique code
			byte[] secretKey = EncryptionUtil.generateKeyFromUniqueCode(uniqueCode);

			// create the zip file in the base directory
			try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
					ZipOutputStream zipOut = new ZipOutputStream(fos)) {

				// Loop through each fileUrl
				for (String fileUrl : fileUrls) {

					Path filePath = Paths.get(fileUrl);

					byte[] fileBytes;

					if (isEncrypted.equals("true")) {
						// Read and decrypt the file bytes
						byte[] encryptedFileBytes = Files.readAllBytes(filePath);
						fileBytes = EncryptionUtil.decrypt(encryptedFileBytes, secretKey);
					} else {
						// Read the raw file bytes
						fileBytes = Files.readAllBytes(filePath);
					}

					// Add the file to the zip
					ZipEntry zipEntry = new ZipEntry(filePath.getFileName().toString());
					zipOut.putNextEntry(zipEntry);
					zipOut.write(fileBytes);
					zipOut.closeEntry();

				}
			}

			log.info("Zip file created for code {}", uniqueCode);
			System.out.println("getFiles from ServerFileHandler");
			return zipFilePath;

		} catch (EntityNotFoundException | TimeExpiredException e) {
			throw e;
		} catch (Exception e) {
			throw new EntityNotFoundException("No files found for the provided code");
		}
	}

	@Override
	public List<String> getFileType(String uniqueCode) {
		return new ArrayList<>();
	}

	private String generateUniqueCode() {
		System.out.println("generateUniqueCode from ServerFileHandler");
		return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
	}

}
