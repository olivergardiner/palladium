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

import org.eclipse.nebula.paperclips.core.BackgroundPrint;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.swt.graphics.RGB;

public class BackgroundPrintTest extends TestCase {
	public void testConstructor_nullArguments() {
		try {
			new BackgroundPrint(null, new RGB(0, 0, 0));
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}

		try {
			new BackgroundPrint(new PrintStub(), null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}
	}

	public void testEquals() {
		Print background = new BackgroundPrint(new PrintStub(0), new RGB(0, 0,
				0));
		assertEquals(background, new BackgroundPrint(new PrintStub(0), new RGB(
				0, 0, 0)));
		assertFalse(background.equals(new BackgroundPrint(new PrintStub(1),
				new RGB(0, 0, 0))));
		assertFalse(background.equals(new BackgroundPrint(new PrintStub(0),
				new RGB(1, 1, 1))));
	}
}
