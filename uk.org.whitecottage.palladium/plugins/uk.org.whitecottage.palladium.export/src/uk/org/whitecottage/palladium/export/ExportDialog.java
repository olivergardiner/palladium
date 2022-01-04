package uk.org.whitecottage.palladium.export;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import uk.org.whitecottage.palladium.export.ExportFormat;

public class ExportDialog extends TitleAreaDialog {
	private Text txtOutputFile;
	private TabFolder tabFolder;

	protected ExportFormat format = ExportFormat.COLLIBRA;

	protected String outputFile = "";

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ExportDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Export the data catalogue for the model in various formats");
		setTitle("Export Data Catalogue");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FillLayout(SWT.VERTICAL));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = new Composite(container, SWT.NONE);
		RowLayout rl_composite = new RowLayout(SWT.VERTICAL);
		rl_composite.wrap = false;
		composite.setLayout(rl_composite);
												
		Composite composite_2 = new Composite(composite, SWT.NONE);
		RowLayout rl_composite_2 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_2.center = true;
		composite_2.setLayout(rl_composite_2);
														
		Label lblNewLabel = new Label(composite_2, SWT.NONE);
		lblNewLabel.setLayoutData(new RowData(110, SWT.DEFAULT));
		lblNewLabel.setText("Output File: ");
																
		txtOutputFile = new Text(composite_2, SWT.BORDER);
		txtOutputFile.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				outputFile = txtOutputFile.getText();
			}
		});
		txtOutputFile.setLayoutData(new RowData(242, SWT.DEFAULT));
		txtOutputFile.setText(outputFile);
		
		Button btnBrowse = new Button(composite_2, SWT.NONE);
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fileDialog = new FileDialog(getShell());
				fileDialog.setOverwrite(true);
				fileDialog.setText("Select the destination file");
				String file = fileDialog.open();
				txtOutputFile.setText(file != null ? file : "");
			}
		});
		btnBrowse.setLayoutData(new RowData(80, SWT.DEFAULT));
		btnBrowse.setText("Browse...");
																										
		tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new RowData(450, SWT.DEFAULT));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (tabFolder.getSelectionIndex()) {
				case 0:
					format = ExportFormat.COLLIBRA;
					break;
				case 1:
					format = ExportFormat.EXCEL;
					break;
				case 2:
					format = ExportFormat.CSV;
					break;
				default:
					format = ExportFormat.COLLIBRA;
					break;
				}
			}
		});

		TabItem tbtmCollibra = new TabItem(tabFolder, SWT.NONE);
		tbtmCollibra.setText("Collibra");

		Label lblNewLabel_1 = new Label(tabFolder, SWT.NONE);
		tbtmCollibra.setControl(lblNewLabel_1);
		lblNewLabel_1.setText("No options to set");

		TabItem tbtmExcel = new TabItem(tabFolder, SWT.NONE);
		tbtmExcel.setText("Excel");

		Label lblNewLabel_1_1 = new Label(tabFolder, SWT.NONE);
		lblNewLabel_1_1.setText("No options to set");
		tbtmExcel.setControl(lblNewLabel_1_1);

		TabItem tbtmCSV = new TabItem(tabFolder, 0);
		tbtmCSV.setText("CSV");

		Label lblNewLabel_1_2 = new Label(tabFolder, SWT.NONE);
		lblNewLabel_1_2.setText("No options to set");
		tbtmCSV.setControl(lblNewLabel_1_2);
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(500, 300);
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String file) {
		this.outputFile = file;
		txtOutputFile.setText(file);
	}

	public ExportFormat getFormat() {
		return format;
	}

	public void setFormat(ExportFormat format) {
		this.format = format;
		switch (format) {
		case COLLIBRA:
			tabFolder.setSelection(0);
			break;
		case EXCEL:
			tabFolder.setSelection(1);
			break;
		case CSV:
			tabFolder.setSelection(2);
			break;
		default:
			break;
		}
	}
}
