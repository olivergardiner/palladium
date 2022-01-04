package org.eclipse.nebula.snippets.datechooser;

import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet 1 for DateChooserCombo : simple combo with border.
 */
public class DateChooserComboSnippet1 {
	public static void main(String[] args) {
		Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout());
    shell.setSize(300, 200);

    DateChooserCombo combo = new DateChooserCombo(shell, SWT.BORDER);
    GridData data = new GridData();
		data.widthHint = 110;
		combo.setLayoutData(data);

    shell.open();
    while ( ! shell.isDisposed() ) {
    	if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
	}
}
