package org.platypus.mvnWatcher.model

/**
 * Bean for storing the build status of a Maven Module
 *
 * @author alfergon
 *
 */
class MvnModuleBuildStatus {

	// Constants -----------------------------------------------------

	public static final WAITING = 'Waiting'
	public static final BUILDING = 'Building ...'
	public static final BUILT = 'Built'

	// Attributes ----------------------------------------------------

	/**The name of the module*/
	String moduleName

	/**The status of the module (defaults to WAITING)*/
	String status = WAITING

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	/**
	 * Is the module waiting to be built?
	 * @return
	 */
	public boolean isWaiting() {
		status == WAITING
	}

	/**
	 * Is the module currently being built?
	 * @return
	 */
	public boolean isBuilding() {
		status == BUILDING
	}

	/**
	 * Is the module already built?
	 * @return
	 */
	public boolean isBuilt() {
		status == BUILT
	}

	/**
	 * Set the module's status as {@link MvnModuleBuildStatus#BUILDING}
	 */
	public void setBuilding() {
		status = BUILDING
	}

	/**
	 * Set the module's status as {@link MvnModuleBuildStatus#BUILT}
	 */
	public void setBuilt() {
		status = BUILT
	}

	@Override
	String toString() {
		return "$moduleName : $status"
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
