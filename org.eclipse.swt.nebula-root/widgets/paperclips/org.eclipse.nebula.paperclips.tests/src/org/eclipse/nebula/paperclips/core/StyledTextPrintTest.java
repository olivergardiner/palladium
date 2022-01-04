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

import junit.framework.TestCase;

import org.eclipse.nebula.paperclips.core.text.StyledTextPrint;
import org.eclipse.nebula.paperclips.core.text.TextStyle;
import org.eclipse.swt.SWT;

public class StyledTextPrintTest extends TestCase {
	public void testEquals() {
		StyledTextPrint styled1 = new StyledTextPrint();
		StyledTextPrint styled2 = new StyledTextPrint();
		assertEquals(styled1, styled2);

		styled1.append(new PrintStub());
		assertFalse(styled1.equals(styled2));
		styled2.append(new PrintStub());
		assertEquals(styled1, styled2);

		styled1.setStyle(new TextStyle().align(SWT.CENTER));
		assertFalse(styled1.equals(styled2));
		styled2.setStyle(new TextStyle().align(SWT.CENTER));
		assertEquals(styled1, styled2);
	}
}
