package org.platypus.mvnWatcher.model

/**
 * Defines a build for maven with the command to be launched and the directory in which to be run
 *  
 * @author alfergon
 *
 */
class MvnBuild {

	// Constants -----------------------------------------------------
	
	static final String MVNCIS = 'mvn.bat clean install -DskipTests '

	// Attributes ----------------------------------------------------
	
	/**The command that will be executed*/
	String command = MVNCIS
	
	/**The directory in which the command will be executed*/
	File directory

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
