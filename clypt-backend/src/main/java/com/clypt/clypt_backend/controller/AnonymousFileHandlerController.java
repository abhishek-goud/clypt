package com.clypt.clypt_backend.controller;



import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
 * @author Abhishek Goud
 */
@Slf4j
@RestController
@RequestMapping("api/v1/clypt/anonymous")
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
	public ResponseEntity<Object> getFile(@RequestParam("code") String uniqueCode){
		return new ResponseEntity<>(uniqueCode, HttpStatus.OK);
		
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
