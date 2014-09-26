package org.platypus.mvnWatcher;

/**
 * Defines a listener for the status of a Maven build
 * 
 * @author alfergon
 * 
 */
public interface MvnBuildStatusListener {

	/**
	 * Recieves the current status of the Maven build
	 * 
	 * @param status
	 *            the status to receive
	 */
	void recieveStatus(MvnBuildStatus status);

}
