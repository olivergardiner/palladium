/*
 * Copyright (c) 2007 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.tests.main;

import org.eclipse.nebula.paperclips.core.ColumnPrint;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.border.BorderPrint;
import org.eclipse.nebula.paperclips.core.border.LineBorder;
import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;

/**
 * Prints "The quick brown fox jumps over the lazy dog." in increasingly large
 * blocks, using a BreakPrint every 5 blocks to force printing to advance to the
 * next column / page.
 * 
 * @author Matthew
 */
public class TestWhetherBorderPrintHoldsSomeContentForLastPage {
	public static Print createPrint() {
		GridPrint grid = new GridPrint("d:g", new DefaultGridLook(10, 10));

		String text = "The quick brown fox jumps over the lazy dog.";
		String printText = text;

		LineBorder border = new LineBorder();
		for (int i = 0; i < 100; i++, printText += "  " + text) {
			grid.add(new BorderPrint(new TextPrint(printText), border));
		}

		return new ColumnPrint(grid, 2, 10);
	}

	/**
	 * Prints the BreakPrintExample to the default printer.
	 * 
	 * @param args
	 *            command-line args
	 */
	public static void main(String[] args) {
		// Workaround for SWT bug on GTK - force SWT to initialize so we don't
		// crash.
		Display.getDefault();

		PaperClips.print(new PrintJob("BreakPrintExample.java", createPrint()),
				new PrinterData());
	}
}
