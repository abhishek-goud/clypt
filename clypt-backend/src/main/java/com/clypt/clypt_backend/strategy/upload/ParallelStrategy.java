package com.clypt.clypt_backend.strategy.upload;


import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.clypt.clypt_backend.exceptions.FileUploadFailedException;

/**
 * ParallelStrategy uses multi-threading store files in parallel
 */

@Component
public class ParallelStrategy implements UploadStrategy {
	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	public List<String> uploadFiles(MultipartFile files[], Path folderPath, String uniqueCode) {
		List<CompletableFuture<String>> futures = new ArrayList<>();
		for (MultipartFile file : files) {

			// new thread for each file
			CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
				try {
					String fileNameWithExtension = file.getOriginalFilename();
					if (fileNameWithExtension == null) {
						throw new FileNotFoundException("Received a file without a valid name in the upload request");
					}

					Path filePath = folderPath.resolve(fileNameWithExtension);
					Files.write(filePath, file.getBytes());
					return filePath.toString();

				} catch (IOException e) {
					log.error("{} error occurred", e.getClass());
					log.error("message: {}", e.getMessage());

					throw new FileUploadFailedException("Failed to upload the files");

				}
			});

			futures.add(future);
		}

		// wait for all thread to complete
		CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		allOf.join();

		List<String> savedPaths = new ArrayList<>();

		for (CompletableFuture<String> future : futures) {
			String filePath = future.join(); // get filePaths
			savedPaths.add(filePath);
		}

		return savedPaths;

	}
}
