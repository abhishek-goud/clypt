package com.clypt.clypt_backend.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The FileHandlerController provides end-points to handle the uploading and retrieval of file data.
 * The endpoints associated with it are: "api/v1/clypt/anonymous (POST & GET)
 *
 * @author Abhishek Goud
 */

@RestController
@RequestMapping("api/v1/clypt/anonymous")
public class AnonymousFileHandlerController {
	
	@PostMapping
	public ResponseEntity<Object> uploadFile() {
	    Map<String, Object> response = new HashMap<>();
	    response.put("message", "File uploaded successfully.");
	    response.put("timestamp", LocalDateTime.now());

	    return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
	}

	
	@GetMapping
	public ResponseEntity<Object> getFile(@RequestParam("code") String uniqueCode){
		return ResponseEntity.status(HttpStatus.SC_ACCEPTED).body(uniqueCode);
		
	}



   

   
}
