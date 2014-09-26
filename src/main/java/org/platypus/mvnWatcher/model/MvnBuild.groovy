package org.platypus.mvnWatcher.model

/**
 * Defines a build for maven with the command to be launched and the directory in which to be run
 *  
 * @author alfergon
 *
 */
class MvnBuild {

	// Constants -----------------------------------------------------
	
	static final String MVN = 'mvn.bat '
	
	static final String MVNCIS = 'clean install -DskipTests '

	// Attributes ----------------------------------------------------
	
	/**The command that will be executed*/
	String command = MVN
	
	/**Options for the Maven build command*/
	String options = MVNCIS
	
	/**The directory in which the command will be executed*/
	File directory

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------
	
	/**
	 * Returns the whole command that must be executed
	 * @return the whole command that must be executed
	 */
	public String getCommand(){
		return MVN + options
	}
	
	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
