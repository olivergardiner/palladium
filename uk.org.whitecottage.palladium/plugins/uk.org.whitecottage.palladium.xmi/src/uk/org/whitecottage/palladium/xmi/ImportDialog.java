package uk.org.whitecottage.palladium.xmi;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowData;
import org.eclipse.uml2.uml.Package;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class ImportDialog extends TitleAreaDialog {
	private Text textPackage;
	private Text textXMIFile;
	
	protected String importFile;
	protected Package targetPackage;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ImportDialog(Shell parentShell) {
		super(parentShell);
	}

	public ImportDialog(Shell parentShell, Package targetPackage) {
		super(parentShell);
		
		this.targetPackage = targetPackage;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Import a model from another tool as XMI");
		setTitle("XMI Import");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = new Composite(container, SWT.NONE);
		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(rl_composite);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		RowLayout rl_composite_1 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_1.center = true;
		composite_1.setLayout(rl_composite_1);
		
		Label lblTargetPackage = new Label(composite_1, SWT.NONE);
		lblTargetPackage.setLayoutData(new RowData(110, SWT.DEFAULT));
		lblTargetPackage.setText("Target Package:");
		
		textPackage = new Text(composite_1, SWT.BORDER);
		textPackage.setEditable(false);
		textPackage.setLayoutData(new RowData(242, SWT.DEFAULT));
		
		Button btnBrowse = new Button(composite_1, SWT.NONE);
		btnBrowse.setText("Browse...");
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		RowLayout rl_composite_2 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_2.center = true;
		composite_2.setLayout(rl_composite_2);
		
		Label lblNewLabel = new Label(composite_2, SWT.NONE);
		lblNewLabel.setLayoutData(new RowData(110, SWT.DEFAULT));
		lblNewLabel.setText("XMI File:");
		
		textXMIFile = new Text(composite_2, SWT.BORDER);
		textXMIFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				importFile = textXMIFile.getText();
			}
		});
		textXMIFile.setLayoutData(new RowData(242, SWT.DEFAULT));
		
		Button btnNewButton = new Button(composite_2, SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fileDialog = new FileDialog(getShell());
				fileDialog.setText("Select the XMI file to import");
				textXMIFile.setText(fileDialog.open());
			}
		});
		btnNewButton.setText("Browse...");

		if (targetPackage != null) {
			textPackage.setText(targetPackage.getQualifiedName());
		} else {
			textPackage.setText("Select a Package");
		}

		return area;
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

	public String getImportFile() {
		return importFile;
	}

	public void setImportFile(String importFile) {
		this.importFile = importFile;
	}

	public Package getTargetPackage() {
		return targetPackage;
	}

	public void setTargetPackage(Package targetPackage) {
		this.targetPackage = targetPackage;
		if (targetPackage != null) {
			textPackage.setText(targetPackage.getQualifiedName());
		} else {
			textPackage.setText("Select a Package");
		}
	}
}
