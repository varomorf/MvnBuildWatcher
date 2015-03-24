package org.platypus.mvnWatcher.controller

import org.platypus.mvnWatcher.model.MvnBuild
import org.platypus.mvnWatcher.model.MvnBuildStatus
import spock.lang.Specification

/**
 * Specification for MvnBuildWatcher
 *
 * Created by alfergon on 08/10/2014.
 */
class MvnBuildWatcherTest extends Specification {

	// Fields --------------------------------------------------------

	static final String TEST_FILE = 'src/test/resources/mvn.txt'
	def watcher = new MvnBuildWatcher()
	MvnBuildStatus status

	// Fixture Methods -----------------------------------------------

	def setup() {
		watcher.newBuild()
		status = watcher.status
	}

	// Feature Methods -----------------------------------------------

	def 'should recognize correctly build output'() {
		setup: 'get file with build output'
		def file = new File(TEST_FILE)
		when: 'watcher analyzes each line of the file'
		file.eachLine {
			watcher.receiveOutput(it)
		}
		then: 'the modules should have been recognized and with correct expected statuses'
		status.modulesStatus.size() == 66
		status.modulesStatus.findAll { it.built }.size() == 15
		status.modulesStatus.findAll { it.building }.size() == 1
		status.modulesStatus.findAll { it.waiting }.size() == 50
	}

	def 'should not recognize .jar building lines as modules'() {
		when: 'watcher receives good line as module status'
		watcher.setBuildingModule(status, '[INFO] Building Some Module 1.0.0')
		then: 'there should be a new module'
		status.modulesStatus.size() == 1
		when: 'watcher receives bad line as module status'
		watcher.setBuildingModule(status, '[INFO] Building something.jar: ')
		then: 'there should be no new modules'
		status.modulesStatus.size() == 1
	}

	def 'should not recognize .war building lines as modules'() {
		when: 'watcher receives good line as module status'
		watcher.setBuildingModule(status, '[INFO] Building Some Module 1.0.0')
		then: 'there should be a new module'
		status.modulesStatus.size() == 1
		when: 'watcher receives bad line as module status'
		watcher.setBuildingModule(status, '[INFO] Building war: /home/foo/bar/target/something.war')
		then: 'there should be no new modules'
		status.modulesStatus.size() == 1
	}

	def 'should not recognize archetype jar lines as modules'() {
		when: 'watcher receives good line as module status'
		watcher.setBuildingModule(status, '[INFO] Building Some Module 1.0.0')
		then: 'there should be a new module'
		status.modulesStatus.size() == 1
		when: 'watcher receives bad line as module status'
		watcher.setBuildingModule(status, '[INFO] Building archetype jar: ')
		then: 'there should be no new modules'
		status.modulesStatus.size() == 1
	}

	def 'should recognize when a build fails'() {
		given: 'watcher has already read the list of modules'
		watcher.listRead = true
		and: 'a line with build failure info'
		def line = '[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.14:test (default-test) on project org.foo.bar.xyz: There are test failures.'
		when: 'watcher receives the build failure line'
		watcher.receiveOutput(line)
		then: 'status set as failed'
		def buildStatus = watcher.status
		buildStatus.failed
		and: 'fail info is correctly retrieved'
		buildStatus.failure.failedGoal == 'org.apache.maven.plugins:maven-surefire-plugin:2.14:test (default-test)'
		buildStatus.failure.failedModule == 'org.foo.bar.xyz'
		buildStatus.failure.failReason == 'There are test failures.'
		when: 'watcher receives new error line'
		def anotherLine = '[ERROR] this must not be analyzed as an error'
		watcher.receiveOutput(anotherLine)
		then: 'status remains the same'
		buildStatus.failed
		and: 'status is not of correct build'
		!buildStatus.buildCorrect
		and: 'fail info is correct retrieved'
		buildStatus.failure.failedGoal == 'org.apache.maven.plugins:maven-surefire-plugin:2.14:test (default-test)'
		buildStatus.failure.failedModule == 'org.foo.bar.xyz'
		buildStatus.failure.failReason == 'There are test failures.'
	}

	def 'shouldSetTheStatusToABuildWhenNotifiedOfItsBegin'() {
		given: 'the watcher is ready for a new build'
		watcher.newBuild()
		and: 'a maven build'
		def build = new MvnBuild()
		when: 'the watcher is notified of the start of the build'
		watcher.receiveBuildLaunched(build)
		then: 'the watcher sets its status to the build'
		watcher.status == build.status
	}

	def 'shouldRecognizeWhenABuildEndsCorrectly'() {
		given: 'the watcher is ready for a new build'
		watcher.newBuild()
		and: 'a line of build success'
		def line = '[INFO] BUILD SUCCESS'
		when: 'the watcher receives that line'
		watcher.receiveOutput(line)
		then: 'the status is of correct build'
		watcher.status.buildCorrect
	}

	def 'shouldNoLongerShowCorrectBuildIfModulesListIsDetected'() {
		given: 'the watcher is ready for a new build'
		watcher.newBuild()
		and: 'a line of build success'
		def line = '[INFO] BUILD SUCCESS'
		when: 'the watcher receives that line'
		watcher.receiveOutput(line)
		then: 'the status is of correct build'
		watcher.status.buildCorrect
		and: 'receive line for starting list of modules'
		watcher.receiveOutput('[INFO] Reactor Build Order:')
		then: 'status no longer of correct build'
		!watcher.status.buildCorrect
	}

	def 'shouldNoLongerShowCorrectBuildIfAnotherBuildIsLaunched'() {
		given: 'the watcher is ready for a new build'
		watcher.newBuild()
		and: 'a line of build success'
		def line = '[INFO] BUILD SUCCESS'
		when: 'the watcher receives that line'
		watcher.receiveOutput(line)
		then: 'the status is of correct build'
		watcher.status.buildCorrect
		and: 'receive new build advice'
		watcher.receiveBuildLaunched(new MvnBuild())
		then: 'status no longer of correct build'
		!watcher.status.buildCorrect
	}

	// Helper Methods ------------------------------------------------

}
