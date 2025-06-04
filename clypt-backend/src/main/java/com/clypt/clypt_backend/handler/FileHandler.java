package com.clypt.clypt_backend.handler;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.clypt.clypt_backend.responses.CodeResponse;
import java.nio.file.Path;

/**
 * FileHandler handles the uploading, deleting and the retrieval of files from the cloud.
 *
 * @author Abhishek Goud
 */
public interface FileHandler {

    /**
     * upload uploads the files to the corresponding folder.
     */
    CodeResponse upload(MultipartFile[] multipartFiles, String folderName);

    /**
     * delete removes the files using the urls and the code directories.
     */
    void delete(String uniqueCode, List<String> fileUrls);

    /**
     * getFiles creates a zip of the files associated with code and returns the path.
     */
    Path getFiles(String uniqueCode);
    
    String getFileType(String uniqueCode);
}