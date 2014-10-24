package org.platypus.mvnWatcher.model

/**
 * File wrapper for Maven build project's files
 *
 * Can be treated as a collection that returns 
 *
 * @author alfergon
 *
 */
class MavenBuildProjectFile {

	static final String OPTIONS_SEPARATOR = ' '

	// Constants -----------------------------------------------------

	static final String PARTS_SEPARATOR = ';'

	// Attributes ----------------------------------------------------

	/**The actual project file*/
	File file

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	/**
	 * Returns a list with al the build commands for this maven build project file
	 *
	 * @return a list with al the build commands for this maven build project file
	 */
	public List<MvnBuild> getBuilds() {
		List<MvnBuild> list = []
		file.eachLine {
			def parts = it.tokenize PARTS_SEPARATOR
			def goals = parts[1].tokenize(OPTIONS_SEPARATOR)
			list.add new MvnBuild(directory: new File(parts[0]), goals: goals)
		}
		return list
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
