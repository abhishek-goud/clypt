package com.clypt.clypt_backend.io;

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

/**
 * ParallelStrategy stores the files using multi-threading to achieve a faster processing
 * 
 */

@Component
public class ParallelUpload {
	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	private String fileType = "";

	public List<String> uploadFiles(MultipartFile files[], Path folderPath, String uniqueCode) {
		List<CompletableFuture<String>> futures = new ArrayList<>();
		for (MultipartFile file : files) {

			// new thread for each file
			CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
				try {
					String fileNameWithExtension = file.getOriginalFilename();
					if (fileNameWithExtension == null) {
						throw new RuntimeException("Received a file without a valid name in the upload request");
					}
					if (fileType.length() == 0)
						fileType = getFileExtension(fileNameWithExtension);
					Path filePath = folderPath.resolve(fileNameWithExtension);
					Files.write(filePath, file.getBytes());
					return filePath.toString();

				} catch (IOException e) {
					log.error("{} error occurred", e.getClass());
					log.error("message: {}", e.getMessage());

					throw new RuntimeException("Failed to upload the files!");

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

	// Helper method to extract file extension
	public String getFileExtension(String filename) {
		if (filename == null || filename.isEmpty()) {
			return "";
		}
		int lastDotIndex = filename.lastIndexOf('.');
		if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
			return "";
		}
		return filename.substring(lastDotIndex); // includes the dot (e.g., ".pdf")
	}

	public String getFileType() {
		return fileType;
	}
}
