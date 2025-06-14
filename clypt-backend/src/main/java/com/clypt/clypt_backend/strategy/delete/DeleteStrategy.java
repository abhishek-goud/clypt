package com.clypt.clypt_backend.strategy.delete;

import java.util.List;

/**
 * DeleteStrategy defines the interface for different upload strategies
 */
public interface DeleteStrategy {
	
	void deleteFiles(List<String> fileUrls);

}
