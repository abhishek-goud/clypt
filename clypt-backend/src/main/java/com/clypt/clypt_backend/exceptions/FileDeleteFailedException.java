package com.clypt.clypt_backend.exceptions;

/**
 * Exception to handle the deletion of files.
 */

public class FileDeleteFailedException extends RuntimeException{
	
	public FileDeleteFailedException(String message) {
		super(message);
	}
}
