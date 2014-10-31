package org.platypus.mvnWatcher.model

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

	/**The name of the module that has caused the build to fail*/
	String failedModule

	/**The goal that failed*/
	String failedGoal

	/**The reason why the goal failed*/
	String failReason

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	/**
	 * Add a new module to the modules of this build (this will NOT look for duplicates) with 
	 * {@link MvnModuleBuildStatus#WAITING} status
	 *
	 * @param moduleName the name of the module to be added
	 */
	public void addNewModule(String moduleName) {
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
	public void setBuildingModule(String moduleName) {
		// check also for waiting status to avoid duplicates
		int index = modulesStatus.findIndexOf { it.moduleName == moduleName && it.waiting }
		switch (index) {
			case -1:
				// module is not yet included -> include as Building
				addNewModule(moduleName)
				modulesStatus[-1].setBuilding()
				break
			case 0:
				// special case for first module being built
				modulesStatus[0].setBuilding()
				break
			default:
				// set current as Building and previous as Built
				modulesStatus[index].setBuilding()
				modulesStatus[index - 1].setBuilt()
		}
	}

	/**
	 * Returns whether the build has failed or not.
	 * @return <code>true</code> the build has failed. <code>false</code> otherwise.
	 */
	public boolean isFailed(){
		failedModule
	}

	@Override
	String toString() {
		"${modulesStatus.size()} modules"
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
