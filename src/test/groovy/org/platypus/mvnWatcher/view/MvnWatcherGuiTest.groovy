package org.platypus.mvnWatcher.view

import org.fest.swing.data.TableCellByColumnId
import org.fest.swing.fixture.FrameFixture
import org.fest.swing.fixture.JTableFixture
import org.platypus.mvnWatcher.model.MvnBuild
import org.platypus.mvnWatcher.model.MvnBuildStatus
import org.platypus.mvnWatcher.model.MvnModuleBuildStatus
import spock.lang.Specification

import static org.platypus.mvnWatcher.model.MvnModuleBuildStatus.BUILDING
import static org.platypus.mvnWatcher.model.MvnModuleBuildStatus.BUILT
import static org.platypus.mvnWatcher.model.MvnModuleBuildStatus.WAITING
import static org.platypus.mvnWatcher.view.MvnWatcherGui.COLUMN_NAME
import static org.platypus.mvnWatcher.view.MvnWatcherGui.COLUMN_STATUS
import static org.platypus.mvnWatcher.view.MvnWatcherGui.ICON_STATUS_LABEL
import static org.platypus.mvnWatcher.view.MvnWatcherGui.RUNNING_ICON_URL
import static org.platypus.mvnWatcher.view.MvnWatcherGui.STATUS_LABEL
import static org.platypus.mvnWatcher.view.MvnWatcherGui.STOP_ICON_URL
import static org.platypus.mvnWatcher.view.MvnWatcherGui.SUCCESS_ICON_URL

/**
 * Created by alfergon on 09/10/2014.
 */
class MvnWatcherGuiTest extends Specification {

	// Fields --------------------------------------------------------

	/**The GUI object*/
	MvnWatcherGui gui

	/**The frame fixture*/
	FrameFixture window

    /**A default build*/
    MvnBuild defaultBuild = new MvnBuild()

	// Fixture Methods -----------------------------------------------

	def setup() {
        defaultBuild.directory = new File('/')
		gui = new MvnWatcherGui()
		window = new FrameFixture(gui.createGui())
		window.show()
	}

	def cleanup(){
		window.cleanUp()
	}

	// Feature Methods -----------------------------------------------

	def 'should update the status table when receiving a status update from the watcher'() {
		given: 'the table is empty'
		def table = window.table('statusTable')
		table.rowCount() == 0
		and: 'a build status with 3 modules on Built, Building and Waiting statuses'
		def moduleName1 = 'moduleName1'
		def moduleName2 = 'moduleName2'
		def moduleName3 = 'moduleName3'
		def status = createStatus([moduleName1, moduleName2, moduleName3], 1)
		when: 'the gui receives the status'
		gui.receiveStatus(status)
		then: 'the table has a new row'
		table.requireRowCount(3)
		and: 'the first row has the module name 1 in it and status built'
		rowWithData(table, 0, moduleName1, BUILT)
		and: 'the second row has the module name 2 in it and status building'
		rowWithData(table, 1, moduleName2, BUILDING)
		and: 'the first row has the module name in it and status waiting'
		rowWithData(table, 2, moduleName3, WAITING)
	}

	def 'shouldUpdateStatusLabelWhenBuildFailureOccurs'(){
		given: 'a failed build status for module foo, goal bar and reason xyz'
		def status = new MvnBuildStatus()
		status.fail '[ERROR] Failed to execute goal bar on project foo: xyz.'
        and: 'a build was running'
        gui.receiveBuildLaunched(defaultBuild)
		when: 'the GUI receives the failed status'
		gui.receiveStatus(status)
		then: 'the status label shows the failed message'
		window.label(STATUS_LABEL).requireText(status.failure.toString())
        and: 'stop icon is shown'
        window.label(ICON_STATUS_LABEL).component().icon.location.path.contains(STOP_ICON_URL)
	}

    def 'shouldShowNoActiveBuildAndStopIconWhenStarted'(){
        expect: 'build status shows no active build'
        window.label(STATUS_LABEL).requireText('No active build running.')
        and: 'stop icon is shown'
        window.label(ICON_STATUS_LABEL).component().icon.location.path.contains(STOP_ICON_URL)
    }

    def 'shouldShownRunningIconWhenBuilding'(){
        given: 'a build was running'
        gui.receiveBuildLaunched(defaultBuild)
        expect: 'the running gif should be set as status icon'
		window.label(ICON_STATUS_LABEL).component().icon.location.path.contains(RUNNING_ICON_URL)
	}

	def 'shouldShowSuccessIconAfterCorrectBuild'(){
		given: 'a build was running'
		gui.receiveBuildLaunched(defaultBuild)
		when: 'a success status update is received'
		gui.receiveStatus(new MvnBuildStatus(buildCorrect: true))
		then: 'correct build  icon is shown'
		window.label(ICON_STATUS_LABEL).component().icon.location.path.contains(SUCCESS_ICON_URL)
	}

	// Helper Methods ------------------------------------------------

	def createStatus(def moduleNames, int buildingPos) {
		def output = new MvnBuildStatus()
		def status = BUILT
		moduleNames.eachWithIndex { it, i ->
			if (buildingPos == i) {
				status = BUILDING
			}
			output.modulesStatus << new MvnModuleBuildStatus(status: status, moduleName: it)
			if (buildingPos == i) {
				status = WAITING
			}
		}
		return output
	}

	void rowWithData(JTableFixture tableFixture, int row, def moduleName, def status) {
		assert tableFixture.cell(TableCellByColumnId.row(row).columnId(COLUMN_NAME)).value() == moduleName
		assert tableFixture.cell(TableCellByColumnId.row(row).columnId(COLUMN_STATUS)).value() == status
	}

}
