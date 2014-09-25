package org.platypus.mvnWatcher

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
		List<String> modules = getList(text)
		List<String> built = getBuilt(text)
		// create list of module statuses for each module
		status.modulesStatus = modules.collect{
			def module = new MvnModuleBuildStatus(moduleName:it)
			if(built.contains(it)){
				if(built[-1] == it){
					module.status = MvnModuleBuildStatus.BUILDING
				}else{
					module.status = MvnModuleBuildStatus.BUILT
				}
			}
			return module
		}
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
	 * Extracts the list of modules being built on a Maven build from a text
	 * 
	 * @param text the text from which to extract the list of modules
	 * @return the list of modules (can be empty but never null)
	 */
	private List<String> getList(String text){
		List<String> list = []
		text.eachLine addModuleToBeBuiltToCollection.curry(list)
		return list
	}

	private List<String> getBuilt(String text){
		List<String> list = []
		text.eachLine {
			if(it.contains(buildingPart) && !it.contains(jarPart)){
				list.add(cleanBuiltEntryText(it))
			}
		}
		return list
	}

	private String cleanBuiltEntryText(String builtEntryText){
		String ret = builtEntryText - buildingPart
		int lastWhite = ret.lastIndexOf(' ')
		return ret - ret[lastWhite..-1]
	}

	/**
	 * Analyzes the passed line and checks (depending on the status of the 
	 * {@link MvnFileWatcher#onList} flag) whether it includes the name of a module in the modules'
	 * list or not, adding it to a collection in positive case.
	 *  
	 * @param col The collection in which to add the module name on positive cases
	 * @param line The line to analyze
	 * @param lineNum The number of the line (for easy use with eachLine)
	 */
	def addModuleToBeBuiltToCollection = { Collection<String> col, String line, int lineNum  ->
		if(line == endOfList){
			onList = false
		}
		if(onList && line != empty){
			String moduleName = line - infoPart
			if(moduleName != ''){
				col.add(moduleName)
			}
		}
		if(line == startOfList){
			onList = true
		}
	}

	// Inner classes -------------------------------------------------

}
