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

import org.eclipse.nebula.paperclips.core.ScalePrint;

import junit.framework.TestCase;

public class ScalePrintTest extends TestCase {
	public void testEquals() {
		ScalePrint scale1 = new ScalePrint(new PrintStub(0), null);
		ScalePrint scale2 = new ScalePrint(new PrintStub(0), null);
		assertEquals(scale1, scale2);

		scale1 = new ScalePrint(new PrintStub(1), null);
		assertFalse(scale1.equals(scale2));
		scale2 = new ScalePrint(new PrintStub(1), null);
		assertEquals(scale1, scale2);

		scale1 = new ScalePrint(new PrintStub(1), new Double(0.5));
		assertFalse(scale1.equals(scale2));
		scale2 = new ScalePrint(new PrintStub(1), new Double(0.5));
		assertEquals(scale1, scale2);
	}
}
