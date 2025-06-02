package com.clypt.clypt_backend.responses;

/**
 * CodeResponse is a record that holds a unique code.
 * This class is immutable and thread-safe.
 * <p>
 * Author: Abhishek Goud
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

//public record CodeResponse(String uniqueCode) {
//}

