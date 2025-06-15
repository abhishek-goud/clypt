package com.clypt.clypt_backend.implementation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clypt.clypt_backend.entity.UrlMapping;
import com.clypt.clypt_backend.exceptions.TimeExpiredException;
import com.clypt.clypt_backend.repository.UrlMappingRepository;
import com.clypt.clypt_backend.services.UrlMappingService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UrlMappingServiceImpl implements UrlMappingService {

	@Autowired
	UrlMappingRepository mappingRepository;

	@Override
	@Transactional
	public UrlMapping save(String uniqueCode, List<String> urls, List<String> fileExtension) {
		// TODO Auto-generated method stub
		UrlMapping urlMapping = UrlMapping.builder().uniqueCode(uniqueCode).urls(urls).fileType(fileExtension)
				.expiryDate(LocalDateTime.now().plusHours(18)).build();

		UrlMapping savedUrlMapping = mappingRepository.save(urlMapping);
		return savedUrlMapping;
	}

	@Override
	@Transactional
	public UrlMapping get(String uniqueCode) {
		UrlMapping urlMapping = mappingRepository.findByUniqueCode(uniqueCode)
				.orElseThrow(() -> new EntityNotFoundException("File not found with code: " + uniqueCode));

		if (urlMapping.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new TimeExpiredException(String.format("The code %s has already expired", uniqueCode));
		}

		return urlMapping;

	}

}
