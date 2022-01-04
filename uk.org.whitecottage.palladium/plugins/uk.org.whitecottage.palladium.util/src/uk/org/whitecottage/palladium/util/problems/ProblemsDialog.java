package uk.org.whitecottage.palladium.util.problems;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ProblemsDialog extends TitleAreaDialog {
	protected List<Problem> problems;
	private Table table;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ProblemsDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("The following problems were detected");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(0, 0, 534, 232);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnProblem = new TableColumn(table, SWT.NONE);
		tblclmnProblem.setWidth(194);
		tblclmnProblem.setText("Problem");
		
		TableColumn tblclmnModelPath = new TableColumn(table, SWT.NONE);
		tblclmnModelPath.setWidth(336);
		tblclmnModelPath.setText("Model path");

		return area;
	}

	public void setProblems(List<Problem> problems) {
		this.problems = problems;
		
		for (Problem problem: problems) {
			TableItem row = new TableItem(table, SWT.NONE);
			row.setText(0, problem.getDescription());
			row.setText(1, problem.getPath());
		}
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		// createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(550, 400);
	}
}
