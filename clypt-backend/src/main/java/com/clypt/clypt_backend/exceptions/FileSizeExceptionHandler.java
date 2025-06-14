package com.clypt.clypt_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.clypt.clypt_backend.responses.ApiResponse;

@ControllerAdvice
public class FileSizeExceptionHandler {
	
//	@ExceptionHandler(MaxUploadSizeExceededException.class)
//    public final ResponseEntity<ApiResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
//        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.PAYLOAD_TOO_LARGE);
//    }
//
//	@ExceptionHandler(MultipartException.class)
//	public final ResponseEntity<ApiResponse> handleMultipartException(MultipartException ex) {
//	    return new ResponseEntity<>(new ApiResponse("Invalid upload request: " + ex.getMessage()), HttpStatus.BAD_REQUEST);
//	}

}
