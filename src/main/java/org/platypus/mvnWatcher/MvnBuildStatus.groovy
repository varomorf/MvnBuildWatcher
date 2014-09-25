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
	List<MvnModuleBuildStatus> modulesStatus = []

	/**Flag to mark whether the build is correct or not*/
	boolean buildCorrect = false

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	/**
	 * Add a new module to the modules of this build (this will NOT look for duplicates) with 
	 * {@link MvnModuleBuildStatus#WAITING} status
	 * 
	 * @param moduleName the name of the module to be added
	 */
	public void addNewModule(String moduleName){
		modulesStatus.add new MvnModuleBuildStatus(moduleName: moduleName)
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
