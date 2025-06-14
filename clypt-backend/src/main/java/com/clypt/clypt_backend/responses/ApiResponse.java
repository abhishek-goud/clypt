package com.clypt.clypt_backend.responses;

/**
 * ApiResponse is used for sending error responses
 */
public class ApiResponse {
	String message;
	public ApiResponse(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}


}



