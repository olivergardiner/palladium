package org.eclipse.nebula.snippets.ganttchart;

/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

import java.util.Calendar;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This sample shows how to create a chart with events that have connections. If shift is held down any linked events will move and resize together. 
 *
 */
public class ConnectionExample {

	public static void main(String [] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Gantt Chart Sample");
		shell.setSize(600, 500);		
		shell.setLayout(new FillLayout());
		
		// Create a chart
		GanttChart ganttChart = new GanttChart(shell, SWT.NONE);
				
		// Create some calendars
		Calendar sdEventOne = Calendar.getInstance();
		Calendar edEventOne = Calendar.getInstance();
		edEventOne.add(Calendar.DATE, 10); 

		Calendar sdEventTwo = Calendar.getInstance();
		Calendar edEventTwo = Calendar.getInstance();
		sdEventTwo.add(Calendar.DATE, 11);
		edEventTwo.add(Calendar.DATE, 15);

		Calendar cpDate = Calendar.getInstance();
		cpDate.add(Calendar.DATE, 16);

		// Create events
		GanttEvent eventOne = new GanttEvent(ganttChart, "Scope Event 1", sdEventOne, edEventOne, 35);		
		GanttEvent eventTwo = new GanttEvent(ganttChart, "Scope Event 2", sdEventTwo, edEventTwo, 10);		
		GanttEvent eventThree = new GanttEvent(ganttChart, "Checkpoint", cpDate, cpDate, 75);
		eventThree.setCheckpoint(true);

		// Create connections
		ganttChart.addConnection(eventOne, eventTwo);
		ganttChart.addConnection(eventTwo, eventThree);
				
		// Show chart
		shell.open();
	
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
		
	}

}
