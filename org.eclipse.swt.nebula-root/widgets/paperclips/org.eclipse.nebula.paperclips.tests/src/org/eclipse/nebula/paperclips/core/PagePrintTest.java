/*
 * Copyright (c) 2007 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core;

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.nebula.paperclips.core.page.PageDecoration;
import org.eclipse.nebula.paperclips.core.page.PageNumber;
import org.eclipse.nebula.paperclips.core.page.PagePrint;

import junit.framework.TestCase;

@SuppressWarnings("restriction")
public class PagePrintTest extends TestCase {
	public void testEquals() {
		PagePrint page1 = new PagePrint(new PrintStub(0));
		PagePrint page2 = new PagePrint(new PrintStub(0));
		assertEquals(page1, page2);

		page1.setBody(new PrintStub(1));
		assertFalse(page1.equals(page2));
		page2.setBody(new PrintStub(1));
		assertEquals(page1, page2);

		page1.setHeader(new PageDecorationStub());
		assertFalse(page1.equals(page2));
		page2.setHeader(new PageDecorationStub());
		assertEquals(page1, page2);

		page1.setHeaderGap(10);
		assertFalse(page1.equals(page2));
		page2.setHeaderGap(10);
		assertEquals(page1, page2);

		page1.setFooter(new PageDecorationStub());
		assertFalse(page1.equals(page2));
		page2.setFooter(new PageDecorationStub());
		assertEquals(page1, page2);

		page1.setFooterGap(10);
		assertFalse(page1.equals(page2));
		page2.setFooterGap(10);
		assertEquals(page1, page2);
	}

	static class PageDecorationStub implements PageDecoration {
		@Override
		public boolean equals(Object obj) {
			return Util.sameClass(this, obj);
		}

		public Print createPrint(PageNumber pageNumber) {
			return null;
		}
	}
}
