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
	private ParallelStrategy parallelStrategy;

	@Autowired
	private SequentialEncryptionStrategy sequentialEncryptionStrategy;

	@Autowired
	private ParallelEncryptionStrategy parallelEncryptionStrategy;

	public UploadStrategy selectStrategy(int numberOfFiles) {
		if (numberOfFiles > parallelStrategyThreshold) {
			return encryptionEnabled ? parallelEncryptionStrategy : parallelStrategy;
		} else {
			return encryptionEnabled ? sequentialEncryptionStrategy : sequentialStrategy;
		}
	}
}
