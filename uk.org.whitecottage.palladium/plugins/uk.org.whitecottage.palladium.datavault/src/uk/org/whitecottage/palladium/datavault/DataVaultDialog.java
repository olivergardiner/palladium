package uk.org.whitecottage.palladium.datavault;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DataVaultDialog extends TitleAreaDialog {
	private Text txtOutputFile;
	private Combo combo;
	private Label lblNewLabel;

	protected List<DDLFormat> comboFormats;
	protected List<String> formatNames;
	protected DDLFormat format = DDLFormat.SNOWFLAKE;
	protected String outputFile = "";
	protected Preferences preferences;
	protected boolean isFileOutput = true;
	
	protected FileDialog fileDialog;
	protected DirectoryDialog pathDialog;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public DataVaultDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
		
		preferences = new Preferences();
		
		comboFormats = new ArrayList<>();
		formatNames = new ArrayList<>();
		
		comboFormats.add(DDLFormat.DBTVAULT);
		formatNames.add("dbtvault");
		comboFormats.add(DDLFormat.SNOWFLAKE);
		formatNames.add("Snowflake");
		
		fileDialog = new FileDialog(parentShell);
		fileDialog.setOverwrite(true);
		fileDialog.setText("Select the destination file");
		
		pathDialog = new DirectoryDialog(parentShell);
		pathDialog.setText("Select the destination folder");
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Export a DDL for the model represented as a DataVault 2.0 schema");
		setTitle("Export DataVault 2.0 DDL");
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
														
		lblNewLabel = new Label(composite_2, SWT.NONE);
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
				if (isFileOutput) {
					String file = fileDialog.open();
					txtOutputFile.setText(file != null ? file : "");
				} else {
					String path = pathDialog.open();
					txtOutputFile.setText(path != null ? path : "");
				}
			}
		});
		btnBrowse.setLayoutData(new RowData(80, SWT.DEFAULT));
		btnBrowse.setText("Browse...");
		
		Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayoutData(new RowData(455, SWT.DEFAULT));
		RowLayout rl_composite_4 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_4.center = true;
		composite_4.setLayout(rl_composite_4);
		
		Label lblDdlTarget = new Label(composite_4, SWT.NONE);
		lblDdlTarget.setLayoutData(new RowData(110, SWT.DEFAULT));
		lblDdlTarget.setText("DDL Target");
		
		combo = new Combo(composite_4, SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setFormat(combo.getSelectionIndex());
			}
		});
		for (String formatName: formatNames) {
			combo.add(formatName);
		}
		combo.setLayoutData(new RowData(120, SWT.DEFAULT));
		combo.select(0);
		setFormat(0);
		
		Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayoutData(new RowData(SWT.DEFAULT, 192));
		
		Button btnForeignKeys = new Button(composite_3, SWT.CHECK);
		btnForeignKeys.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				preferences.setPreference(Preferences.APPLY_FOREIGN_KEYS, btnForeignKeys.getSelection());
			}
		});
		btnForeignKeys.setSelection(preferences.isPreference(Preferences.APPLY_FOREIGN_KEYS));
		btnForeignKeys.setBounds(10, 7, 440, 16);
		btnForeignKeys.setText("Create Foreign Key constraints for DataVault");
		
		Button btnUseCVTable = new Button(composite_3, SWT.CHECK);
		btnUseCVTable.setBounds(10, 117, 189, 16);
		btnUseCVTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				preferences.setPreference(Preferences.USE_CV_TABLE, btnUseCVTable.getSelection());
			}
		});
		btnUseCVTable.setSelection(preferences.isPreference(Preferences.USE_CV_TABLE));
		btnUseCVTable.setText("Use Controlled Vocabulary table");
		
		Button btnCheckButton = new Button(composite_3, SWT.CHECK);
		btnCheckButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				preferences.setPreference(Preferences.APPLY_FOREIGN_KEYS_RD_RD, btnCheckButton.getSelection());
			}
		});
		btnCheckButton.setSelection(preferences.isPreference(Preferences.APPLY_FOREIGN_KEYS_RD_RD));
		btnCheckButton.setBounds(10, 51, 440, 16);
		btnCheckButton.setText("Create Foreign Key constraints for Reference Data in Reference Data");
		
		Button btnCheckButton_1 = new Button(composite_3, SWT.CHECK);
		btnCheckButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				preferences.setPreference(Preferences.USE_ENUM_TABLE, btnCheckButton_1.getSelection());
			}
		});
		btnCheckButton_1.setText("Use Enumeration table");
		btnCheckButton_1.setSelection(preferences.isPreference(Preferences.USE_ENUM_TABLE));
		btnCheckButton_1.setBounds(10, 161, 440, 16);
		
		Button btnCheckButton_2 = new Button(composite_3, SWT.CHECK);
		btnCheckButton_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				preferences.setPreference(Preferences.CREATE_TYPES_RD, btnCheckButton_2.getSelection());
			}
		});
		btnCheckButton_2.setBounds(10, 95, 440, 16);
		btnCheckButton_2.setText("Create types for Reference Data");
		btnCheckButton_2.setSelection(preferences.isPreference(Preferences.CREATE_TYPES_RD));
		
		Button btnCheckButton_3 = new Button(composite_3, SWT.CHECK);
		btnCheckButton_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				preferences.setPreference(Preferences.APPLY_FOREIGN_KEYS_RD_DV, btnCheckButton_3.getSelection());
			}
		});
		btnCheckButton_3.setSelection(preferences.isPreference(Preferences.APPLY_FOREIGN_KEYS_RD_DV));
		btnCheckButton_3.setBounds(10, 29, 440, 16);
		btnCheckButton_3.setText("Create Foreign Key constraints for Reference Data in DataVault");
		
		Button btnCheckButton_4 = new Button(composite_3, SWT.CHECK);
		btnCheckButton_4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				preferences.setPreference(Preferences.USE_NATURAL_KEYS_RD, btnCheckButton_4.getSelection());
			}
		});
		btnCheckButton_4.setSelection(preferences.isPreference(Preferences.USE_NATURAL_KEYS_RD));
		btnCheckButton_4.setBounds(10, 139, 440, 16);
		btnCheckButton_4.setText("Use natural keys for Reference Data");
		
		Button btnCheckButton_5 = new Button(composite_3, SWT.CHECK);
		btnCheckButton_5.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				preferences.setPreference(Preferences.CREATE_TYPES_DV, btnCheckButton_5.getSelection());
			}
		});
		btnCheckButton_5.setSelection(preferences.isPreference(Preferences.CREATE_TYPES_DV));
		btnCheckButton_5.setBounds(10, 73, 390, 16);
		btnCheckButton_5.setText("Create types for DataVault");
		
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
		return new Point(500, 420);
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String file) {
		this.outputFile = file;
	}

	public DDLFormat getFormat() {
		return format;
	}

	public void setFormat(DDLFormat format) {
		this.format = format;
		int selection = comboFormats.indexOf(format);
		
		if (selection == -1) {
			format = comboFormats.get(0);
			selection = 0;
		}
		
		combo.select(selection);
	}
	
	protected void setFormat(int index) {
		format = comboFormats.get(index);
		
		if (format == DDLFormat.DBTVAULT) {
			isFileOutput = false;
			lblNewLabel.setText("Output Folder: ");
		} else {
			isFileOutput = true;
			lblNewLabel.setText("Output File: ");
		}
	}
	
	public Preferences getPreferences() {
		
		return preferences;
	}	
}
