package com.clypt.clypt_backend.exceptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.clypt.clypt_backend.responses.ApiResponse;

import jakarta.persistence.EntityNotFoundException;

/**
 * Handles exceptions by returning custom responses instead of stack traces.
 */

@RestControllerAdvice
public class CustomExceptionHandler {
	
	@Value("${spring.servlet.multipart.max-file-size}")
	String maxFileSize;
	
	@Value("${spring.servlet.multipart.max-request-size}")
	String totalFileSize;
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public final ResponseEntity<ApiResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
	    String message = "Maximum upload size exceeded. Each file must be under " + maxFileSize + ", and total upload size under " + totalFileSize + ".";
	    return new ResponseEntity<>(new ApiResponse(message), HttpStatus.PAYLOAD_TOO_LARGE);
	}


	@ExceptionHandler(MultipartException.class)
	public final ResponseEntity<ApiResponse> handleMultipartException(MultipartException e) {
	    return new ResponseEntity<>(new ApiResponse("Invalid upload request: " + e.getMessage()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<ApiResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TimeExpiredException.class)
    public final ResponseEntity<ApiResponse> handleTimeExpiredException(TimeExpiredException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.GONE);
    }

    @ExceptionHandler(FileUploadFailedException.class)
    public final ResponseEntity<ApiResponse> handleImageUploadFailedException(FileUploadFailedException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileDeleteFailedException.class)
    public final ResponseEntity<ApiResponse> handleFileDeleteFailedException(FileDeleteFailedException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InValidUrlException.class)
    public final ResponseEntity<ApiResponse> handleInValidUrlException(InValidUrlException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
