package org.platypus.mvnWatcher

import org.apache.commons.lang3.StringUtils;

/**
 * Watches and analyces the outpu from a Maven build and extracts build's status data from it
 * 
 * @author alfergon
 */
class MvnBuildWatcher implements MvnBuildOutputListener{

	// Constants -----------------------------------------------------

	static final String startOfList = '[INFO] Reactor Build Order:'
	static final String endOfList = '[INFO] ------------------------------------------------------------------------'
	static final String infoPart = '[INFO] '
	static final String buildingPart = '[INFO] Building '
	static final String BUILD_SUCCESS = '[INFO] BUILD SUCCESS'
	static final String jarPart = '.jar'

	// Attributes ----------------------------------------------------

	/**Flag for marking whether the watcher is reading the modules list or not*/
	boolean onList = false

	/**Flag for marking whether the watcher has read the modules list or not*/
	boolean listRead = false

	/**The status of the Maven build being watched*/
	MvnBuildStatus status

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
	public void recieveOutput(String line) {
		analyzeLine status, line
		if(line.contains(BUILD_SUCCESS)){
			status.buildCorrect = true
		}
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
		String ret = builtEntryText - buildingPart
		// remove version (from the las white space to the end of the line)
		int lastWhite = ret.lastIndexOf(' ')
		return ret - ret[lastWhite..-1]
	}

	// Inner classes -------------------------------------------------

	/**
	 * Analyzes the passed line and checks (depending on the status of the
	 * {@link MvnFileWatcher#onList} flag) whether it includes the name of a module in the modules'
	 * list or not, adding it to a collection in positive case.
	 *
	 * @param col The MvnBuildStatus in which to add the module name on positive cases
	 * @param line The line to analyze
	 */
	def addModuleToBeBuilt = { MvnBuildStatus status, String line  ->
		// check when the list of modules has ended
		if(onList && line == endOfList){
			onList = false
			listRead = true
		}
		if(onList){
			String moduleName = line - infoPart
			// append only valid module names from the modules' list
			if(StringUtils.isNotBlank(moduleName)){
				status.addNewModule(moduleName)
			}
		}
		// check when the start of the list of modules begin
		if(line == startOfList){
			onList = true
		}
	}

	/**
	 * Analyzes the passed line and checks (depending on the status of the 
	 * {@link MvnFileWatcher#onList} flag) whether it includes the name of a module being built
	 * or not, adding it to a collection in positive case.
	 *  
	 * @param status The MvnBuildStatus in which to set the module name on positive cases
	 * @param line The line to analyze
	 */
	def setBuildingModule = { MvnBuildStatus status, String line  ->
		if(line.contains(buildingPart) && !line.contains(jarPart)){
			status.setBuildingModule(cleanBuiltEntryText(line))
		}
	}

	/**
	 * Analyzes the passed line and checks (depending on the status of the
	 * {@link MvnFileWatcher#onList} flag) whether it includes the name of a new module to be built,
	 * the name of a module that is being built or not usefull information, updating the passed
	 * Maven build status accordingly.
	 *
	 * @param status The MvnBuildStatus to update in positive cases
	 * @param line The line to analyze
	 */
	def analyzeLine = { MvnBuildStatus status, String line  ->
		if(listRead == false){
			addModuleToBeBuilt(status, line)
		}else{
			setBuildingModule(status, line)
		}
		// check for finalization
		if(line.contains(BUILD_SUCCESS)){
			status.buildCorrect = true
		}
	}

}
