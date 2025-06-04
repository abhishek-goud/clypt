package com.clypt.clypt_backend.handler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;
import com.clypt.clypt_backend.responses.CodeResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CloudinaryFileHandler implements FileHandler{

	@Autowired
	private Cloudinary cloudinary;
	

	
	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);
	
//	@Override
//	public CodeResponse upload(MultipartFile[] multipartFiles, String folderName) {
//		// utils needed while uploading the files.
//		Map<Object, Object> utils = new HashMap<>();
//		utils.put("folder", folderName);
//        utils.put("use_filename", true);
//        utils.put("unique_filename", false);
//        utils.put("type", "upload");         
//        utils.put("resource_type", "raw");   
//        utils.put("access_mode", "public");   
//        
//        // list to store the uploaded files urls.
//        List<String> fileUrls = new ArrayList<>();
//        String uniqueCode = generateUniqueCode();
//        
//        try {
//        	for(MultipartFile file: multipartFiles) {
//        		String fileNameWithExtension = file.getOriginalFilename();
//        		String fileNameWithoutExtension = getFileNameWithoutExtension(fileNameWithExtension);
//        		utils.put("public_id", fileNameWithoutExtension + uniqueCode);
//        		
//        		Map<String, Object> uploadResult = cloudinary
//        				.uploader()
//        				.upload(file.getBytes(), utils);
//        		
//        		//getting url to access the file
//        		String url = (String) uploadResult.get("secure_url");
//        		log.info("File uploaded with name {}", fileNameWithExtension);
//        		fileUrls.add(url);
//        		
//        	}
//        	
//        	
//            System.out.println("upload from CloudinaryFileHandler");
//            return new CodeResponse(uniqueCode);
//
//        }  catch(IOException e) {
//        	log.error("Deleting due to error while uploading.");
//        	delete(uniqueCode, fileUrls);
//        	
//        }
//		return null;
//   
//	}
	
	@Override
	public CodeResponse upload(MultipartFile[] multipartFiles, String folderName) {
	    // utils needed while uploading the files.
	    Map<Object, Object> utils = new HashMap<>();
	    utils.put("folder", folderName);
	    utils.put("use_filename", true);
	    utils.put("unique_filename", false);
	    utils.put("type", "upload");
	    utils.put("resource_type", "raw");
	    utils.put("access_mode", "public");

	    // list to store the uploaded files urls.
	    List<String> fileUrls = new ArrayList<>();
	    String uniqueCode = generateUniqueCode();

	    try {
	        for(MultipartFile file: multipartFiles) {
	            String fileNameWithExtension = file.getOriginalFilename();
	            String fileNameWithoutExtension = getFileNameWithoutExtension(fileNameWithExtension);
	            String extension = getFileExtension(fileNameWithExtension);
	            
	            // Include extension in the public_id to preserve it
	            utils.put("public_id", fileNameWithoutExtension + uniqueCode + extension);

	            Map<String, Object> uploadResult = cloudinary
	                .uploader()
	                .upload(file.getBytes(), utils);

	            //getting url to access the file
	            String url = (String) uploadResult.get("secure_url");
	            log.info("File uploaded with name {}", fileNameWithExtension);
	            fileUrls.add(url);
	        }

	      
	        System.out.println("upload from CloudinaryFileHandler");
	        return new CodeResponse(uniqueCode);

	    } catch(IOException e) {
	        log.error("Deleting due to error while uploading.");
	        delete(uniqueCode, fileUrls);
	       
	    }
	    return null;
	}
	
	@Override
	public void delete(String uniqueCode, List<String> fileUrls) {
		try {
			for(String url: fileUrls) {
				String publicId = extractPublicIdFromUrl(url);
				
				Map<Object, Object> result = cloudinary
						.uploader()
						.destroy(publicId, ObjectUtils.emptyMap());
				
				if(!("ok".equals(result.get("result")))) {
					throw new Exception("Failed to delete file from Cloudinary: " + url);
				}
				 log.info("File with url: {} deleted", url);
	                System.out.println("delete from CloudinaryFileHandler");
			}
			
			 try {
	                // Fetch list of uploaded resources
	                Map result = cloudinary.api().resources(ObjectUtils.emptyMap());

	                // Print uploaded resources
	                System.out.println("cloudinary uploads "+result);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            
	            System.out.println("delete from ServerFileHandler");
		} catch(Exception e) {
			log.error("\"Failed to delete files from Cloudinary:" + e);
			
		}
		
	}
	
	 @Override
	  public Path getFiles(String uniqueCode) {
	  	System.out.println("getFiles from CloudinaryFileHandler");
	      return null;
	  }
	  
		

	// Helper method to extract file extension
	private String getFileExtension(String filename) {
	    if (filename == null || filename.isEmpty()) {
	        return "";
	    }
	    int lastDotIndex = filename.lastIndexOf('.');
	    if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
	        return "";
	    }
	    return filename.substring(lastDotIndex); // includes the dot (e.g., ".pdf")
	}

	
	  private String extractPublicIdFromUrl(String secureUrl) {
	        String[] parts = secureUrl.split("/");
	        if (parts.length < 2) {
	            log.error("Invalid Cloudinary URL: " + secureUrl);
	        }

	        String publicID = getPublicID(parts);
	        System.out.println("extractPublicIdFromUrl from CloudinaryFileHandler");
	        return publicID;
	    }
	  
	  
	  private String getPublicID(String[] parts) {
	        String publicIdWithExtension = parts[parts.length - 1];
	        String folderName = parts[parts.length - 2];

	        int lastDotIndex = publicIdWithExtension.lastIndexOf('.');
	        String publicIdWithOutExtension = (lastDotIndex == -1) ? publicIdWithExtension : publicIdWithExtension.substring(0, lastDotIndex);
	        System.out.println("getPublicID from CloudinaryFileHandler");
	        return folderName + "/" + publicIdWithOutExtension;
	    }
	
	
	private String generateUniqueCode() {
		System.out.println("generateUniqueCode from CloudinaryFileHandler");
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
	}
	
	private String getFileNameWithoutExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            originalFilename = originalFilename
                    .substring(0, originalFilename.lastIndexOf('.'));
        }
        System.out.println("getFileNameWithoutExtension from CloudinaryFileHandler");
        return originalFilename;
    }
	
	
}
