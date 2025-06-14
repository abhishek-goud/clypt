package com.clypt.clypt_backend.responses;

/**
 * CodeResponse is used to create an object with a unique code that is sent as a response.
 */

public class CodeResponse{
	String uniqueCode;
	public CodeResponse(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
	
	public String getUniqueCode() {
		return uniqueCode;
	}	
	
}



