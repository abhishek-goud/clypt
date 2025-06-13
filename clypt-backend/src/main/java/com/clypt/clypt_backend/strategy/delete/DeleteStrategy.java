package com.clypt.clypt_backend.strategy.delete;

import java.util.List;

public interface DeleteStrategy {
	
	void deleteFiles(List<String> fileUrls);

}
