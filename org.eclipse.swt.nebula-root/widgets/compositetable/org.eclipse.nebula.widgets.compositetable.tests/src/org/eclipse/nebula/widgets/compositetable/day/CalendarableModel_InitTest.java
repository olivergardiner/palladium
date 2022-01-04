/*******************************************************************************
 * Copyright (c) 2006 The Pampered Chef and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     The Pampered Chef - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.compositetable.day;

import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.nebula.widgets.compositetable.timeeditor.CalendarableItem;
import org.eclipse.nebula.widgets.compositetable.timeeditor.CalendarableModel;
import org.eclipse.nebula.widgets.compositetable.timeeditor.EventContentProvider;
import org.eclipse.nebula.widgets.compositetable.timeeditor.EventCountProvider;

/**
 * @since 3.2
 *
 */
public class CalendarableModel_InitTest extends TestCase {
	final boolean[] initCalled = new boolean[] {false};
	private CalendarableModel cm;


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		cm = new CalendarableModel();
	}
	
	public void testInitCalledAfterAllSettersSet() throws Exception {
		EventCountProvider ecp = new EventCountProvider() {
			public int getNumberOfEventsInDay(Date day) {
				initCalled[0] = true;
				return 0;
			};
		};
		
		cm.setEventCountProvider(ecp);
		assertNoRefreshCalled();
		cm.setTimeBreakdown(1, 2);
		assertNoRefreshCalled();
		cm.setEventContentProvider(new EventContentProvider() {
			public void refresh(Date day, CalendarableItem[] controls) {
			}});
		assertNoRefreshCalled();
		cm.setStartDate(new Date());
		assertRefreshCalled();
	}

	private void assertNoRefreshCalled() {
		assertFalse("no refresh yet", initCalled[0]);
	}
	
	private void assertRefreshCalled() {
		assertTrue("refresh already", initCalled[0]);
	}
	
	public void testSetTimeBreakdown_numberOfDivisionsInHourNotSet() throws Exception {
		try {
			cm.setTimeBreakdown(1, 0);
			fail("IllegalArgumentException expected");
		} catch(IllegalArgumentException e) {
			//success
		}
	}

	public void testSetTimeBreakdown_numberOfDaysNotSet() throws Exception {
		try {
			cm.setTimeBreakdown(0, 1);
			fail("IllegalArgumentException expected");
		} catch(IllegalArgumentException e) {
			//success
		}
	}
	
	public void testColumnWithinDayIsInitializedForOneDay() throws Exception {
		final int NUM_DAYS=1;
		cm.setTimeBreakdown(NUM_DAYS, 4);
		assertColumnWithinDayIsInitialized(NUM_DAYS);
	}

	public void testColumnWithinDayIsInitializedForSeveralDays() throws Exception {
		final int NUM_DAYS=5;
		cm.setTimeBreakdown(NUM_DAYS, 4);
		assertColumnWithinDayIsInitialized(NUM_DAYS);
	}

	private void assertColumnWithinDayIsInitialized(final int NUM_DAYS) {
		for (int i=0; i < NUM_DAYS; ++i) {
			assertEquals("It's initialized", -1, cm.getNumberOfColumnsWithinDay(i));
		}
	}
}
