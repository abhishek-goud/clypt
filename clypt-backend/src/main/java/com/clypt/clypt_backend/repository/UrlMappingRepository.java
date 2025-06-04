package com.clypt.clypt_backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clypt.clypt_backend.entity.UrlMapping;

/**
 * UrlMappingRepository acts as an interface to interact with the database.
 */
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
	
	/**
     * findByUniqueCode returns the UrlMapping associated with the code provided in the args.
     */
    Optional<UrlMapping> findByUniqueCode(String uniqueCode);
    
    /**
     * findByExpiryDateBefore returns the list of UrlMappings that have expiry date less than now.
     */
    @Query("SELECT u FROM url_mapping u WHERE u.expiryDate < :now")
    List<UrlMapping> findByExpiryDateBefore(@Param("now") LocalDateTime now);


	
}
