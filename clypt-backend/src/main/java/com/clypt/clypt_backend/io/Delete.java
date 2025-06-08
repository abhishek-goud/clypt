package com.clypt.clypt_backend.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Delete {
	
	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);
	
	public void deleteFiles(List<String> fileUrls) {
		for(String fileUrl: fileUrls) {
			try {
				Path path = Paths.get(fileUrl);
				Files.deleteIfExists(path);
				
				log.info("File with path {} deleted", fileUrl);
				
		
			} catch(IOException e) {
				log.error("{} error occured", e.getClass());
				log.error("message: {}", e.getMessage());
				
				throw new RuntimeException("Failed to delete the file");
				
				
			}
		}
	}

}
