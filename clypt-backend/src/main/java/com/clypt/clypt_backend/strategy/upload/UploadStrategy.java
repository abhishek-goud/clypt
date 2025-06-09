package com.clypt.clypt_backend.strategy.upload;

import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface UploadStrategy {

	List<String> uploadFiles(MultipartFile[] files, Path folderPath, String uniqueCode);
}
