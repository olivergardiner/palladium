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
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.snippets.table.renderers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.nebula.widgets.pagination.collections.PageResultLoaderList;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageGraphicsRenderer;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageGraphicsRendererFactory;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.BlackNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.BlueNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.GreenNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.table.PageableTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This sample display a list of String in a SWT Table with navigation page
 * drawn with {@link GC} by using
 * {@link ResultAndNavigationPageGraphicsRendererFactory}.
 *
 * The combo on the bottom of the table display several styles "Blue", "Green",
 * "Black" taht you can select to change the styles of GC navigation page.
 */
public class GraphicsPageableTableExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);

		final List<String> items = createList();

		// 1) Create pageable table with 10 items per page
		// This SWT Component create internally a SWT Table+JFace TreeViewer
		int pageSize = 10;
		final PageableTable pageableTable = new PageableTable(
				shell,
				SWT.BORDER,
				SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL,
				pageSize,
				PageResultContentProvider.getInstance(),
				ResultAndNavigationPageGraphicsRendererFactory.getBlueFactory(),
				null);
		pageableTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		// 2) Initialize the table viewer
		TableViewer viewer = pageableTable.getViewer();
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());

		// 3) Set the page loader used to load a page (sublist of String)
		// according the page index selected, the page size etc.
		pageableTable.setPageLoader(new PageResultLoaderList<>(items));

		// 4) Set current page to 0 to display the first page
		pageableTable.setCurrentPage(0);

		final Combo styleCombo = new Combo(shell, SWT.READ_ONLY);
		styleCombo.setItems(new String[] { "Blue", "Green", "Black" });
		styleCombo.select(0);
		styleCombo.addListener(SWT.Selection, e -> {
			if (styleCombo.getText().equals("Blue")) {
				((ResultAndNavigationPageGraphicsRenderer) pageableTable.getCompositeTop()).getNavigationPage()
						.setConfigurator(BlueNavigationPageGraphicsConfigurator.getInstance());
			} else if (styleCombo.getText().equals("Green")) {
				((ResultAndNavigationPageGraphicsRenderer) pageableTable.getCompositeTop())
						.setConfigurator(GreenNavigationPageGraphicsConfigurator.getInstance());
			} else {
				((ResultAndNavigationPageGraphicsRenderer) pageableTable.getCompositeTop())
						.setConfigurator(BlackNavigationPageGraphicsConfigurator.getInstance());
			}

		});

		shell.setSize(450, 300);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
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
}
