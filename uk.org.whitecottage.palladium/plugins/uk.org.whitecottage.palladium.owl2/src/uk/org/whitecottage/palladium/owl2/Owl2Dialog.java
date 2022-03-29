package uk.org.whitecottage.palladium.owl2;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;

public class Owl2Dialog extends TitleAreaDialog {
	private Text txtOutputFile;
	private Combo format;
	
	protected String outputFile = "";
	protected String formatString = "RDF/XML";

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public Owl2Dialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Generate OWL2 files derived from the declarations in the model");
		setTitle("Convert Model to OWL2");
		Composite container = (Composite) super.createDialogArea(parent);
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setLayoutData(new RowData(110, SWT.DEFAULT));
		lblNewLabel.setText("Ouptut File:");
		
		txtOutputFile = new Text(composite_1, SWT.BORDER);
		txtOutputFile.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				outputFile = txtOutputFile.getText();
			}
		});
		txtOutputFile.setLayoutData(new RowData(242, SWT.DEFAULT));
		txtOutputFile.setText(outputFile);
		
		Button btnBrowse = new Button(composite_1, SWT.NONE);
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
		btnBrowse.setText("Browse...");
		
		Composite composite_2 = new Composite(container, SWT.NONE);
		composite_2.setLayout(new FormLayout());
		
		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		FormData fd_composite_3 = new FormData();
		fd_composite_3.top = new FormAttachment(0);
		fd_composite_3.left = new FormAttachment(0);
		composite_3.setLayoutData(fd_composite_3);
		composite_3.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblNewLabel_1 = new Label(composite_3, SWT.NONE);
		lblNewLabel_1.setLayoutData(new RowData(110, SWT.DEFAULT));
		lblNewLabel_1.setText("Format:");
		
		format = new Combo(composite_3, SWT.NONE);
		format.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				formatString = format.getText();
			}
		});
		format.setLayoutData(new RowData(144, SWT.DEFAULT));
		format.add("RDF/XML");
		format.add("RDF/XML-ABBREV");
		format.add("N-TRIPLE");
		format.add("TURTLE");
		format.select(1);

		return container;
	}

	/**
	 * Create contents of the button bar.
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
		return new Point(450, 300);
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String file) {
		this.outputFile = file;
		txtOutputFile.setText(file);
	}
	
	public String getFormat() {
		return formatString;
	}
}
