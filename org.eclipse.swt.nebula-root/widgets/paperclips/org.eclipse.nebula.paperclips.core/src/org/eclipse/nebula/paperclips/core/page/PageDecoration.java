/*
 * Copyright (c) 2005 Matthew Hall and others.
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
package org.eclipse.nebula.paperclips.core.page;

import org.eclipse.nebula.paperclips.core.Print;

/**
 * An interface for creating page decorations. Instances of this interface are
 * used as headers and footers in conjunction with the PagePrint class.
 * 
 * @see PagePrint
 * @see SimplePageDecoration
 * @see PageNumberPageDecoration
 * @author Matthew Hall
 */
public interface PageDecoration {
	/**
	 * Returns a decorator Print for the page with the given page number, or
	 * null if no decoration is provided for the given page.
	 * 
	 * @param pageNumber
	 *            the page number of the page being decorated.
	 * @return a decorator Print for the page with the given page number, or
	 *         null if no decoration is provided for the given page.
	 */
	public Print createPrint(PageNumber pageNumber);
}
