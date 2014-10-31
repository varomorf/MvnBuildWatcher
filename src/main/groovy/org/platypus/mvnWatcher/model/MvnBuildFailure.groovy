package org.platypus.mvnWatcher.model

/**
 * Holds information from a failure in a Maven build.
 *
 * @author alfergon
 */
class MvnBuildFailure {

	// Constants -----------------------------------------------------

	// Attributes ----------------------------------------------------

	/**The name of the module that has caused the build to fail*/
	public static final def FAILED_MODULE_REGEX = /\[ERROR\] Failed to execute goal (.+) on project (.+): (.+)/
	String failedModule

	/**The goal that failed*/
	String failedGoal

	/**The reason why the goal failed*/
	String failReason

	// Static --------------------------------------------------------

	/**
	 * Creates a new MvnBuildFailure from the passed line as long as the line represents a failure from a build
	 * @param line the line from which to extract the failure data
	 * @return the new MvnBuildFailure or <code>null</code> if the line was not from a build failure
	 */
	static MvnBuildFailure createFromLine(String line) {
		def matcher = (line =~ FAILED_MODULE_REGEX)
		if (matcher.find()) {
			MvnBuildFailure failure = new MvnBuildFailure()
			failure.failedGoal = matcher.group(1)
			failure.failedModule = matcher.group(2)
			failure.failReason = matcher.group(3)
			return failure
		}
		return null
	}

	@Override
	String toString() {
		"Build failed on module $failedModule when performing $failedGoal because of $failReason"
	}
// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------
}
