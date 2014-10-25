package org.platypus.mvnWatcher.controller

import com.jezhumble.javasysmon.JavaSysMon
import com.jezhumble.javasysmon.OsProcess
import org.apache.maven.shared.invoker.DefaultInvoker
import org.apache.maven.shared.invoker.InvocationOutputHandler
import org.apache.maven.shared.invoker.Invoker
import org.platypus.mvnWatcher.listener.MvnBuildOutputListener
import org.platypus.mvnWatcher.model.MavenBuildProjectFile
import org.platypus.mvnWatcher.model.MvnBuild

/**
 * Class for launching Maven builds. This can be done for a specific folder or for each folder on
 * a project file.
 *
 * @author alfergon
 *
 */
class MvnBuildLauncher implements InvocationOutputHandler {

	// Constants -----------------------------------------------------

	static final String JAVA_EXE = 'java.exe'

	// Attributes ----------------------------------------------------

	/**The parent thread of each of the builds*/
	Thread projectThread

	/**List of the listeners for the output of the Maven build*/
	List<MvnBuildOutputListener> listeners = []

	/**System monitor to kill children processes*/
	final JavaSysMon systemMonitor = new JavaSysMon()

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	/**
	 * Launches the passed build command on the specified directory and calls listeners passing
	 * each of the lines of the output of said command
	 *
	 * @param build the Maven build to be launched
	 * @return the started thread of the build process
	 */
	public Thread launchBuild(final MvnBuild build) {
		Thread.start {
			try {
				Invoker invoker = new DefaultInvoker()
				invoker.setOutputHandler(this)
				invoker.execute(build)
			} catch (InterruptedException ignored) {
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
	public void launchBuildProject(final MavenBuildProjectFile buildProjectFile) {
		projectThread = Thread.start {
			try {
				buildProjectFile.builds.each { build ->
					Thread buildThread = launchBuild(build)
					// inform of new build launched
					listeners.each { it.receiveBuildLaunched(build) }
					synchronized (buildThread) {
						// wait until finished
						buildThread.wait()
					}
				}
			} catch (InterruptedException ignored) {
				// re-assert interrupt
				Thread.currentThread().interrupt()
			}
		}
	}

	/**
	 * Stops the currently run build
	 */
	public void stopBuild() {
		killBuildProcess()
		if (projectThread) {
			projectThread.interrupt()
		} else {
			if (buildThread) {
				buildThread.interrupt()
			}
		}
	}

	/**
	 * Adds a new listener for the builds' output
	 * @param listener the new listener
	 */
	public void addListener(MvnBuildOutputListener listener) {
		listeners.add listener
	}

	@Override
	public void consumeLine(String line) {
		listeners.each { it.receiveOutput(line) }
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	/**
	 * Searches for and kills the process that is currently building
	 */
	protected void killBuildProcess() {
		int currentPid = systemMonitor.currentPid()
		int buildPid = 0
		def visitor = { OsProcess p, l ->
			if (p.processInfo().name == JAVA_EXE && p.processInfo().pid != currentPid) {
				// get the pid of the building process
				buildPid = p.processInfo().pid
			}
			// do not kill visited process
			return false
		}
		// execute the visitor to get the building pid from the current process
		systemMonitor.visitProcessTree(currentPid, visitor)
		// kill the building process
		systemMonitor.killProcess(buildPid)
	}

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

}