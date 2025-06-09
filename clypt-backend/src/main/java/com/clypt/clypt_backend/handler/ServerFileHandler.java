package com.clypt.clypt_backend.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;
import com.clypt.clypt_backend.io.Delete;
import com.clypt.clypt_backend.io.EncryptionUpload;
import com.clypt.clypt_backend.io.ParallelUpload;
import com.clypt.clypt_backend.io.Upload;
import com.clypt.clypt_backend.responses.CodeResponse;
import com.clypt.clypt_backend.services.UrlMappingService;
import com.clypt.clypt_backend.utils.EncryptionUtil;

@Service
@ConditionalOnProperty(name = "file.handler", havingValue = "server")
public class ServerFileHandler implements FileHandler {

	@Value("${base.directory}")
	private String BASE_DIRECTORY;

//	@Autowired
//	private Upload uploadService;

//	@Autowired
//	private EncryptionUpload uploadService;
	
	@Autowired
	private ParallelUpload uploadService;

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

			fileUrls = uploadService.uploadFiles(multipartFiles, folderPath, uniqueCode);

			String fileExtension = uploadService.getFileType();

			// save file URLs with the unique code.
			urlMappingService.save(uniqueCode, fileUrls, fileExtension);

			System.out.println("Uploaded from ServerFileHandler");
			return new CodeResponse(uniqueCode);

		} catch (Exception e) {
			throw new RuntimeException("Failed to upload files");
		}

	}

	@Override
	public void delete(String uniqueCode, List<String> fileUrls) {
		try {
			deleteService.deleteFiles(fileUrls);

			// delete the code directory.
			Path codePath = Paths.get(BASE_DIRECTORY, "anonymous", uniqueCode);
			Files.deleteIfExists(codePath);

		} catch (Exception e) {
			throw new RuntimeException("Failed to delete files for code: " + uniqueCode);
		}

	}

	@Override
	public Path getFiles(String uniqueCode) {
		// TODO Auto-generated method stub
		return null;
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
