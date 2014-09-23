package org.platypus.mvnWatcher

import org.apache.commons.io.FileUtils;

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

	/**The file in which the output from the build is being redirected*/
	File outputFile

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	public void launchBuild(String command, String outputFileName, File buildDir){
		buildThread = Thread.start{
			outputFile = new File(buildDir, outputFileName)
			buildProcess = Runtime.getRuntime().exec("$command $REDIRECT $outputFileName", null, buildDir)
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(buildProcess.getInputStream()))
			String s
			while((s = stdInput.readLine()) != null){
				outputFile.text += s
			}
		}
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
