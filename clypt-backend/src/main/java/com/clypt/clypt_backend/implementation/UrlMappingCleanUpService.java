package com.clypt.clypt_backend.implementation;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;
import com.clypt.clypt_backend.entity.UrlMapping;
import com.clypt.clypt_backend.handler.FileHandler;
import com.clypt.clypt_backend.repository.UrlMappingRepository;

import jakarta.transaction.Transactional;


/**
 * CleanUp service uses a cron job to delete expired URLs and their associated files.
 */
@Component
public class UrlMappingCleanUpService {

	@Autowired
	private UrlMappingRepository urlMappingRepository;

	@Autowired
	private FileHandler fileHandler;

	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	@Scheduled(cron = "0 * * * * *")
	@Transactional
	public void deleteExpiredUrlMapping() {
		log.info("Running Clean Up Service...");
		List<UrlMapping> expiredUrlMappings = urlMappingRepository.findByExpiryDateBefore(LocalDateTime.now());

		for (UrlMapping urlMapping : expiredUrlMappings) {
			fileHandler.delete(urlMapping.getUniqueCode(), urlMapping.getUrls());
			urlMappingRepository.delete(urlMapping);
		}
		
		log.warn("{} mappings have been cleaned!", expiredUrlMappings.size());
	}

}
