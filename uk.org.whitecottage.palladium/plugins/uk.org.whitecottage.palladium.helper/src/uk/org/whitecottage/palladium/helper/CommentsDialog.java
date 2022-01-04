package uk.org.whitecottage.palladium.helper;

import java.util.ArrayList;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.nebula.widgets.richtext.RichTextEditorConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.wb.swt.ResourceManager;

import uk.org.whitecottage.palladium.util.emf.CommandUtil;

public class CommentsDialog extends TitleAreaDialog {
	private RichTextEditor text;
	private List list;
	
	protected java.util.List<WrappedComment> comments;
	protected java.util.List<WrappedComment> deletedComments;
	protected int currentComment;
	protected Element element;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public CommentsDialog(Shell parentShell) {
		super(parentShell);
		
		comments = new ArrayList<>();
		currentComment = -1;

		deletedComments = new ArrayList<>();
	}

	public CommentsDialog(Shell parentShell, Element element, Comment selected) {
		this(parentShell);
		
		this.element = element;
		
		if (element == null) {
			return;
		}
		
		if (element.getOwnedComments().isEmpty()) {
			currentComment = -1;
		} else {
			currentComment = 0;
		}
		
		int c = 0;
		for (Comment comment: element.getOwnedComments()) {
			comments.add(new WrappedComment(comment));
			if (comment == selected) {
				currentComment = c;
			}
			
			c++;
		}
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Edit comments for the selected element");
		setTitle("Comments");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.heightHint = 650;
		container.setLayoutData(gd_container);
		
		Composite composite_1 = new Composite(container, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		gd_composite_1.widthHint = 700;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setLayout(new GridLayout(1, false));
		
		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_2.widthHint = 400;
		composite_2.setLayoutData(gd_composite_2);
		composite_2.setLayout(new GridLayout(5, false));
		
		Label lblNewLabel = new Label(composite_2, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setLocation(20, 8);
		lblNewLabel.setSize(294, 15);
		lblNewLabel.setText("Owned Comments");
		
		Button button = new Button(composite_2, SWT.NONE);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				saveComment();
				
				currentComment = list.getItemCount();

				addComment();
				text.setText("");
				
				list.setSelection(currentComment);
				
				text.setFocus();
			}
		});
		button.setImage(ResourceManager.getPluginImage("uk.org.whitecottage.palladium", "icons/Add.gif"));
		
		Button button_1 = new Button(composite_2, SWT.NONE);
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				deleteComment();
				if (currentComment >= list.getItemCount()) {
					currentComment = list.getItemCount() - 1;
				}
				
				if (currentComment >= 0) {
					text.setText(comments.get(currentComment).getBody());
					list.setSelection(currentComment);
				} else {
					text.setText("");
				}
			}
		});
		button_1.setImage(ResourceManager.getPluginImage("uk.org.whitecottage.palladium", "icons/Delete.gif"));
		
		Button button_2 = new Button(composite_2, SWT.NONE);
		Button button_3 = new Button(composite_2, SWT.NONE);
		
		button_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (swapComment(currentComment, currentComment - 1)) {
					currentComment -= 1;
					list.setSelection(currentComment);
				}
				
				button_2.setEnabled(currentComment > 0);
				button_3.setEnabled(currentComment < list.getItemCount() - 1);
			}
		});
		button_2.setImage(ResourceManager.getPluginImage("uk.org.whitecottage.palladium", "icons/ArrowUp.gif"));
		
		button_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (swapComment(currentComment, currentComment + 1)) {
					currentComment += 1;
					list.setSelection(currentComment);
				}
				
				button_2.setEnabled(currentComment > 0);
				button_3.setEnabled(currentComment < list.getItemCount() - 1);
			}
		});
		button_3.setImage(ResourceManager.getPluginImage("uk.org.whitecottage.palladium", "icons/ArrowDown.gif"));
		
		Composite composite = new Composite(composite_1, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(1, false));
		
		ListViewer listViewer = new ListViewer(composite, SWT.BORDER | SWT.V_SCROLL);
		list = listViewer.getList();
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selection = list.getSelectionIndex();
				saveComment();
				
				text.setText(comments.get(selection).getBody());
				currentComment = selection;

				button_2.setEnabled(currentComment > 0);
				button_3.setEnabled(currentComment < list.getItemCount() - 1);
			}
		});
		GridData gd_list = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_list.heightHint = 60;
		gd_list.widthHint = 405;
		list.setLayoutData(gd_list);
		list.setSize(76, 68);
		list.setLocation(-151, 3);
						
		Composite composite_3 = new Composite(composite_1, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		composite_3.setLayout(new GridLayout(1, false));
		
		RichTextEditorConfiguration editorConfiguration = new RichTextEditorConfiguration();
		editorConfiguration.setOption(RichTextEditorConfiguration.REMOVE_PLUGINS, "[ 'elementspath' ]");
		editorConfiguration.setOption(RichTextEditorConfiguration.TOOLBAR_GROUPS, "["
				+ "{ name: 'clipboard', groups: [ 'clipboard', 'undo', 'find' ] },"
				+ "{ name: 'other' },"
				+ "{ name: 'editing', groups: [ 'find' ] },"
				+ "'/',"
				+ "{ name: 'paragraph', groups: [ 'list', 'indent', 'align' ] },"
				+ "{ name: 'colors' },"
				+ "'/',"
				+ "{ name: 'styles' },"
				+ "{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },"
				+ "{ name: 'links' }"
				+ "]");

		text = new RichTextEditor(composite_3, editorConfiguration, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_text.heightHint = 500;
		gd_text.widthHint = 371;
		text.setLayoutData(gd_text);

		initialiseList(list);

		button_2.setEnabled(currentComment > 0);
		button_3.setEnabled(currentComment < list.getItemCount() - 1);

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				saveComment();
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(750, 800);
	}
	
	protected void initialiseList(List list) {
		for (WrappedComment comment: comments) {
			String commentText = comment.getBody();
			if (commentText == null) {
				commentText = "";
			}
			list.add(commentText);
		}
		
		if (currentComment >= 0) {
			list.setSelection(currentComment);
			String commentText = comments.get(currentComment).getBody();
			if (commentText == null) {
				commentText = "";
			}
			text.setText(commentText);
		}
	}
	
	protected boolean swapComment(int first, int second) {
		if (first < 0 || second < 0) {
			return false;
		}
		
		int max = list.getItemCount();
		if (first >= max || second >= max) {
			return false;
		}
		
		String firstValue = list.getItem(first);
		String secondValue = list.getItem(second);
		list.setItem(first, secondValue);
		list.setItem(second, firstValue);
		WrappedComment firstComment = comments.get(first);
		WrappedComment secondComment = comments.get(second);
		comments.set(first, secondComment);
		comments.set(second, firstComment);
		
		return true;
	}
	
	protected void saveComment() {
		if (currentComment >= 0) {
			comments.get(currentComment).setBody(text.getText());
			list.setItem(currentComment, text.getText());
		} else if (!text.getText().isEmpty()) {
			currentComment = 0;
			comments.add(new WrappedComment(text.getText()));
			list.add(text.getText());
		}
	}

	protected void addComment() {
		comments.add(new WrappedComment());
		list.add("");
	}

	protected void deleteComment() {
		list.remove(currentComment);
		deletedComments.add(comments.get(currentComment));
		comments.remove(currentComment);
	}

	public CompoundCommand getAddDeleteCommand() {
		CompoundCommand command = new CompoundCommand();
		for (WrappedComment comment: deletedComments) {
			if (!comment.isNewComment()) { // You can't delete a comment that doesn't exist...
				command.append(CommandUtil.buildDeleteCommand(comment.getComment()));
			}
		}
		
		for (WrappedComment comment: comments) {
			if (comment.getComment() == null) {
				Comment newComment = UMLFactory.eINSTANCE.createComment();
				newComment.setBody(comment.getBody());
				comment.setComment(newComment);
				Command newCommand = CommandUtil.buildCreateCommentCommand(element, newComment);
				command.append(newCommand);
			} else {
				if (comment.isChanged()) {
					Command newCommand = CommandUtil.buildUpdateCommentCommand(comment.getComment(), comment.getBody());
					command.append(newCommand);
				}
			}
		}

		return command;
	}
	
	public CompoundCommand getOrderCommand() {
		CompoundCommand command = new CompoundCommand();
		
		int index = 0;
		for (WrappedComment comment: comments) {
			Command newCommand = CommandUtil.buildMoveCommentCommand(element, comment.getComment(), index);
			command.append(newCommand);

			index++;
		}

		return command;
	}	
}
