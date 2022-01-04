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
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

@SuppressWarnings("restriction")
public final class PrintStub implements Print {
	private int id;

	public PrintStub() {
		this(0);
	}

	public PrintStub(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!Util.sameClass(this, obj))
			return false;

		PrintStub that = (PrintStub) obj;
		return this.id == that.id;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return null;
	}
}