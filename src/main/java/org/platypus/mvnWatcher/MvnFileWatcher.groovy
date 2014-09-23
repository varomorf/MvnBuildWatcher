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
	static final String jarPart = '.jar'

	// Attributes ----------------------------------------------------

	/**The file wiht the build data to be watched*/
	File file

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	public List<Map<String, String>> getStatusData(){
		String text = file.text
		List<String> list = getList(text)
		List<String> built = getBuilt(text)
		list.collect{
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
	}

	public void refreshData(){

	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	private List<String> getList(String text){
		boolean start = false
		List<String> list = []
		text.eachLine {
			if(it == endOfList){
				start = false
			}
			if(start && it != empty){
				list.add(it - infoPart)
			}
			if(it == startOfList){
				start = true
			}
		}
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

	// Inner classes -------------------------------------------------

}
