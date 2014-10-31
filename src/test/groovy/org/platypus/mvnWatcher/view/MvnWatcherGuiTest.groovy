package org.platypus.mvnWatcher.view

import org.fest.swing.data.TableCellByColumnId
import org.fest.swing.fixture.FrameFixture
import org.fest.swing.fixture.JTableFixture
import org.platypus.mvnWatcher.model.MvnBuildStatus
import org.platypus.mvnWatcher.model.MvnModuleBuildStatus
import spock.lang.Specification

import static org.platypus.mvnWatcher.model.MvnModuleBuildStatus.BUILDING
import static org.platypus.mvnWatcher.model.MvnModuleBuildStatus.BUILT
import static org.platypus.mvnWatcher.model.MvnModuleBuildStatus.WAITING
import static org.platypus.mvnWatcher.view.MvnWatcherGui.COLUMN_NAME
import static org.platypus.mvnWatcher.view.MvnWatcherGui.COLUMN_STATUS

/**
 * Created by alfergon on 09/10/2014.
 */
class MvnWatcherGuiTest extends Specification {

	// Fields --------------------------------------------------------

	// Fixture Methods -----------------------------------------------

	/**The GUI object*/
	MvnWatcherGui gui

	/**The frame fixture*/
	FrameFixture window

	def setup() {
		gui = new MvnWatcherGui()
		window = new FrameFixture(gui.createGui())
		window.show()
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

//	def 'shouldUpdateStatusLabelWhenBuildFailureOccurs'(){
//		given: 'a failed build status for module foo, goal bar and reason xyz'
//		def status = new MvnBuildStatus()
//		status.fail '[ERROR] Failed to execute goal bar on project foo: xyz.'
//		when: 'the GUI receives the failed status'
//		gui.receiveStatus(status)
//		then: 'the status label shows the failed message'
//		window.label('statusLabel').text() == status.failure.toString()
//	}

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
