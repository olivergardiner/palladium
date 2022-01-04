package uk.org.whitecottage.palladium.catalogue;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DocumentationDialog extends TitleAreaDialog {
	private Text txtTemplateFileOOXML;
	private Text txtOutputFolderOOXML;
	private Text txtTemplateFolderHTML;
	private Text txtOutputFolderHTML;
	private Button btnUseDefaultTemplateOOXML;
	private Button btnUseDefaultTemplateHTML;
	private TabFolder tabFolder;

	protected CatalogueFormat format = CatalogueFormat.OOXML;

	protected String folderOOXML = "";
	protected String templateOOXML = "";
	protected boolean isDefaultTemplateOOXML = true;
	protected String folderHTML = "";
	protected String templateHTML = "";
	protected boolean isDefaultTemplateHTML = true;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public DocumentationDialog(Shell parentShell) {
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
		setMessage("Create configurable documentation of the model");
		setTitle("Documentation Generation");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FillLayout(SWT.VERTICAL));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (tabFolder.getSelectionIndex()) {
				case 0:
					format = CatalogueFormat.HTML;
					break;
				case 1:
					format = CatalogueFormat.OOXML;
					break;
				default:
					format = CatalogueFormat.HTML;
					break;
				}
			}
		});
		
				TabItem tbtmHtml = new TabItem(tabFolder, SWT.NONE);
				tbtmHtml.setText("HTML");
				
				Composite composite_4 = new Composite(tabFolder, SWT.NONE);
				tbtmHtml.setControl(composite_4);
				composite_4.setLayout(new RowLayout(SWT.VERTICAL));
				
		Composite composite_5 = new Composite(composite_4, SWT.NONE);
		RowLayout rl_composite_5 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_5.center = true;
		composite_5.setLayout(rl_composite_5);
		
		Label lblNewLabel_1 = new Label(composite_5, SWT.NONE);
		lblNewLabel_1.setLayoutData(new RowData(110, SWT.DEFAULT));
		lblNewLabel_1.setText("Template Folder:");
		
		txtTemplateFolderHTML = new Text(composite_5, SWT.BORDER);
		txtTemplateFolderHTML.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				templateHTML = txtTemplateFolderHTML.getText();
			}
		});
		txtTemplateFolderHTML.setLayoutData(new RowData(242, SWT.DEFAULT));
		
		Button btnNewButton = new Button(composite_5, SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
				directoryDialog.setText("Select the template directory");
				txtTemplateFolderHTML.setText(directoryDialog.open());
			}
		});
		btnNewButton.setLayoutData(new RowData(80, SWT.DEFAULT));
		btnNewButton.setText("Browse...");
		
		Composite composite_6 = new Composite(composite_4, SWT.NONE);
		RowLayout rl_composite_6 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_6.center = true;
		composite_6.setLayout(rl_composite_6);
		
		Label lblOutputFolder = new Label(composite_6, SWT.NONE);
		lblOutputFolder.setLayoutData(new RowData(110, SWT.DEFAULT));
		lblOutputFolder.setText("Output Folder: ");
		
		txtOutputFolderHTML = new Text(composite_6, SWT.BORDER);
		txtOutputFolderHTML.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				folderHTML = txtOutputFolderHTML.getText();
			}
		});
		txtOutputFolderHTML.setLayoutData(new RowData(242, SWT.DEFAULT));
		
		Button btnNewButton_1 = new Button(composite_6, SWT.NONE);
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
				directoryDialog.setText("Select the output directory");
				txtOutputFolderHTML.setText(directoryDialog.open());
			}
		});
		btnNewButton_1.setLayoutData(new RowData(80, SWT.DEFAULT));
		btnNewButton_1.setText("Browse...");
		
		Composite composite_7 = new Composite(composite_4, SWT.NONE);
		composite_7.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		btnUseDefaultTemplateHTML = new Button(composite_7, SWT.CHECK);
		btnUseDefaultTemplateHTML.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (btnUseDefaultTemplateHTML.getSelection()) {
					txtTemplateFolderHTML.setEnabled(false);
					btnNewButton.setEnabled(false);
					isDefaultTemplateHTML = true;
				} else {
					txtTemplateFolderHTML.setEnabled(true);
					btnNewButton.setEnabled(true);
					isDefaultTemplateHTML = false;
				}
			}
		});
		btnUseDefaultTemplateHTML.setText("Use Default Template Set");
		
		btnUseDefaultTemplateHTML.setSelection(true);
		txtTemplateFolderHTML.setEnabled(false);
		btnNewButton.setEnabled(false);

		TabItem tbtmOOXML = new TabItem(tabFolder, SWT.NONE);
		tbtmOOXML.setText("OOXML");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmOOXML.setControl(composite);
		composite.setLayout(new RowLayout(SWT.VERTICAL));

		Composite composite_1 = new Composite(composite, SWT.NONE);
		RowLayout rl_composite_1 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_1.center = true;
		composite_1.setLayout(rl_composite_1);

		Label lblTemplateFile = new Label(composite_1, SWT.NONE);
		lblTemplateFile.setLayoutData(new RowData(110, SWT.DEFAULT));
		lblTemplateFile.setText("Template File: ");

		txtTemplateFileOOXML = new Text(composite_1, SWT.BORDER);
		txtTemplateFileOOXML.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				templateOOXML = txtTemplateFileOOXML.getText();
			}
		});
		txtTemplateFileOOXML.setLayoutData(new RowData(242, SWT.DEFAULT));

		Button btnBrowse_1 = new Button(composite_1, SWT.NONE);
		btnBrowse_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fileDialog = new FileDialog(getShell());
				fileDialog.setText("Select the template file");
				txtTemplateFileOOXML.setText(fileDialog.open());
			}
		});
		btnBrowse_1.setLayoutData(new RowData(80, SWT.DEFAULT));
		btnBrowse_1.setText("Browse...");

		Composite composite_2 = new Composite(composite, SWT.NONE);
		RowLayout rl_composite_2 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_2.center = true;
		composite_2.setLayout(rl_composite_2);

		Label lblNewLabel = new Label(composite_2, SWT.NONE);
		lblNewLabel.setLayoutData(new RowData(110, SWT.DEFAULT));
		lblNewLabel.setText("Output Folder: ");

		txtOutputFolderOOXML = new Text(composite_2, SWT.BORDER);
		txtOutputFolderOOXML.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				folderOOXML = txtOutputFolderOOXML.getText();
			}
		});
		txtOutputFolderOOXML.setLayoutData(new RowData(242, SWT.DEFAULT));
		txtOutputFolderOOXML.setText(folderOOXML);

		Button btnBrowse = new Button(composite_2, SWT.NONE);
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
				directoryDialog.setText("Select the destination directory");
				String directory = directoryDialog.open();
				txtOutputFolderOOXML.setText(directory != null ? directory : "");
			}
		});
		btnBrowse.setLayoutData(new RowData(80, SWT.DEFAULT));
		btnBrowse.setText("Browse...");

		Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayout(new RowLayout(SWT.HORIZONTAL));

		btnUseDefaultTemplateOOXML = new Button(composite_3, SWT.CHECK);
		btnUseDefaultTemplateOOXML.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (btnUseDefaultTemplateOOXML.getSelection()) {
					txtTemplateFileOOXML.setEnabled(false);
					btnBrowse_1.setEnabled(false);
					isDefaultTemplateOOXML = true;
				} else {
					txtTemplateFileOOXML.setEnabled(true);
					btnBrowse_1.setEnabled(true);
					isDefaultTemplateOOXML = false;
				}
			}
		});
		btnUseDefaultTemplateOOXML.setText("Use Default Template");

		btnUseDefaultTemplateOOXML.setSelection(true);
		txtTemplateFileOOXML.setEnabled(false);
		btnBrowse_1.setEnabled(false);
		
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

	public String getFolderOOXML() {
		return folderOOXML;
	}

	public void setFolderOOXML(String folder) {
		this.folderOOXML = folder;
		txtOutputFolderOOXML.setText(folder);
	}

	public String getTemplateOOXML() {
		return templateOOXML;
	}

	public void setTemplateOOXML(String template) {
		this.templateOOXML = template;
		txtTemplateFileOOXML.setText(template);
	}

	public boolean isDefaultTemplateOOXML() {
		return isDefaultTemplateOOXML;
	}

	public void setDefaultTemplateOOXML(boolean isDefaultTemplate) {
		this.isDefaultTemplateOOXML = isDefaultTemplate;
		btnUseDefaultTemplateOOXML.setSelection(isDefaultTemplate);
	}

	public String getFolderHTML() {
		return folderHTML;
	}

	public void setFolderHTML(String folder) {
		this.folderHTML = folder;
		txtTemplateFolderHTML.setText(folder);
	}

	public String getTemplateHTML() {
		return templateHTML;
	}

	public void setTemplateHTML(String template) {
		this.templateHTML = template;
		txtOutputFolderHTML.setText(template);
	}

	public boolean isDefaultTemplateHTML() {
		return isDefaultTemplateHTML;
	}

	public void setDefaultTemplateHTML(boolean isDefaultTemplate) {
		this.isDefaultTemplateHTML = isDefaultTemplate;
		btnUseDefaultTemplateOOXML.setSelection(isDefaultTemplate);
	}

	public CatalogueFormat getFormat() {
		return format;
	}

	public void setFormat(CatalogueFormat format) {
		this.format = format;
		switch (format) {
		case HTML:
			tabFolder.setSelection(0);
			break;
		case OOXML:
			tabFolder.setSelection(1);
			break;
		default:
			break;
		}
	}
}
