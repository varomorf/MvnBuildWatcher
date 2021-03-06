package org.platypus.mvnWatcher.view

import groovy.swing.SwingBuilder
import net.miginfocom.swing.MigLayout
import org.platypus.mvnWatcher.controller.MvnBuildLauncher
import org.platypus.mvnWatcher.controller.MvnBuildWatcher
import org.platypus.mvnWatcher.listener.MvnBuildOutputListener
import org.platypus.mvnWatcher.listener.MvnBuildStatusListener
import org.platypus.mvnWatcher.model.MavenBuildProjectFile
import org.platypus.mvnWatcher.model.MvnBuild
import org.platypus.mvnWatcher.model.MvnBuildStatus

import javax.swing.*
import java.awt.*

/**
 * Main window for the Maven Watcher app
 *
 * @author alfergon
 *
 */
class MvnWatcherGui implements MvnBuildOutputListener, MvnBuildStatusListener {

	// Constants -----------------------------------------------------

	public static final String COLUMN_NAME = 'Name'
	public static final String COLUMN_STATUS = 'Status'
    public static final String STATUS_LABEL = 'statusLabel'
    public static final String INITIAL_BUILD_STATUS = 'No active build running.'
    public static final String STOP_ICON_URL = 'images/stop-icon.png'
    public static final String RUNNING_ICON_URL = 'images/running-icon.gif'
    public static final String SUCCESS_ICON_URL = 'images/success-icon.png'
    public static final String ICON_STATUS_LABEL = 'iconStatusLabel'

    // Attributes ----------------------------------------------------

	/**The text area in which the raw text from the output file will be shown*/
	JTextArea rawOutput

	/**The table in which the build status will be shown*/
	JTable statusTable

	/**The label in which the status messages will be shown*/
	JLabel statusLabel

    /**The label in which the build status icon will be shown*/
    JLabel iconStatusLabel

	/**File watcher instance for analysing and polling the output file from the build*/
	MvnBuildWatcher watcher = new MvnBuildWatcher()

	/**Launcher for builds*/
	MvnBuildLauncher launcher = new MvnBuildLauncher()

	/**Swing builder to use for this GUI*/
	SwingBuilder swing = new SwingBuilder()

	/**The GUI frame*/
	Frame frame

	// Static --------------------------------------------------------

	// Constructors --------------------------------------------------

	/**
	 * Creates a new MvnWatcherGui starting the launcher and watcher
	 */
	public MvnWatcherGui() {
		watcher.statusListener = this
		launcher.addListener(this)
		launcher.addListener(watcher)
	}

	// Public --------------------------------------------------------

	/**
	 * Creates and shows the main GUI for the app
	 */
	public void showGui() {
		createGui()
		frame.pack()
		frame.visible = true
	}

	/**
	 * Creates the main GUI for the app, and so initializes the frame field
	 */
	public JFrame createGui() {
		def mainLayout = new MigLayout('fill, gap 0!', '[]', '[87%!][grow][shrink]')
		def statusLayout = new MigLayout('fill', '[300:600:50%][300:600:50%]', '[]')
		//noinspection GroovyAssignabilityCheck
		frame = swing.frame(id: 'frame', title: 'MVN Build Watcher', windowClosing: closing,
				preferredSize: [800, 655], defaultCloseOperation: JFrame.EXIT_ON_CLOSE) {
			panel(layout: mainLayout) {
				panel(layout: statusLayout, constraints: 'grow, wrap') {
					scrollPane(constraints: 'grow') { rawOutput = textArea() }
					scrollPane(constraints: 'grow') {
						statusTable = table(id: 'statusTable') {
							tableModel() {
								closureColumn(header: COLUMN_NAME, read: { row -> return row.moduleName })
								closureColumn(header: COLUMN_STATUS, read: { row -> return row.status })
							}
						}
					}
				}
				panel(constraints: 'center, wrap') {
					button(id: 'launchBuildOnDirButton', text: 'Launch build on dir', actionPerformed: launchBuild)
					button(id: 'launchProjectButton', text: 'Launch build project', actionPerformed: launchBuildProject)
					button(id: 'stopBuildButton', text: 'Stop build', actionPerformed: stopBuild)
				}
				panel() {
                    iconStatusLabel = label(id: ICON_STATUS_LABEL, icon: stopIcon)
					statusLabel = label(id: STATUS_LABEL, text: INITIAL_BUILD_STATUS)
				}
			}
		}
	}

	@Override
	public void receiveOutput(String line) {
		// append new line
		swing.edt { rawOutput.append(line + '\n') }
	}

	@Override
	public void receiveStatus(MvnBuildStatus status) {
		swing.edt {
			// change table data and force redraw of the table
			statusTable.model.rowsModel.value = status.modulesStatus
			statusTable.model.fireTableDataChanged()
			// success status update
			if(status.buildCorrect){
				statusLabel.text = "Building success!"
				iconStatusLabel.icon = successIcon
			}
			// failed status update
			if(status.failed){
				statusLabel.text = status.failure
                iconStatusLabel.icon = stopIcon
			}
		}
	}

	@Override
	public void receiveBuildLaunched(MvnBuild build) {
		// update status bar
		swing.edt {
            statusLabel.text = "Building $build.goals on $build.pomFile.absolutePath"
            iconStatusLabel.icon = runningIcon
        }
	}

	/**
	 * Clean the GUI and leave it as it starts
	 */
	protected void cleanGui() {
		rawOutput.text = ''
		watcher.newBuild()
		updateStatus()
	}

	// Package protected ---------------------------------------------

	// Protected -----------------------------------------------------

	/**
	 * Returns the file of the directory in which the file choosers must start
	 *
	 * @return the file of the directory in which the file choosers must start
	 */
	protected File getStartingDir() {
		// defaults to the executing directory
		return new File('.')
	}

    /**
     * Returns an ImageIcon for the stop icon
     * @return an ImageIcon for the stop icon (or null if the image is not found)
     */
    protected ImageIcon getStopIcon() {
        def url = getClass().getClassLoader().getResource(STOP_ICON_URL)
        url ? new ImageIcon(url) : null
    }

    /**
     * Returns an ImageIcon for the running icon
     * @return an ImageIcon for the running icon (or null if the image is not found)
     */
    protected ImageIcon getRunningIcon() {
        def url = getClass().getClassLoader().getResource(RUNNING_ICON_URL)
        url ? new ImageIcon(url) : null
    }

	/**
	 * Returns an ImageIcon for the success icon
	 * @return an ImageIcon for the success icon (or null if the image is not found)
	 */
	protected ImageIcon getSuccessIcon() {
		def url = getClass().getClassLoader().getResource(SUCCESS_ICON_URL)
		url ? new ImageIcon(url) : null
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
		final JFileChooser fc = new JFileChooser(startingDir)
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
		if (fc.showOpenDialog(rawOutput) == JFileChooser.APPROVE_OPTION) {
			File buildDir = fc.getSelectedFile()
			cleanGui()
			swing.doOutside {
				launcher.launchBuild(new MvnBuild(directory: buildDir))
			}
		}
	}

	/**
	 * Launches a Maven build on each directory specified on a project file
	 */
	def launchBuildProject = {
		final JFileChooser fc = new JFileChooser(startingDir)
		if (fc.showOpenDialog(rawOutput) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile()
			cleanGui()
			swing.doOutside {
				launcher.launchBuildProject(new MavenBuildProjectFile(file: file))
			}
		}
	}

	/**
	 * Action for stopping the current build
	 */
	def stopBuild = {
		swing.doOutside { launcher.stopBuild() }
	}

	/**
	 * Stops the current build on the EDT when closing the app
	 */
	def closing = {
		launcher.stopBuild()
	}

}
