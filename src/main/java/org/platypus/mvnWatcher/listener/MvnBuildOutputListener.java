package org.platypus.mvnWatcher.listener;

import org.platypus.mvnWatcher.model.MvnBuild;

/**
 * Defines a listener for the output of a Maven build
 *
 * @author alfergon
 */
public interface MvnBuildOutputListener {

	/**
	 * Receives a line from the output of the Maven build
	 *
	 * @param line the line to receive
	 */
	void receiveOutput(String line);

	/**
	 * Receives the build that is being executed
	 *
	 * @param build the build that is being executed
	 */
	void receiveBuildLaunched(MvnBuild build);

}
