package org.platypus.mvnWatcher

/**
 * Holds the status of a Maven Build
 * 
 * @author alfergon
 *
 */
class MvnBuildStatus {

	// Constants -----------------------------------------------------

	// Attributes ----------------------------------------------------

	/**List with the statuses of each of the modules of the build*/
	List<MvnModuleBuildStatus> modulesStatus

	/**Flag to mark whether the build is correct or not*/
	boolean buildCorrect

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
