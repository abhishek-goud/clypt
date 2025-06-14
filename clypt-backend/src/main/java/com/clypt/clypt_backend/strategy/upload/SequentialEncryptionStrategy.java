package com.clypt.clypt_backend.strategy.upload;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;
import com.clypt.clypt_backend.exceptions.FileUploadFailedException;
import com.clypt.clypt_backend.utils.EncryptionUtil;

/**
 * The SequentialEncryptionStrategy stores the files by encrypting and storing the files sequentially one after others
 */

@Component
public class SequentialEncryptionStrategy implements UploadStrategy {
	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	public List<String> uploadFiles(MultipartFile files[], Path folderPath, String uniqueCode) {
		List<String> fileUrls = new ArrayList<>();

		try {
			byte[] secretKey = EncryptionUtil.generateKeyFromUniqueCode(uniqueCode);

			for (MultipartFile file : files) {
				byte fileBytes[] = file.getBytes();
				byte encryptedBytes[] = EncryptionUtil.encrypt(fileBytes, secretKey);

				String fileNameWithExtension = file.getOriginalFilename();
				if (fileNameWithExtension == null) {
					throw new FileNotFoundException("Received a file without a valid name in the upload request.");
				}

				Path filePath = folderPath.resolve(fileNameWithExtension);
				Files.write(filePath, encryptedBytes);
				fileUrls.add(filePath.toString());

			}

		} catch (Exception e) {
			log.error("{} error occurred", e.getClass());
			log.error("message: {}", e.getMessage());

			throw new FileUploadFailedException("Failed to upload the files");
		}

		return fileUrls;
	}

}
