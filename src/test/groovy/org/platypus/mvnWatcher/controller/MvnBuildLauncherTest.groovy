package org.platypus.mvnWatcher.controller

import org.platypus.mvnWatcher.model.MavenBuildProjectFile
import org.platypus.mvnWatcher.model.MvnBuildStatus
import spock.lang.Specification

/**
 * Created by alfergon on 31/10/2014.
 */
class MvnBuildLauncherTest extends Specification {

	// Fields --------------------------------------------------------

	static final String GOOD_TEST_FILE = 'target/test-classes/goodProjects.mvnBuild'
	static final String FIRST_FAILED_TEST_FILE = 'target/test-classes/failureAndGood.mvnBuild'
	def builder = new MvnBuildLauncher()
	def watcher = new MvnBuildWatcher()
	MvnBuildStatus status

	// Fixture Methods -----------------------------------------------

	def setup(){
		watcher.newBuild()
		builder.addListener(watcher)
		status = watcher.status
	}

	// Feature Methods -----------------------------------------------

	def 'shouldBuildAProjectFile'(){
		given: 'a maven build project file for 2 correct Maven projects'
		def projectFile = new MavenBuildProjectFile(file: new File(GOOD_TEST_FILE))
		when: 'a build is launched for the project file'
		builder.launchBuildProject(projectFile)
		sleep(10000)
		then: 'the status has 2 correct builds'
		status.modulesStatus.size == 2
		status.modulesStatus[0].building//TODO at some point this should say built
		status.modulesStatus[1].building//TODO at some point this should say built
	}

	def 'shouldStopBuildingWhenBuildFails'(){
		given: 'a maven build project file for 2 Maven projects with the first failing'
		def file = new File(FIRST_FAILED_TEST_FILE)
		def projectFile = new MavenBuildProjectFile(file: file)
		file.readLines().size == 2
		when: 'a build is launched for the project file'
		builder.launchBuildProject(projectFile)
		sleep(5000)
		then: 'only the first build has been added'
		status.modulesStatus.size == 1
		status.modulesStatus[0].building//TODO at some point this should say failed
	}

	// Helper Methods ------------------------------------------------

}
