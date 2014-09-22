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
			def row = ['name':it]
			if(built.contains(it)){
				if(built[-1] == it){
					row.put 'status', 'Building'
				}else{
					row.put 'status', 'Built'
				}
			}
			return row
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
				list.add(it - buildingPart)
			}
		}
		list = list.collect{
			int lastWhite = it.lastIndexOf(' ')
			return it - it[lastWhite..-1]
		}
		return list
	}

	// Inner classes -------------------------------------------------

}
