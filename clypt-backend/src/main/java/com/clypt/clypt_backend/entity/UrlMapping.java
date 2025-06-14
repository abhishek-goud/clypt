package com.clypt.clypt_backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The UrlMapping class represents a mapping entity that stores a unique code,
 * a list of associated URLs, their file types, and an expiration date.
 * It is used to store multiple URLs under a single unique code.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "url_mapping")
public class UrlMapping {

    @Id
    @Column(name = "unique_code", unique = true, nullable = false) 
    private String uniqueCode;

    @ElementCollection
    @CollectionTable(name = "url_list", joinColumns = @JoinColumn(name = "url_mapping_id"))
    private List<String> urls;

    @ElementCollection
    @CollectionTable(name = "file_type_list", joinColumns = @JoinColumn(name = "url_mapping_id"))
    private List<String> fileType;

    @Column(name = "expiry_date", nullable = false) 
    private LocalDateTime expiryDate;
}

