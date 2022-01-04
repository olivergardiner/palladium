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

import org.eclipse.nebula.paperclips.core.SeriesPrint;

import junit.framework.TestCase;

public class SeriesPrintTest extends TestCase {
	public void testEquals() {
		SeriesPrint series1 = new SeriesPrint();
		SeriesPrint series2 = new SeriesPrint();
		assertEquals(series1, series2);

		series1.add(new PrintStub());
		assertFalse(series1.equals(series2));
		series2.add(new PrintStub());
		assertEquals(series1, series2);
	}
}
