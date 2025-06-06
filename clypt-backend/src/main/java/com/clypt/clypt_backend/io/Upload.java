package com.clypt.clypt_backend.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;

import lombok.extern.slf4j.Slf4j;

/**
 * SequentialStrategy stores the files one by one without encryption or
 * parallelism.
 *
 */

@Component
public class Upload {

	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	public String fileExtension = "";

	public List<String> uploadFiles(MultipartFile[] files, Path folderPath, String uniqueCode){
		List<String> fileUrls = new ArrayList<>();
		String fileExtension = "";
		
		for(MultipartFile file: files) {
			try {
				String fileNameWithExtension = file.getOriginalFilename();
				if(fileExtension.length() == 0) fileExtension = getFileExtension(fileNameWithExtension);;
						
				if(fileNameWithExtension == null) {
					throw new RuntimeException("Received a file without a valid name in the upload request.");
				}
				Path filePath = folderPath.resolve(fileNameWithExtension);
				Files.write(filePath, file.getBytes());
				fileUrls.add(filePath.toString());
			} catch(IOException e) {
				log.error("{} error occurred", e.getClass());
                log.error("message: {}", e.getMessage());
				
			}
			
		}
		return fileUrls;
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

}
