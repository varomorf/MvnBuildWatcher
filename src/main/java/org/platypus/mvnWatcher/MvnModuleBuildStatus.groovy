package org.platypus.mvnWatcher

/**
 * Bean for storing the build status of a Maven Module
 * 
 * @author alfergon
 *
 */
class MvnModuleBuildStatus {

	// Constants -----------------------------------------------------

	static final WAITING = 'Waiting'
	static final BUILDING = 'Building ...'
	static final BUILT = 'Built'

	// Attributes ----------------------------------------------------

	/**The name of the module*/
	String moduleName

	/**The status of the module (defaults to WAITING)*/
	String status = WAITING

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
