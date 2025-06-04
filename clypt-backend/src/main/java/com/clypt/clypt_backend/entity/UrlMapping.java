package com.clypt.clypt_backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The UrlMapping class represents a mapping entity that stores information
 * about unique URL codes and associated URLs with an expiration date. <br>
 * It is used to store a list of URLs mapped to a unique code.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "url_mapping")
public class UrlMapping {
	@Id
	@Column(unique = true, nullable = false)
	private String uniqueCode;
	
	@ElementCollection
	@CollectionTable(name = "url_list", joinColumns = @JoinColumn(name = "url_mapping_id"))
	private List<String> urls;
	
	@Column(nullable = false)
	private String fileExtension;
	
	@Column(nullable = false)
	private LocalDateTime expiryDate;
	
	
	

}
