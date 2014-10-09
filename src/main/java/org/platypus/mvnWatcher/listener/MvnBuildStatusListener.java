package org.platypus.mvnWatcher.listener;

import org.platypus.mvnWatcher.model.MvnBuildStatus;

/**
 * Defines a listener for the status of a Maven build
 *
 * @author alfergon
 */
public interface MvnBuildStatusListener {

	/**
	 * Receives the current status of the Maven build
	 *
	 * @param status the status to receive
	 */
	void receiveStatus(MvnBuildStatus status);

}
