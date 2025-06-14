package com.clypt.clypt_backend.exceptions;

/**
 * Exception to handle the events that failed during file upload.
 */

public class FileUploadFailedException extends RuntimeException{
	
	public FileUploadFailedException(String message) {
		super(message);
	}
}
