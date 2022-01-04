package org.eclipse.nebula.snippets.compositetable;

import org.eclipse.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.nebula.widgets.compositetable.GridRowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A CompositeTable displaying first/last name pairs
 * 
 * @author djo
 */
public class CompositeTableSnippet1 {
	// First some data to display...
	private static class Name {
		public final String first;
		public final String last;

		public Name(String first, String last) {
			this.first = first;
			this.last = last;
		}
	}

	static Name[] swtCommitters = new Name[] { new Name("Grant", "Gayed"), new Name("Veronika", "Irvine"), new Name("Steve", "Northover"), new Name("Mike", "Wilson"), new Name("Christophe", "Cornu"), new Name("Lynne", "Kues"),
			new Name("Silenio", "Quarti"), new Name("Tod", "Creasey"), new Name("Felipe", "Heidrich"), new Name("Billy", "Biggs"), new Name("B", "Shingar") };

	// Now, define the table's header and row objects
	//
	// A tabular layout is desired, so no layout manager is needed on the header
	// or row. CompositeTable will handle the layout automatically. However,
	// if you supply a layout manager, CompositeTable will respect and use it.

	private static class Header extends Composite {
		public Header(Composite parent, int style) {
			super(parent, style);
			setLayout(new GridRowLayout(new int[] { 160, 100 }, false));
			new Label(this, SWT.NULL).setText("First Name");
			new Label(this, SWT.NULL).setText("Last Name");
		}
	}

	private static class Row extends Composite {
		public Row(Composite parent, int style) {
			super(parent, style);
			setLayout(new GridRowLayout(new int[] { 160, 100 }, false));
			firstName = new Text(this, SWT.NULL);
			lastName = new Text(this, SWT.NULL);
		}

		public final Text firstName;
		public final Text lastName;
	}

	// Where it all starts...

	public static void main (String [] args) {
	    Display display = new Display ();
	    Shell shell = new Shell (display);
	    shell.setText("CompositeTable Snippet 1 -- Display first/last name");
	    shell.setLayout(new FillLayout());

	    CompositeTable table = new CompositeTable(shell, SWT.NULL);
	    new Header(table, SWT.NULL); // Just drop the Header and Row in that order...
	    new Row(table, SWT.NULL);
	    table.setRunTime(true);
	    table.setNumRowsInCollection(swtCommitters.length);
	    
	    // Note the JFace-like virtual table API
	    table.addRowContentProvider((sender,currentObjectOffset,rowControl) -> {
				Row row = (Row) rowControl;
				row.firstName.setText(swtCommitters[currentObjectOffset].first);
				row.lastName.setText(swtCommitters[currentObjectOffset].last);
	    });
	    
	    shell.setSize(500, 150);
	    shell.open ();
	    while (!shell.isDisposed()) {
	        if (!display.readAndDispatch ()) display.sleep ();
	    }
	    display.dispose ();
	}
}
