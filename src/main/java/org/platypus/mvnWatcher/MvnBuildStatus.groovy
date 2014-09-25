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

	/**
	 * Set the status for the passed module (which mut be contained in this Maven build) as 
	 * {@link MvnModuleBuildStatus#BUILDING}. As Maven builds are done in an iterative manner, 
	 * when a module is being built, the module before it will pass to 
	 * {@link MvnModuleBuildStatus#BUILT} status.
	 * 
	 * @param moduleName the name of the module currently being built
	 */
	public void setBuildingModule(String moduleName){
		// special case for first module being built
		if(modulesStatus[0].isWaiting()){
			modulesStatus[0].setBuilding()
		}else{
			modulesStatus.find{it.moduleName == moduleName}.setBuilding()
			modulesStatus.find{it.isBuilding()}.setBuilt()
		}
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
