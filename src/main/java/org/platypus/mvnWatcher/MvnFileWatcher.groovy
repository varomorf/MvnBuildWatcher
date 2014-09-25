package org.platypus.mvnWatcher

import org.apache.commons.lang3.StringUtils;

/**
 * File that watches a file with the text from a Maven build and 
 * extracts build's status data from it
 * 
 * @author alfergon
 *
 */
class MvnFileWatcher {

	// Constants -----------------------------------------------------

	static final String startOfList = '[INFO] Reactor Build Order:'
	static final String endOfList = '[INFO] ------------------------------------------------------------------------'
	static final String empty = '[INFO]'
	static final String infoPart = '[INFO] '
	static final String buildingPart = '[INFO] Building '
	static final String BUILD_SUCCESS = '[INFO] BUILD SUCCESS'
	static final String jarPart = '.jar'

	// Attributes ----------------------------------------------------

	/**The file wiht the build data to be watched*/
	File file

	/**Flag for marking whether the watcher is reading the modules list or not*/
	boolean onList = false

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	/**
	 * Analyzes the file from a Maven build extracting the modules being built and the actual
	 * build status for each one of it and returns an object for the status of the whole build.
	 * 
	 * @return an object holding the status of a Maven build
	 */
	public MvnBuildStatus getStatusData(){
		MvnBuildStatus status = new MvnBuildStatus()
		String text = file.text
		// add all modules names to the Maven build status object
		text.eachLine addModuleToBeBuilt.curry(status)
		// change status of the modules
		text.eachLine setBuildingModule.curry(status)
		// check if the complete build has been completed
		if(text.contains(BUILD_SUCCESS)){
			status.buildCorrect = true
		}
		return status
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
	 * @param lineNum The number of the line (for easy use with eachLine)
	 */
	def addModuleToBeBuilt = { MvnBuildStatus status, String line, int lineNum  ->
		if(line == endOfList){
			onList = false
		}
		if(onList && line != empty){
			String moduleName = line - infoPart
			if(StringUtils.isNotBlank(moduleName)){
				status.addNewModule(moduleName)
			}
		}
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
	 * @param lineNum The number of the line (for easy use with eachLine)
	 */
	def setBuildingModule = { MvnBuildStatus status, String line, int lineNum  ->
		// add only if not on modules name, the line containg the Building part and the line does
		// not refer to a jar being built
		if(onList == false && line.contains(buildingPart) && !line.contains(jarPart)){
			status.setBuildingModule(cleanBuiltEntryText(line))
		}
	}

}
