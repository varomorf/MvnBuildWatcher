package org.platypus.mvnWatcher.controller

import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.apache.maven.shared.invoker.InvocationOutputHandler
import org.apache.maven.shared.invoker.InvocationRequest
import org.apache.maven.shared.invoker.Invoker
import org.platypus.mvnWatcher.listener.MvnBuildOutputListener;
import org.platypus.mvnWatcher.model.MavenBuildProjectFile
import org.platypus.mvnWatcher.model.MvnBuild

import com.jezhumble.javasysmon.JavaSysMon

/**
 * Class for launching Maven builds. This can be done for a specific folder or for each folder on
 * a project file.
 * 
 * @author alfergon
 *
 */
class MvnBuildLauncher implements InvocationOutputHandler {

	// Constants -----------------------------------------------------

	// Attributes ----------------------------------------------------

	/**The thread on which the build is executed*/
	Thread buildThread

	/**The parent thread of each of the builds*/
	Thread projectThread

	/**List of the listeners for the output of the Maven build*/
	List<MvnBuildOutputListener> listeners = []

	/**System monitor to kill children processes*/
	final JavaSysMon sysmon = new JavaSysMon()

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
			try{
				Invoker invoker = new DefaultInvoker()
				invoker.setOutputHandler(this)
				invoker.execute(build)
			}catch(InterruptedException e){
				// kill process tree
				sysmon.infanticide()
				// re-assert interrupt
				Thread.currentThread().interrupt()
			}
		}
	}

	/**
	 * Launches a new build for each line of the project file (that must contain a directory and
	 * a Maven build command separated by ;) 
	 *  
	 * @param buildProjectFile the maven build project file
	 */
	public void launchBuildProject(final MavenBuildProjectFile buildProjectFile){
		projectThread = Thread.start{
			try{
				buildProjectFile.builds.each{ build ->
					launchBuild(build)
					// inform of new build launched
					listeners.each{it.receiveBuildLaunched(build)}
					synchronized (buildThread) {
						// wait until finished
						buildThread.wait()
					}
				}
			}catch(InterruptedException e){
				// kill process tree
				sysmon.infanticide()
				// re-assert interrupt
				Thread.currentThread().interrupt()
			}
		}
	}

	/**
	 * Stops the currently run build
	 */
	public void stopBuild(){
		if(projectThread){
			projectThread.interrupt()
		}else{
			if(buildThread){
				buildThread.interrupt()
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

	@Override
	public void consumeLine(String line) {
		listeners.each{it.recieveOutput(line)}
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}
