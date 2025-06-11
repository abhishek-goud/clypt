package com.clypt.clypt_backend.strategy.upload;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;
import com.clypt.clypt_backend.utils.EncryptionUtil;

@Component
public class ParallelEncryptionStrategy implements UploadStrategy {
	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	public List<String> uploadFiles(MultipartFile[] files, Path folderPath, String uniqueCode) {
		byte[] secretKey;
		try {
			secretKey = EncryptionUtil.generateKeyFromUniqueCode(uniqueCode);
		} catch (Exception e) {
			log.error("{} error occurred", e.getClass());
			log.error("message: {}", e.getMessage());

			throw new RuntimeException("Failed to upload the files!");
		}

		List<CompletableFuture<String>> futures = new ArrayList<>();

		for (MultipartFile file : files) {
			CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
				try {
					byte[] fileBytes = file.getBytes();
					byte[] encryptedBytes = EncryptionUtil.encrypt(fileBytes, secretKey);

					String fileNameWithExtension = file.getOriginalFilename();
					if (fileNameWithExtension == null) {
						throw new RuntimeException("Received a file without a valid name in the upload request.");
					}
					
					Path filePath = folderPath.resolve(fileNameWithExtension);
					Files.write(filePath, encryptedBytes);
					return filePath.toString();

				} catch (Exception e) {
					log.error("{} error occurred", e.getClass());
					log.error("message: {}", e.getMessage());

					throw new RuntimeException("Failed to upload the files");
				}
			});

			futures.add(future);
		}

		List<String> savedPaths = new ArrayList<>();
		for (CompletableFuture<String> future : futures) {
			savedPaths.add(future.join());
		}

		return savedPaths;

	}


}
