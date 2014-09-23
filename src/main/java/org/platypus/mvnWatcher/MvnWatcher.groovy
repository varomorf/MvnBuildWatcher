package org.platypus.mvnWatcher

import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import java.awt.event.ActionListener

import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JTable
import javax.swing.Timer

import net.miginfocom.swing.MigLayout

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

def swing = new SwingBuilder()
swing.registerBeanFactory('migLayout', MigLayout)

swing.build{
	frame(title:'MVN Build Watcher', visible:true, pack:true, preferredSize:[800,600],
		defaultCloseOperation: JFrame.EXIT_ON_CLOSE){
		panel(layout:new MigLayout('fill','[]','[90%!][10%!]')){
			panel(layout:new MigLayout('fill', '[300:600:50%][300:600:50%]','[]'), constraints:'grow, wrap'){
				scrollPane(constraints:'grow'){source = textArea()}
				scrollPane(constraints:'grow'){
					target = table(){
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
			}
		}
	}
}