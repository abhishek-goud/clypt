package com.clypt.clypt_backend.strategy.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UploadStrategySelector {
	@Value("${encryption.enabled}")
	private boolean encryptionEnabled;

	@Value("${parallel.threshold}")
	private Integer parallelStrategyThreshold;

	@Autowired
	private SequentialStrategy sequentialStrategy;

	@Autowired
	private SequentialEncryptionStrategy sequentialEncryptionStrategy;

	public UploadStrategy selectStrategy() {
		return sequentialStrategy;
	}

}
