package com.clypt.clypt_backend.services;


import java.util.List;
import com.clypt.clypt_backend.entity.UrlMapping;

/**
 * UrlMappingService is the class associated to service the UrlMapping entity.
 *
 */
public interface UrlMappingService {

    /**
     * The save method takes a unique code and a list of urls. <br>
     * It stores the UrlMapping entity in the database.
     */
    UrlMapping save(String uniqueCode, List<String> urls, String fileExtension);

    /**
     * The get method takes the unique code. <br>
     * It retrieves the UrlMapping associated with the code.
     */
    UrlMapping get(String uniqueCode);

	
}