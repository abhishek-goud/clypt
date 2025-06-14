package com.clypt.clypt_backend.strategy.delete;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;
import com.clypt.clypt_backend.exceptions.FileDeleteFailedException;

/**
 * ParallelDeleteStrategy uses multi-threading to delete files in parallel.
 */

@Component
public class ParallelDeleteStrategy implements DeleteStrategy {
	@Override
	public void deleteFiles(List<String> fileUrls) {
		// TODO Auto-generated method stub
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (String fileUrl : fileUrls) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					Path path = Paths.get(fileUrl);
					Files.deleteIfExists(path);
					log.info("File with path {} deleted", path);

				} catch (Exception e) {
					log.error("{} error occurred", e.getClass());
					log.error("message: {}", e.getMessage());

					throw new FileDeleteFailedException("Failed to delete file");
				}
			});

			futures.add(future);
		}

		futures.forEach(future -> future.join());

	}

	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

}
