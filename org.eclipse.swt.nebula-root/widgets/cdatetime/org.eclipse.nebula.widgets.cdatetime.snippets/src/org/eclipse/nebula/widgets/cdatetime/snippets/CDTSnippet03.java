/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime.snippets;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CDTSnippet03 {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("CDateTime");
		shell.setLayout(new GridLayout());

		final CDateTime cdt = new CDateTime(shell, CDT.BORDER | CDT.SIMPLE);
		cdt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		cdt.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		cdt.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		cdt.setPickerBackgroundColor(display.getSystemColor(SWT.COLOR_BLUE));
		cdt.setPickerForegroundColor(display.getSystemColor(SWT.COLOR_WHITE));
		cdt.setPickerTodayColor(display.getSystemColor(SWT.COLOR_YELLOW));

		shell.pack();
		final Point size = shell.getSize();
		final Rectangle screen = display.getMonitors()[0].getBounds();
		shell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
