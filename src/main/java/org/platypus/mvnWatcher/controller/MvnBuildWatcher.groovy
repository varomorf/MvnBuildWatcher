package org.platypus.mvnWatcher.controller

import org.apache.commons.lang3.StringUtils;
import org.platypus.mvnWatcher.listener.MvnBuildOutputListener;
import org.platypus.mvnWatcher.listener.MvnBuildStatusListener;
import org.platypus.mvnWatcher.model.MvnBuild;
import org.platypus.mvnWatcher.model.MvnBuildStatus;

/**
 * Watches and analyzes the output from a Maven build and extracts builds' status data from it
 *
 * @author alfergon
 */
class MvnBuildWatcher implements MvnBuildOutputListener{

	// Constants -----------------------------------------------------

	static final String START_OF_LIST = '[INFO] Reactor Build Order:'
	static final String END_OF_LIST = '[INFO] ------------------------------------------------------------------------'
	static final String INFO_PART = '[INFO] '
	static final String BUILDING_PART = '[INFO] Building '
	static final String BUILD_SUCCESS = '[INFO] BUILD SUCCESS'
	static final def PACKAGE_FILE_PART = /.+\.[tj]ar.*/

	// Attributes ----------------------------------------------------

	/**Flag for marking whether the watcher is reading the modules list or not*/
	boolean onList = false

	/**Flag for marking whether the watcher has read the modules list or not*/
	boolean listRead = false

	/**The status of the Maven build being watched*/
	MvnBuildStatus status

	/**The listener for the changes on this status*/
	MvnBuildStatusListener statusListener

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	/**
	 * Start a new build to be watched
	 */
	public void newBuild(){
		status = new MvnBuildStatus()
		listRead = false
	}

	@Override
	public void receiveOutput(String line) {
		analyzeLine status, line
		if(line.contains(BUILD_SUCCESS)){
			status.buildCorrect = true
		}
		statusListener.receiveStatus(status)
	}

	@Override
	public void receiveBuildLaunched(MvnBuild build) {
		// NOOP
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	/**
	 * Cleans the building part (version included) from a string (that it's supposed to have both
	 * string on it
	 *
	 * @param builtEntryText the String to be cleaned
	 * @return the cleaned string
	 */
	private String cleanBuiltEntryText(String builtEntryText){
		String ret = builtEntryText - BUILDING_PART
		// remove version (from the las white space to the end of the line)
		int lastWhite = ret.lastIndexOf(' ')
		return ret - ret[lastWhite..-1]
	}

	// Inner classes -------------------------------------------------

	/**
	 * Analyzes the passed line and checks (depending on the status of the
	 * {@link MvnBuildWatcher#onList} flag) whether it includes the name of a module in the modules'
	 * list or not, adding it to a collection in positive case.
	 *
	 * @param col The MvnBuildStatus in which to add the module name on positive cases
	 * @param line The line to analyze
	 */
	def addModuleToBeBuilt = { MvnBuildStatus status, String line  ->
		// check when the list of modules has ended
		if(onList && line == END_OF_LIST){
			onList = false
			listRead = true
		}
		if(onList){
			String moduleName = line - INFO_PART
			// append only valid module names from the modules' list
			if(StringUtils.isNotBlank(moduleName)){
				status.addNewModule(moduleName)
			}
		}
	}

	/**
	 * Analyzes the passed line and checks whether it includes the name of a module being built
	 * or not, adding it to a collection in positive case.
	 *
	 * @param status The MvnBuildStatus in which to set the module name on positive cases
	 * @param line The line to analyze
	 */
	def setBuildingModule = { MvnBuildStatus status, String line  ->
		if(line.contains(BUILDING_PART) && !(line ==~ PACKAGE_FILE_PART)){
			status.setBuildingModule(cleanBuiltEntryText(line))
		}
	}

	/**
	 * Analyzes the passed line and checks (depending on the status of the
	 * {@link MvnBuildWatcher#listRead} flag) whether it includes the name of a new module to be built,
	 * the name of a module that is being built or not useful information, updating the passed
	 * Maven build status accordingly.
	 *
	 * @param status The MvnBuildStatus to update in positive cases
	 * @param line The line to analyze
	 */
	def analyzeLine = { MvnBuildStatus status, String line  ->
		if(!listRead){
			addModuleToBeBuilt(status, line)
		}
		setBuildingModule(status, line)
		// check when the start of the list of modules begin
		if(line == START_OF_LIST){
			onList = true
			listRead = false
		}
		// check for finalization
		if(line.contains(BUILD_SUCCESS)){
			status.buildCorrect = true
		}
	}

}
