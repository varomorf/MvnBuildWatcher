package org.platypus.mvnWatcher

/**
 * Class for launching Maven builds under a folder
 * 
 * @author alfergon
 *
 */
class MvnBuildLauncher {

	// Constants -----------------------------------------------------

	static final String MVNCIS = 'mvn.bat clean install -DskipTests'

	static final String REDIRECT = '>'

	static final String DEFAULT_FILENAME = 'mvnBuild.output'

	// Attributes ----------------------------------------------------

	/**The process of the current build*/
	Process buildProcess

	/**The thread on which the build is executed*/
	Thread buildThread

	/**The stream in which the output of the build is being redirected*/
	BufferedReader buildStream

	/**List of the listeners for the output of the Maven build*/
	List<MvnBuildOutputListener> listeners = []

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	public void launchBuild(String command, File buildDir){
		buildThread = Thread.start{
			buildProcess = Runtime.getRuntime().exec("$command", null, buildDir)
			buildStream = new BufferedReader(new InputStreamReader(buildProcess.getInputStream()))
			String s
			while((s = buildStream.readLine()) != null){
				listeners.each{ it.recieveOutput(s) }
			}
		}
	}
	
	public void addListener(MvnBuildOutputListener listener){
		listeners.add listener
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
