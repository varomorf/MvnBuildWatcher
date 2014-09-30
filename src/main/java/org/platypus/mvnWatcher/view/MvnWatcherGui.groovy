package org.platypus.mvnWatcher.view


import groovy.swing.SwingBuilder

import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.JTextArea

import org.platypus.mvnWatcher.controller.MvnBuildLauncher;
import org.platypus.mvnWatcher.controller.MvnBuildWatcher;
import org.platypus.mvnWatcher.listener.MvnBuildOutputListener;
import org.platypus.mvnWatcher.listener.MvnBuildStatusListener;
import org.platypus.mvnWatcher.model.MavenBuildProjectFile
import org.platypus.mvnWatcher.model.MvnBuild
import org.platypus.mvnWatcher.model.MvnBuildStatus;

import net.miginfocom.swing.MigLayout

/**
 * Main window for the Maven Watcher app
 * 
 * @author alfergon
 *
 */
class MvnWatcherGui implements MvnBuildOutputListener, MvnBuildStatusListener{

	// Constants -----------------------------------------------------

	// Attributes ----------------------------------------------------

	/**The text area in which the raw text from the output file will be shown*/
	JTextArea rawOutput

	/**The table in which the build status will be shown*/
	JTable statusTable

	/**The label in which the status messages will be shown*/
	JLabel statuslabel

	/**File watcher instance for analycing and polling the output file from the build*/
	MvnBuildWatcher watcher = new MvnBuildWatcher()

	/**Launcher for builds*/
	MvnBuildLauncher launcher = new MvnBuildLauncher()

	/**Swing builder to use for this GUI*/
	SwingBuilder swing = new SwingBuilder()

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	/**
	 * Creates a new MvnWatcherGui starting the launcher and watcher
	 */
	public MvnWatcherGui(){
		watcher.statusListener = this
		launcher.addListener(this)
		launcher.addListener(watcher)
	}

	// Public --------------------------------------------------------

	/**
	 * Creates and shows the main GUI for the app
	 */
	public void showGui(){
		def mainLayout = new MigLayout('fill, debug, gap 0!','[]','[87%!][grow][shrink]')
		def statusLayout = new MigLayout('fill', '[300:600:50%][300:600:50%]','[]')
		swing.frame(title:'MVN Build Watcher', visible:true, pack:true,
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
						panel(constraints: 'center, wrap'){
							button(text:'Launch build on dir', actionPerformed:launchBuild)
							button(text:'Launch build project', actionPerformed:launchBuildProject)
							button(text:'Stop build', actionPerformed:stopBuild)
						}
						panel(){
							statuslabel = label(text:'Build status')
						}
					}
				}
	}

	@Override
	public void recieveOutput(String line) {
		// append new line
		swing.edt{ rawOutput.append(line+'\n') }
	}

	@Override
	public void recieveStatus(MvnBuildStatus status) {
		swing.edt{
			// change table data and force redraw of the table
			statusTable.model.rowsModel.value = status.modulesStatus
			statusTable.model.fireTableDataChanged()
		}
	}

	@Override
	public void receiveBuildLaunched(MvnBuild build) {
		// update status bar
		swing.edt{statuslabel.text = "Building $build.command on $build.directory.absolutePath"}
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	/**
	 * Clean the GUI and leave it as it starts
	 */
	protected void cleanGui(){
		rawOutput.text = ''
		watcher.newBuild()
		updateStatus()
	}

	// Private -------------------------------------------------------

	// Inner classes -------------------------------------------------

	/**
	 * Refreshes the status table
	 */
	def updateStatus = {
		statusTable.model.rowsModel.value = watcher.status.modulesStatus
		statusTable.model.fireTableDataChanged()
	}

	/**
	 * Launches a Maven build on a directory
	 */
	def launchBuild = {
		final JFileChooser fc = new JFileChooser()
		fc.setCurrentDirectory(new File('/'))
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
		if (fc.showOpenDialog(rawOutput) == JFileChooser.APPROVE_OPTION) {
			File buildDir = fc.getSelectedFile()
			cleanGui()
			swing.doOutside {
				launcher.launchBuild(new MvnBuild(command:MvnBuild.MVNCIS, options:buildDir))
			}
		}
	}

	/**
	 * Launches a Maven build on each directory specified on a project file
	 */
	def launchBuildProject = {
		final JFileChooser fc = new JFileChooser()
		fc.setCurrentDirectory(new File('/'))
		if (fc.showOpenDialog(rawOutput) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile()
			cleanGui()
			swing.doOutside {
				launcher.launchBuildProject(new MavenBuildProjectFile(file:file))
			}
		}
	}

	/**
	 * Action for stoping the current build
	 */
	def stopBuild = {
		swing.doOutside { launcher.stopBuild() }
	}

}
