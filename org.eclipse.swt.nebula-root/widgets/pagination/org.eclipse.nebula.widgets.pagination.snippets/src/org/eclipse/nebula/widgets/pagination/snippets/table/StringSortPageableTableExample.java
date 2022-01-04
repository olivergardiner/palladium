/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Angelo ZERR - initial API and implementation
 * Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.snippets.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.nebula.widgets.pagination.collections.PageResultLoaderList;
import org.eclipse.nebula.widgets.pagination.table.PageableTable;
import org.eclipse.nebula.widgets.pagination.table.SortTableColumnSelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * This sample display a list of String in a SWT Table with pagination banner
 * displayed with Page Results+Page Links on the top of the SWT Table. The
 * column which display the list of String can be clicked to sort the paginated
 * list.
 *
 */
public class StringSortPageableTableExample {

	private static Text txt;
	private static PageableTable paginationTable;

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);

		final List<String> items = createList();

		// 1) Create pageable table with 10 items per page
		// This SWT Component create internally a SWT Table+JFace TreeViewer
		int pageSize = 10;
		paginationTable = new PageableTable(shell, SWT.BORDER, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, pageSize);
		paginationTable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		// 2) Initialize the table viewer + SWT Table
		TableViewer viewer = paginationTable.getViewer();
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// 3) Create column by adding SortTableColumnSelectionListener listener
		// to sort the paginated table.
		TableViewerColumn col = createTableViewerColumn(viewer, "Name", 150);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String p = (String) element;
				return p;
			}
		});

		// Call SortTableColumnSelectionListener with null property name because
		// it's a list of String.
		col.getColumn().addSelectionListener(new SortTableColumnSelectionListener(null));

		// 4) Set the page loader used to load a page (sublist of String)
		// according the page index selected, the page size etc.
		paginationTable.setPageLoader(new PageResultLoaderList<>(items));

		// 5) Set current page to 0 to display the first page
		paginationTable.setCurrentPage(0);

		Label lbl = new Label(shell, SWT.NONE);
		lbl.setText("Max rows per page: ");
		lbl.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		txt = new Text(shell, SWT.BORDER);
		txt.setText("10");
		GridData gd = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		gd.widthHint = 30;
		txt.setLayoutData(gd);
		txt.addTraverseListener(e -> updatePageSize());
		txt.addListener(SWT.FocusOut, e -> updatePageSize());

		// paginationTable.getController().setPageSize(10);

		shell.setSize(550, 320);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void updatePageSize() {
		int pageSize;
		try {
			pageSize=Integer.parseInt(txt.getText().trim());
		} catch (NumberFormatException  nfe) {
			pageSize = 10;
		}
		txt.setText(""+pageSize);
		paginationTable.getController().setPageSize(pageSize);
	}

	/**
	 * Create a static list.
	 *
	 * @return
	 */
	private static List<String> createList() {
		List<String> names = new ArrayList<>();
		for (int i = 1; i < 2012; i++) {
			names.add("Name " + i);
		}
		return names;
	}

	private static TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
}
