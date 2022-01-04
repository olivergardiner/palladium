/*
 * Copyright (c) 2006 Matthew Hall and others.
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
package org.eclipse.nebula.paperclips.snippets;

import org.eclipse.nebula.paperclips.core.BigPrint;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.border.LineBorder;
import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.nebula.paperclips.widgets.PrintPreview;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Demonstrate use of BigPrint.
 *
 * @author Matthew
 */
public class Snippet3 {
	public static Print createPrint() {
		// Using "preferred" size columns, to force the document to be wider
		// than
		// the page. In most
		// cases it is recommended to use "d" for "default" columns, which can
		// shrink when needed.
		DefaultGridLook look = new DefaultGridLook();
		look.setCellBorder(new LineBorder());
		GridPrint grid = new GridPrint(look);

		final int ROWS = 60;
		final int COLS = 10;

		for (int i = 0; i < COLS; i++)
			grid.addColumn("p");
		for (int r = 0; r < ROWS; r++)
			for (int c = 0; c < COLS; c++)
				grid.add(new TextPrint("Row " + r + " Col " + c));

		// Give entire grid a light green background.
		return new BigPrint(grid);
	}

	/**
	 * Executes the snippet.
	 *
	 * @param args
	 *            command-line args.
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		shell.setText("Snippet3.java");
		shell.setBounds(100, 100, 640, 480);
		shell.setLayout(new GridLayout(3, false));

		Button prevPage = new Button(shell, SWT.PUSH);
		prevPage.setLayoutData(
				new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false));
		prevPage.setText("Previous Page");

		Button nextPage = new Button(shell, SWT.PUSH);
		nextPage.setLayoutData(
				new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false));
		nextPage.setText("Next Page");

		Button printButton = new Button(shell, SWT.PUSH);
		printButton.setLayoutData(
				new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false));
		printButton.setText("Print");

		final PrintPreview preview = new PrintPreview(shell, SWT.BORDER);
		preview.setFitHorizontal(true);
		preview.setFitVertical(true);
		preview.setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		final PrintJob job = new PrintJob("Snippet3.java", createPrint());
		job.setMargins(72);
		preview.setPrintJob(job);

		prevPage.addListener(SWT.Selection, event -> {
			int page = Math.max(preview.getPageIndex() - 1, 0);
			preview.setPageIndex(page);
		});

		nextPage.addListener(SWT.Selection, event -> {
			int page = Math.min(preview.getPageIndex() + 1,
					preview.getPageCount() - 1);
			preview.setPageIndex(page);
		});

		printButton.addListener(SWT.Selection, event -> {
			PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
			PrinterData printerData = dialog.open();
			if (printerData != null) {
				PaperClips.print(job, printerData);
				// Update the preview to display according to the selected
				// printer.
				preview.setPrinterData(printerData);
			}
		});

		shell.setVisible(true);

		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
	}
}
