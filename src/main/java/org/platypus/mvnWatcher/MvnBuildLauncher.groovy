package org.platypus.mvnWatcher

/**
 * Class for launching Maven builds. This can be done for a specific folder or for each folder on
 * a project file.
 * 
 * @author alfergon
 *
 */
class MvnBuildLauncher {

	// Constants -----------------------------------------------------

	static final String MVNCIS = 'mvn.bat clean install -DskipTests'

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
	 * @param command the command to be launched
	 * @param buildDir the directory on which to launch the command
	 */
	public void launchBuild(String command, File buildDir){
		buildThread = Thread.start{
			Process buildProcess = Runtime.getRuntime().exec("$command", null, buildDir)
			buildStream = new BufferedReader(new InputStreamReader(buildProcess.getInputStream()))
			String s
			while((s = buildStream.readLine()) != null){
				// notify listeners
				listeners.each{ it.recieveOutput(s) }
			}
		}
	}
	
	/**
	 * Launches the passed build command on each of the directories specified on the build project
	 * file.
	 * 
	 * @param command the command to be launched
	 * @param buildProjectFile a file with one line for each directory in which to launch a build
	 */
	public void launchBuildProject(String command, File buildProjectFile){
		buildProjectFile.eachLine{
			launchBuild(command, new File(it))
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
