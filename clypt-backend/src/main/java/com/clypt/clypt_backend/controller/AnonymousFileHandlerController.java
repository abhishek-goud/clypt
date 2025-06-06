package com.clypt.clypt_backend.controller;



import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.clypt.clypt_backend.entity.UrlMapping;
import com.clypt.clypt_backend.handler.FileHandler;
import com.clypt.clypt_backend.repository.UrlMappingRepository;
import com.clypt.clypt_backend.responses.CodeResponse;


import lombok.extern.slf4j.Slf4j;



/**
 * The FileHandlerController provides end-points to handle the uploading and retrieval of file data.
 * The endpoints associated with it are: "api/v1/clypt/anonymous (POST & GET)
 *
 */
@Slf4j
@RestController
@RequestMapping("api/clypt/anonymous")
public class AnonymousFileHandlerController {
	
	    private final FileHandler fileHandler;

	    @Value("${folder.anonymous}") 
	    private String folderName;
	    
	    @Autowired
	    private UrlMappingRepository urlMappingRepository;
	    
	    private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	    public AnonymousFileHandlerController(FileHandler fileHandler) {
	        this.fileHandler = fileHandler;
	    }
	    

	    /**
	     * uploadFile uploads the files provided through the request.
	     *
	     * @param multipartFiles It represents the files given through the request.
	     * @return It returns the code associated with the uploaded files.
	     */
	
	@PostMapping
	public ResponseEntity<CodeResponse> uploadFile(@RequestParam("files") MultipartFile[] multipartFiles) {
		log.info("API endpoint /ghost-drop/anonymous: Method:POST");
		System.out.println("length- "+multipartFiles.length);
	    
		CodeResponse code = this.fileHandler.upload(multipartFiles, folderName);
		

		return new ResponseEntity<>(code, HttpStatus.CREATED);
	}

	
	@GetMapping
	public ResponseEntity<Resource> getFile(@RequestParam("code") String uniqueCode){
		log.info("API endpoint /ghost-drop/anonymous: Method:GET");

        // retrieve the path to the zip file.
        Path zipFilePath = fileHandler.getFiles(uniqueCode);

        try {
            // create a resource for the zip file.
            Resource resource = new UrlResource(zipFilePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                // return the zip file as a downloadable response.
            	
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFilePath.getFileName().toString() + "\"")
                        .body(resource);
            } else {
                log.error("Zip file not found or not readable: {}", zipFilePath);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            log.error("Error while downloading zip file for code {}: {}", uniqueCode, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
		
	}
	
	@GetMapping("filetype")
	public ResponseEntity<String> getFileType(@RequestParam("code") String uniqueCode){
		String fileType = fileHandler.getFileType(uniqueCode);
		
		return new ResponseEntity<>(fileType, HttpStatus.OK);
		
	}
	
	@DeleteMapping
	public ResponseEntity<Object> deleteFile(@RequestParam("code") String uniqueCode){
		List<UrlMapping> expiredMappings = this.urlMappingRepository.findByExpiryDateBefore(LocalDateTime.now());

        if (!expiredMappings.isEmpty()) {
            // Iterating over the expiredMappings.
            for (UrlMapping mapping : expiredMappings) {
                this.fileHandler.delete(mapping.getUniqueCode(), mapping.getUrls());
                this.urlMappingRepository.delete(mapping);
            }
        }

		return new ResponseEntity<>("deleted", HttpStatus.OK);
		
	}



   

   
}
