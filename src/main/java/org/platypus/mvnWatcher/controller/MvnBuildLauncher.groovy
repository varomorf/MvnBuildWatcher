package org.platypus.mvnWatcher.controller

import org.platypus.mvnWatcher.listener.MvnBuildOutputListener;
import org.platypus.mvnWatcher.model.MvnBuild

/**
 * Class for launching Maven builds. This can be done for a specific folder or for each folder on
 * a project file.
 * 
 * @author alfergon
 *
 */
class MvnBuildLauncher {

	// Constants -----------------------------------------------------

	static final String SEPARATOR = ';'

	// Attributes ----------------------------------------------------

	/**The thread on which the build is executed*/
	Thread buildThread

	/**The stream in which the output of the build is being redirected*/
	BufferedReader buildStream

	/**List of the listeners for the output of the Maven build*/
	List<MvnBuildOutputListener> listeners = []

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	/**
	 * Launches the passed build command on the specified directory and calls listeners passing
	 * each of the lines of the output of said command
	 *  
	 * @param build the Maven build to be launched
	 */
	public void launchBuild(final MvnBuild build){
		buildThread = Thread.start{
			Process buildProcess = Runtime.getRuntime().exec(build.command, null, build.directory)
			buildStream = new BufferedReader(new InputStreamReader(buildProcess.getInputStream()))
			String s
			while((s = buildStream.readLine()) != null){
				// notify listeners
				listeners.each{ it.recieveOutput(s) }
			}
		}
	}

	/**
	 * Launches a new build for each line of the project file (that must contain a directory and
	 * a Maven build command separated by ;) 
	 *  
	 * @param buildProjectFile the maven build project file
	 */
	public void launchBuildProject(final File buildProjectFile){
		buildProjectFile.eachLine{
			def parts = it.tokenize SEPARATOR
			MvnBuild build = new MvnBuild(directory:new File(parts[0]), options:parts[1])
			launchBuild(build)
			while(buildThread.isAlive()){
				// wait for a second before checking again
				sleep(1000)
			}
		}
	}

	/**
	 * Adds a new listener for the build's output
	 * @param listener the new listener
	 */
	public void addListener(MvnBuildOutputListener listener){
		listeners.add listener
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
