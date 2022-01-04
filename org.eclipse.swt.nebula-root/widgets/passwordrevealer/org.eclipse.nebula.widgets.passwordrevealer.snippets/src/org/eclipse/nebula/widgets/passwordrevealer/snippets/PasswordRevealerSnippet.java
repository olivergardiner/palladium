/*******************************************************************************
 * Copyright (c) 2019 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com)
 *******************************************************************************/
package org.eclipse.nebula.widgets.passwordrevealer.snippets;

import org.eclipse.nebula.widgets.passwordrevealer.PasswordRevealer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A snippet for the Password Revealer Widget
 */
public class PasswordRevealerSnippet {
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		shell.setText("Password Revealer Snippet");

		final Image image = new Image(display, PasswordRevealerSnippet.class.getResourceAsStream("eye.png"));
		final Image clickImage = new Image(display, PasswordRevealerSnippet.class.getResourceAsStream("eye-slash.png"));
		shell.addListener(SWT.Dispose, e -> {
			image.dispose();
			clickImage.dispose();
		});

		final Label lbl1 = new Label(shell, SWT.NONE);
		lbl1.setText("Password Revealer:");
		final GridData gdLabel1 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gdLabel1.widthHint = 150;
		lbl1.setLayoutData(gdLabel1);

		final PasswordRevealer revealer = new PasswordRevealer(shell, SWT.NONE);
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gd.widthHint = 250;
		revealer.setLayoutData(gd);
		revealer.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		revealer.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		new Label(shell, SWT.NONE);

		final Label lbl2 = new Label(shell, SWT.NONE);
		lbl2.setText("Password Revealer with other icon:");
		final GridData gdLabel2 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gdLabel2.widthHint = 150;
		lbl2.setLayoutData(gdLabel2);

		final PasswordRevealer revealer2 = new PasswordRevealer(shell, SWT.NONE);
		final GridData gd2 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gd2.widthHint = 250;
		revealer2.setLayoutData(gd2);
		revealer2.setImage(image);
		revealer2.setClickImage(clickImage);

		new Label(shell, SWT.NONE);
		
		final Label lbl3 = new Label(shell, SWT.NONE);
		lbl3.setText("Password Revealer with push style:");
		final GridData gdLabel3 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gdLabel3.widthHint = 200;
		lbl3.setLayoutData(gdLabel3);

		final PasswordRevealer revealer3 = new PasswordRevealer(shell, SWT.PUSH);
		final GridData gd3 = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gd3.widthHint = 250;
		revealer3.setLayoutData(gd3);

		
		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

}
