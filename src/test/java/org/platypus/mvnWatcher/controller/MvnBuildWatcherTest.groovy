package org.platypus.mvnWatcher.controller

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

	// Helper Methods ------------------------------------------------

}