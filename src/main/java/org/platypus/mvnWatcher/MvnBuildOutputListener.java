package org.platypus.mvnWatcher;

/**
 * Defines a listener for the output of a Maven build
 * 
 * @author alfergon
 * 
 */
public interface MvnBuildOutputListener {

	/**
	 * Recieves a line from the output of the Maven build
	 * 
	 * @param line
	 *            the line to receive
	 */
	void recieveOutput(String line);

}
