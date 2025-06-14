package com.clypt.clypt_backend.strategy.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The DeleteStrategySelector provides an interface to select one of the delete strategies based on the number of files.
 */

@Component
public class DeleteStrategySelector {
	
	@Value("${parallel.threshold}")
	private int parallelStrategyThreshold;
	
	@Autowired
	private SequentialDeleteStrategy sequentialDeleteStrategy;
	
	@Autowired
	private ParallelDeleteStrategy parallelDeleteStrategy;
	
	public DeleteStrategy selectStrategy(int numberOfFiles) {
		if(numberOfFiles >= parallelStrategyThreshold) {
			return parallelDeleteStrategy;
		}
		else {
			return sequentialDeleteStrategy;
		}
	}

}
