package org.platypus.mvnWatcher

import java.awt.event.ActionListener

import groovy.swing.SwingBuilder

import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JTable
import javax.swing.JTextArea
import javax.swing.Timer

import net.miginfocom.swing.MigLayout

/**
 * Main window for the Maven Watcher app
 * 
 * @author alfergon
 *
 */
class MvnWatcherGui {

	// Constants -----------------------------------------------------

	// Attributes ----------------------------------------------------

	/**The text area in which the raw text from the output file will be shown*/
	JTextArea rawOutput

	/**The table in which the build status will be shown*/
	JTable statusTable

	/**File watcher instance for analycing and polling the output file from the build*/
	MvnFileWatcher fileWatcher = new MvnFileWatcher()

	/**Launcher for builds*/
	MvnBuildLauncher launcher = new MvnBuildLauncher()

	/**Timer for updating the GUI*/
	Timer timer

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	// Public --------------------------------------------------------

	/**
	 * Creates and shows the main GUI for the app
	 */
	public void showGui(){
		def mainLayout = new MigLayout('fill','[]','[90%!][10%!]')
		def statusLayout = new MigLayout('fill', '[300:600:50%][300:600:50%]','[]')
		new SwingBuilder().frame(title:'MVN Build Watcher', visible:true, pack:true,
				preferredSize:[800, 600], defaultCloseOperation: JFrame.EXIT_ON_CLOSE){
					panel(layout:mainLayout){
						panel(layout:statusLayout, constraints:'grow, wrap'){
							scrollPane(constraints:'grow'){rawOutput = textArea()}
							scrollPane(constraints:'grow'){
								statusTable = table(){
									tableModel(){
										closureColumn(header:'Name', read:{row -> return row.moduleName})
										closureColumn(header:'Status', read:{row -> return row.status})
									}
								}
							}
						}
						panel(constraints: 'shrink 5, center'){
							button(text:'Select file',actionPerformed:selectFile)
							button(text:'Start auto-analize', actionPerformed:{timer.start()})
							button(text:'Stop auto-analize', actionPerformed:{timer.stop()})
							button(text:'Launch build', actionPerformed:launchBuild)
						}
					}
				}
		timer = new Timer(250, timerListener as ActionListener)
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

	def updateStatus = {
		MvnBuildStatus status = fileWatcher.getStatusData()
		statusTable.model.rowsModel.value = status.modulesStatus
		statusTable.model.fireTableDataChanged()
		if(status.buildCorrect){
			timer.stop()
		}
	}

	def timerListener = {
		if(fileWatcher.file != null){
			rawOutput.text = fileWatcher.file.text
			updateStatus()
		}else{
			fileWatcher.file = launcher.outputFile
		}
	}

	def selectFile = {
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File('./src/test/resources'))
		if (fc.showOpenDialog(rawOutput) == JFileChooser.APPROVE_OPTION) {
			fileWatcher.file = fc.getSelectedFile()
			timer.start()
		}
	}

	def launchBuild = {
		final JFileChooser fc = new JFileChooser()
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
		if (fc.showOpenDialog(rawOutput) == JFileChooser.APPROVE_OPTION) {
			File buildDir = fc.getSelectedFile()
			launcher.launchBuild(MvnBuildLauncher.MVNCIS, MvnBuildLauncher.DEFAULT_FILENAME, buildDir)
			fileWatcher.file = launcher.outputFile
			timer.start()
		}
	}

}
