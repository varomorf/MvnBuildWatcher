package org.platypus.mvnWatcher

import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import java.awt.event.ActionListener

import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JTable
import javax.swing.Timer

MvnFileWatcher fileWatcher = new MvnFileWatcher()

def source
JTable target

def updateStatus = {
	target.model.rowsModel.value = fileWatcher.getStatusData()
	target.model.fireTableDataChanged()
}

def timerListener = {
	source.text = fileWatcher.file.text
	updateStatus()
}

Timer timer = new Timer(250, timerListener as ActionListener)

def selectFile = {
	final JFileChooser fc = new JFileChooser();
	fc.setCurrentDirectory(new File('./src/test/resources'))
	int returnVal = fc.showOpenDialog(source);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		fileWatcher.file = fc.getSelectedFile()
		timer.start()
	}
}

new SwingBuilder().frame(title:'MVN Build Watcher',visible:true,pack:true,
		defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE){
			borderLayout()
			panel(constraints:BorderLayout.WEST){
				scrollPane(preferredSize:[600, 600]){ source = textArea() }
			}
			panel(constraints:BorderLayout.SOUTH){
				button(text:'Select file',actionPerformed:selectFile)
				button(text:'Start auto-analize', actionPerformed:{timer.start()})
				button(text:'Stop auto-analize', actionPerformed:{timer.stop()})
			}
			panel(constraints:BorderLayout.EAST){
				scrollPane(preferredSize:[600, 600]){
					target = table(){
						tableModel(){
							closureColumn(header:'Name', read:{row -> return row.name})
							closureColumn(header:'Status', read:{row -> return row.status})
						}
					}
				}
			}
		}