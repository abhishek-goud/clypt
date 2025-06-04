package com.clypt.clypt_backend.implementation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clypt.clypt_backend.entity.UrlMapping;
import com.clypt.clypt_backend.repository.UrlMappingRepository;
import com.clypt.clypt_backend.services.UrlMappingService;

@Service
public class UrlMappingServiceImpl implements UrlMappingService {

	@Autowired
	UrlMappingRepository mappingRepository;

	@Override
	public UrlMapping save(String uniqueCode, List<String> urls) {
		// TODO Auto-generated method stub
		UrlMapping urlMapping = UrlMapping
				.builder()
                .uniqueCode(uniqueCode)
                .urls(urls)
                .expiryDate(LocalDateTime.now())
                .build();
		
		UrlMapping savedUrlMapping = mappingRepository.save(urlMapping);
		return savedUrlMapping;
	}

	@Override
	public UrlMapping get(String uniqueCode) {
		 UrlMapping urlMapping = mappingRepository.findByUniqueCode(uniqueCode)
	            .orElseThrow(() -> new RuntimeException("URL Mapping not found for code: " + uniqueCode));
		 
		 return urlMapping;
	  
	}

}
