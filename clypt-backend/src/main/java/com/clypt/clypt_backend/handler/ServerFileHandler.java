package com.clypt.clypt_backend.handler;

import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.clypt.clypt_backend.responses.CodeResponse;

public class ServerFileHandler implements FileHandler {

	@Override
	public CodeResponse upload(MultipartFile[] multipartFiles, String folderName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String uniqueCode, List<String> fileUrls) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Path getFiles(String uniqueCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileType(String uniqueCode) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
