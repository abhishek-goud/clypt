package com.clypt.clypt_backend.controller;

import java.util.List;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.clypt.clypt_backend.handler.FileHandler;

import com.clypt.clypt_backend.responses.CodeResponse;

/**
 * Handles file upload and retrieval at the endpoint: api/clypt/anonymous
 */

@RestController
@RequestMapping("api/clypt/anonymous")
public class AnonymousFileHandlerController {

	private final FileHandler fileHandler;

	@Value("${folder.anonymous}")
	private String folderName;

	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	public AnonymousFileHandlerController(FileHandler fileHandler) {
		this.fileHandler = fileHandler;
	}

	/**
	 * Uploads the files provided in the request.
	 *
	 * @param multipartFiles the files to be uploaded
	 * @return the unique code associated with the uploaded files
	 */
	
	@PostMapping
	public ResponseEntity<CodeResponse> uploadFile(@RequestParam("files") MultipartFile[] multipartFiles) {
		log.info("API endpoint /clypt/anonymous: Method:POST");
		System.out.println("length- " + multipartFiles.length);

		CodeResponse code = this.fileHandler.upload(multipartFiles, folderName);

		return new ResponseEntity<>(code, HttpStatus.CREATED);
	}

	/**
	 * Returns a ZIP file containing all files associated with the given code.
	 *
	 * @param uniqueCode the unique code provided as a query parameter
	 * @return the ZIP file as a downloadable resource
	 */
	
	@GetMapping
	public ResponseEntity<Resource> getFile(@RequestParam("code") String uniqueCode) {
		log.info("API endpoint /clypt/anonymous: Method:GET");

		// retrieve the path to the zip file.
		Path zipFilePath = fileHandler.getFiles(uniqueCode);

		try {
			// create a resource for the zip file.
			Resource resource = new UrlResource(zipFilePath.toUri());

			if (resource.exists() || resource.isReadable()) {
				// return the zip file as a downloadable response.

				return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
						.header(HttpHeaders.CONTENT_DISPOSITION,
								"attachment; filename=\"" + zipFilePath.getFileName().toString() + "\"")
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
	public ResponseEntity<List<String>> getFileType(@RequestParam("code") String uniqueCode) {
		log.info("API endpoint /clypt/anonymous/filetype: Method:GET");
		List<String> fileType = fileHandler.getFileType(uniqueCode);

		return new ResponseEntity<>(fileType, HttpStatus.OK);

	}

}
