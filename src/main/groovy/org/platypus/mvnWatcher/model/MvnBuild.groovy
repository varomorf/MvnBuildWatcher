package org.platypus.mvnWatcher.model

import org.apache.maven.shared.invoker.DefaultInvocationRequest

/**
 * Defines a build for maven with the command to be launched and the directory in which to be run
 *
 * @author alfergon
 *
 */
class MvnBuild extends DefaultInvocationRequest {

	// Constants -----------------------------------------------------

	/**Default is build without tests*/
	static final List<String> MVNCIS = [
			'clean',
			'install',
			'-DskipTests'
	]

	/**Default name for pom file*/
	static final String POM = 'pom.xml'

	// Attributes ----------------------------------------------------

	/**The status of the build*/
	MvnBuildStatus status

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	/**
	 * Creates a new Maven Build object specifying the
	 */
	public MvnBuild() {
		setPomFileName(POM)
		setGoals(MVNCIS)
	}

	// Public --------------------------------------------------------

	/**
	 * Sets the directory in which the pom file will be located
	 * @param directory the directory in which the pom file will be located
	 */
	public void setDirectory(File directory) {
		setPomFile(new File(directory, pomFileName))
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
