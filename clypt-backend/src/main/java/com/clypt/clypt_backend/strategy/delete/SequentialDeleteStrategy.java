package com.clypt.clypt_backend.strategy.delete;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;


public class SequentialDeleteStrategy implements DeleteStrategy{
	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	@Override
	public void deleteFiles(List<String> fileUrls) {
		// TODO Auto-generated method stub
		for(String fileUrl: fileUrls) {
			try {
				Path path = Paths.get(fileUrl);
				Files.deleteIfExists(path);
				
				log.info("File with path {} deleted", fileUrl);
			} catch(Exception e) {
				log.error("{} error occured", e.getClass());
				log.error("message: {}", e.getMessage());
				
				throw new RuntimeException("Failed to delete file");
			}
		}
		
	}
	
	

}
