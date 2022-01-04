/*******************************************************************************
 * Copyright (c) 2017 Diamond Light Source Ltd.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.nebula.visualization.xygraph.linearscale.Tick;
import org.eclipse.nebula.visualization.xygraph.linearscale.TickFactory;
import org.eclipse.nebula.visualization.xygraph.linearscale.TickFactory.TickFormatting;
import org.junit.Assert;
import org.junit.Test;

public class TickFactoryTest {

	private static final double ERROR = 1e-15;

	@Test
	public void testRounding() {
		try {
			TickFactory.roundDown(BigDecimal.ONE, BigDecimal.ZERO);
		} catch (IllegalArgumentException iae) {

		} catch (Exception e) {
			Assert.fail("Did not throw IAE");
		}

		try {
			TickFactory.roundUp(BigDecimal.ONE, BigDecimal.ZERO);
		} catch (IllegalArgumentException iae) {

		} catch (Exception e) {
			Assert.fail("Did not throw IAE");
		}

		Assert.assertEquals(0, TickFactory.roundDown(BigDecimal.ZERO, BigDecimal.ONE), ERROR);
		Assert.assertEquals(0, TickFactory.roundUp(BigDecimal.ZERO, BigDecimal.ONE), ERROR);

		Assert.assertEquals(1, TickFactory.roundDown(BigDecimal.valueOf(1.5), BigDecimal.ONE), ERROR);
		Assert.assertEquals(2, TickFactory.roundUp(BigDecimal.valueOf(1.5), BigDecimal.ONE), ERROR);

		Assert.assertEquals(1, TickFactory.roundDown(BigDecimal.valueOf(1), BigDecimal.ONE), ERROR);
		Assert.assertEquals(1, TickFactory.roundUp(BigDecimal.valueOf(1), BigDecimal.ONE), ERROR);

		Assert.assertEquals(0, TickFactory.roundDown(BigDecimal.valueOf(0.5), BigDecimal.ONE), ERROR);
		Assert.assertEquals(1, TickFactory.roundUp(BigDecimal.valueOf(0.5), BigDecimal.ONE), ERROR);

		Assert.assertEquals(-1, TickFactory.roundDown(BigDecimal.valueOf(-0.5), BigDecimal.ONE), ERROR);
		Assert.assertEquals(0, TickFactory.roundUp(BigDecimal.valueOf(-0.5), BigDecimal.ONE), ERROR);

		Assert.assertEquals(-1, TickFactory.roundDown(BigDecimal.valueOf(-1), BigDecimal.ONE), ERROR);
		Assert.assertEquals(-1, TickFactory.roundUp(BigDecimal.valueOf(-1), BigDecimal.ONE), ERROR);

		Assert.assertEquals(-2, TickFactory.roundDown(BigDecimal.valueOf(-1.5), BigDecimal.ONE), ERROR);
		Assert.assertEquals(-1, TickFactory.roundUp(BigDecimal.valueOf(-1.5), BigDecimal.ONE), ERROR);

		Assert.assertEquals(-1.6, TickFactory.roundDown(BigDecimal.valueOf(-1.5), BigDecimal.valueOf(0.2)), ERROR);
		Assert.assertEquals(-1.4, TickFactory.roundUp(BigDecimal.valueOf(-1.5), BigDecimal.valueOf(0.2)), ERROR);

		Assert.assertEquals(-0.2, TickFactory.roundDown(BigDecimal.valueOf(-0.1), BigDecimal.valueOf(0.2)), ERROR);
		Assert.assertEquals(0, TickFactory.roundUp(BigDecimal.valueOf(-0.1), BigDecimal.valueOf(0.2)), ERROR);

		Assert.assertEquals(-0.2, TickFactory.roundDown(BigDecimal.valueOf(-0.2), BigDecimal.valueOf(0.2)), ERROR);
		Assert.assertEquals(-0.2, TickFactory.roundUp(BigDecimal.valueOf(-0.2), BigDecimal.valueOf(0.2)), ERROR);

		Assert.assertEquals(0, TickFactory.roundDown(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2)), ERROR);
		Assert.assertEquals(0.2, TickFactory.roundUp(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2)), ERROR);

		Assert.assertEquals(0.2, TickFactory.roundDown(BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.2)), ERROR);
		Assert.assertEquals(0.2, TickFactory.roundUp(BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.2)), ERROR);

		Assert.assertEquals(0.6, TickFactory.roundDown(BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.2)), ERROR);
		Assert.assertEquals(0.8, TickFactory.roundUp(BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.2)), ERROR);

		Assert.assertEquals(0.6, TickFactory.roundDown(BigDecimal.valueOf(0.6 + 1e-7), BigDecimal.valueOf(0.2)), ERROR);
		Assert.assertEquals(0.8, TickFactory.roundUp(BigDecimal.valueOf(0.8 - 1e-7), BigDecimal.valueOf(0.2)), ERROR);

		Assert.assertEquals(0.6, TickFactory.roundDown(BigDecimal.valueOf(0.6 - 1e-7), BigDecimal.valueOf(0.2)), ERROR);
		Assert.assertEquals(0.8, TickFactory.roundUp(BigDecimal.valueOf(0.8 + 1e-7), BigDecimal.valueOf(0.2)), ERROR);

		Assert.assertEquals(-0.6, TickFactory.roundDown(BigDecimal.valueOf(-0.6 + 1e-7), BigDecimal.valueOf(0.2)),
				ERROR);
		Assert.assertEquals(-0.8, TickFactory.roundUp(BigDecimal.valueOf(-0.8 - 1e-7), BigDecimal.valueOf(0.2)), ERROR);

		Assert.assertEquals(-0.6, TickFactory.roundDown(BigDecimal.valueOf(-0.6 - 1e-7), BigDecimal.valueOf(0.2)),
				ERROR);
		Assert.assertEquals(-0.8, TickFactory.roundUp(BigDecimal.valueOf(-0.8 + 1e-7), BigDecimal.valueOf(0.2)), ERROR);

		Assert.assertEquals(0.12345678901234560,
				TickFactory.roundDown(BigDecimal.valueOf(0.12345678901234561), BigDecimal.valueOf(2e-17)), ERROR);
		Assert.assertEquals(0.12345678901234570,
				TickFactory.roundUp(BigDecimal.valueOf(0.12345678901234569), BigDecimal.valueOf(2e-17)), ERROR);

		Assert.assertEquals(0.12345678901234560e-20,
				TickFactory.roundDown(BigDecimal.valueOf(0.12345678901234561e-20), BigDecimal.valueOf(2e-37)), ERROR);
		Assert.assertEquals(0.12345678901234570e-20,
				TickFactory.roundUp(BigDecimal.valueOf(0.12345678901234569e-20), BigDecimal.valueOf(2e-37)), ERROR);

		Assert.assertEquals(0.12345678901234560e20,
				TickFactory.roundDown(BigDecimal.valueOf(0.12345678901234561e20), BigDecimal.valueOf(2e3)), ERROR);
		Assert.assertEquals(0.12345678901234570e20,
				TickFactory.roundUp(BigDecimal.valueOf(0.12345678901234569e20), BigDecimal.valueOf(2e3)), ERROR);
	}

	@Test
	public void testNiceNum() {
		Assert.assertEquals(new BigDecimal("0.1"), TickFactory.nicenum(BigDecimal.valueOf(0.1), false));
		Assert.assertEquals(new BigDecimal("1"), TickFactory.nicenum(BigDecimal.valueOf(1), false));
		Assert.assertEquals(new BigDecimal("1e1"), TickFactory.nicenum(BigDecimal.valueOf(10), false));
		Assert.assertEquals(new BigDecimal("1"), TickFactory.nicenum(BigDecimal.valueOf(0.9), false));
		Assert.assertEquals(new BigDecimal("1e1"), TickFactory.nicenum(BigDecimal.valueOf(9), false));
		Assert.assertEquals(new BigDecimal("1e2"), TickFactory.nicenum(BigDecimal.valueOf(90), false));
		Assert.assertEquals(new BigDecimal("1"), TickFactory.nicenum(BigDecimal.valueOf(0.7), false));
		Assert.assertEquals(new BigDecimal("1e1"), TickFactory.nicenum(BigDecimal.valueOf(7), false));
		Assert.assertEquals(new BigDecimal("1e2"), TickFactory.nicenum(BigDecimal.valueOf(70), false));
		Assert.assertEquals(new BigDecimal("0.5"), TickFactory.nicenum(BigDecimal.valueOf(0.5), false));
		Assert.assertEquals(new BigDecimal("5"), TickFactory.nicenum(BigDecimal.valueOf(5), false));
		Assert.assertEquals(new BigDecimal("5e1"), TickFactory.nicenum(BigDecimal.valueOf(50), false));
		Assert.assertEquals(new BigDecimal("0.5"), TickFactory.nicenum(BigDecimal.valueOf(0.3), false));
		Assert.assertEquals(new BigDecimal("5"), TickFactory.nicenum(BigDecimal.valueOf(3), false));
		Assert.assertEquals(new BigDecimal("5e1"), TickFactory.nicenum(BigDecimal.valueOf(30), false));
		Assert.assertEquals(new BigDecimal("0.2"), TickFactory.nicenum(BigDecimal.valueOf(0.2), false));
		Assert.assertEquals(new BigDecimal("2"), TickFactory.nicenum(BigDecimal.valueOf(2), false));
		Assert.assertEquals(new BigDecimal("2e1"), TickFactory.nicenum(BigDecimal.valueOf(20), false));
		Assert.assertEquals(new BigDecimal("0.2"), TickFactory.nicenum(BigDecimal.valueOf(0.19), false));
		Assert.assertEquals(new BigDecimal("2"), TickFactory.nicenum(BigDecimal.valueOf(1.9), false));
		Assert.assertEquals(new BigDecimal("2e1"), TickFactory.nicenum(BigDecimal.valueOf(19), false));

		// double[] ranges = new double[] {1, 2, 5};
		// for (double r : ranges) {
		// for (int i = 2; i <= 10; i++) {
		// System.out.println(TickFactory.nicenum(BigDecimal.valueOf(r/i),
		// true));
		// }
		// }

		Assert.assertEquals(new BigDecimal("1"), TickFactory.nicenum(BigDecimal.valueOf(1), true));
		Assert.assertEquals(new BigDecimal("1"), TickFactory.nicenum(BigDecimal.valueOf(1.3), true));
		Assert.assertEquals(new BigDecimal("2"), TickFactory.nicenum(BigDecimal.valueOf(1.5), true));
		Assert.assertEquals(new BigDecimal("2"), TickFactory.nicenum(BigDecimal.valueOf(2), true));
		Assert.assertEquals(new BigDecimal("2"), TickFactory.nicenum(BigDecimal.valueOf(2.2), true));
		Assert.assertEquals(new BigDecimal("2.5"), TickFactory.nicenum(BigDecimal.valueOf(2.3), true));
		Assert.assertEquals(new BigDecimal("2.5"), TickFactory.nicenum(BigDecimal.valueOf(2.5), true));
		Assert.assertEquals(new BigDecimal("2.5"), TickFactory.nicenum(BigDecimal.valueOf(3.), true));
		Assert.assertEquals(new BigDecimal("5"), TickFactory.nicenum(BigDecimal.valueOf(4.3), true));
		Assert.assertEquals(new BigDecimal("5"), TickFactory.nicenum(BigDecimal.valueOf(5.), true));
		Assert.assertEquals(new BigDecimal("5"), TickFactory.nicenum(BigDecimal.valueOf(7.), true));
		Assert.assertEquals(new BigDecimal("1e1"), TickFactory.nicenum(BigDecimal.valueOf(8.3), true));
		Assert.assertEquals(new BigDecimal("1e1"), TickFactory.nicenum(BigDecimal.valueOf(9.7), true));
		Assert.assertEquals(new BigDecimal("1e1"), TickFactory.nicenum(BigDecimal.valueOf(10), true));

		Assert.assertEquals(new BigDecimal("2e308"), TickFactory.nicenum(new BigDecimal("1.6e+308"), true));
		Assert.assertEquals(new BigDecimal("-2e308"), TickFactory.nicenum(new BigDecimal("-1.6e+308"), true));
	}

	@Test
	public void testTicks() {
		testGeneratedBothTicks(0, 1, 11, "0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0");
		testGeneratedBothTicks(-0.5, 0.5, 11, "-0.5", "-0.4", "-0.3", "-0.2", "-0.1", "0.0", "0.1", "0.2", "0.3", "0.4",
				"0.5");

		testGeneratedBothTicks(0, 1, 6, "0.0", "0.2", "0.4", "0.6", "0.8", "1.0");
		testGeneratedLooseTicks(-0.5, 0.5, 7, "-0.6", "-0.4", "-0.2", "0.0", "0.2", "0.4", "0.6");
		testGeneratedTightTicks(-0.5, 0.5, 7, "-0.4", "-0.2", "0.0", "0.2", "0.4");

		testGeneratedBothTicks(0, 1, 5, "0.00", "0.25", "0.50", "0.75", "1.00");
		testGeneratedBothTicks(-0.5, 0.5, 5, "-0.50", "-0.25", "0.00", "0.25", "0.50");

		testGeneratedBothTicks(0, 1, 3, "0.0", "0.5", "1.0");
		testGeneratedBothTicks(-0.5, 0.5, 3, "-0.5", "0.0", "0.5");

		testGeneratedLooseTicks(-1e-2, 1.2, 3, "-1", "0", "1", "2");
		testGeneratedTightTicks(-1e-2, 1.2, 3, "0", "1");
		testGeneratedLooseTicks(-1e-5, 1.2, 3, "-1", "0", "1", "2");
		testGeneratedTightTicks(-1e-5, 1.2, 3, "0", "1");
		testGeneratedLooseTicks(-1e-6, 1.2, 3, "0", "1", "2"); // rounds when
																// difference is
																// too small to
																// see
		testGeneratedTightTicks(-1e-6, 1.2, 3, "0", "1");
		testGeneratedLooseTicks(-1e-16, 1.2, 3, "0", "1", "2");
		testGeneratedTightTicks(-1e-16, 1.2, 3, "0", "1");

		testGeneratedBothTicks(0, 1, 2, "0", "1");
		testGeneratedBothTicks(0, 0.6, 2, "0", "1");
		testGeneratedBothTicks(0, 0.5, 2, "0.0", "0.5");
		testGeneratedBothTicks(0, 0.3, 2, "0.0", "0.5");
		testGeneratedBothTicks(0, 0.25, 2, "0.0", "0.5");
		testGeneratedBothTicks(0, 0.2, 2, "0.0", "0.2");
		testGeneratedBothTicks(0, 0.12, 2, "0.0", "0.2");
		testGeneratedBothTicks(0, 0.1, 2, "0.0", "0.1");
		testGeneratedBothTicks(0, 0.07, 2, "0.0", "0.1");

		testGeneratedBothTicks(-0.5, 0.5, 2, "-0.5", "0.0", "0.5");
		testGeneratedBothTicks(-0.1, 0.1, 2, "-0.1", "0.0", "0.1");

		testGeneratedLooseTicks(0, 695, 8, "0", "200", "400", "600", "800");
		testGeneratedTightTicks(0, 695, 8, "0", "200", "400", "600");

		testGeneratedLooseTicks(0, 12, 8, "0.0", "2.5", "5.0", "7.5", "10.0", "12.5");
		testGeneratedTightTicks(0, 12, 8, "0.0", "2.5", "5.0", "7.5", "10.0");
	}

	@Test
	public void testExtremeTicks() {
		testGeneratedLooseTicks(0, 0.9e308, 6, "0.0e+00", "2.0e+307", "4.0e+307", "6.0e+307", "8.0e+307", "1.0e+308");
		testGeneratedTightTicks(0, 0.9e308, 6, "0.0e+00", "2.0e+307", "4.0e+307", "6.0e+307", "8.0e+307");

		testGeneratedLooseTicks(false, 0, 1.6e308, 6, "0.0e+00", "5.0e+307", "1.0e+308", "1.5e+308"); // tick positions do not match
		testGeneratedTightTicks(0, 1.6e308, 6, "0.0e+00", "5.0e+307", "1.0e+308", "1.5e+308");

		testGeneratedLooseTicks(-0.9e308, 0.9e308, 5, "-1.0e+308", "-5.0e+307", "0.0e+00", "5.0e+307", "1.0e+308");
		testGeneratedTightTicks(-0.9e308, 0.9e308, 5, "-5.0e+307", "0.0e+00", "5.0e+307");

		testGeneratedLooseTicks(-1.6e308, 1.6e308, 5, "-1e+308", "0e+00", "1e+308");
		testGeneratedTightTicks(-1.6e308, 1.6e308, 5, "-1e+308", "0e+00", "1e+308");
	}

	@Test
	public void testLargeChangesTicks() {
		testGeneratedLooseTicks(1234, 1244, 4, "1230", "1235", "1240", "1245");
		testGeneratedTightTicks(1234, 1244, 4, "1235", "1240");
		testGeneratedLooseTicks(1234, 1334, 4, "1200", "1250", "1300", "1350");
		testGeneratedTightTicks(1234, 1334, 4, "1250", "1300");
		testGeneratedLooseTicks(1234, 2234, 4, "1000", "1500", "2000", "2500");
		testGeneratedTightTicks(1234, 2234, 4, "1500", "2000");

		testGeneratedLooseTicks(-1234, 1244, 4, "-2000", "0", "2000");
		// testGeneratedTightTicks(-1234, 1244, 4, "0", "2000"); // test does
		// not work when reversed
		testGeneratedLooseTicks(-1234, 1334, 4, "-2000", "0", "2000");
		testGeneratedLooseTicks(-1234, 2234, 4, "-2000", "0", "2000", "4000");

		testGeneratedLooseTicks(123400, 124400, 4, "123000", "123500", "124000", "124500");
		testGeneratedLooseTicks(123400, 133400, 4, "120000", "125000", "130000", "135000");
		testGeneratedLooseTicks(123400, 223400, 4, "100000", "150000", "200000", "250000");

		testGeneratedLooseTicks(-123400, 124400, 4, "-200000", "0", "200000");
		testGeneratedLooseTicks(-123400, 133400, 4, "-200000", "0", "200000");
		testGeneratedLooseTicks(-123400, 223400, 4, "-200000", "0", "200000", "400000");
	}

	@Test
	public void testCloseTicks() {
		int[] power = { 0, -20, 20 };

		for (int p : power) {
			double s = Math.pow(10, p);
			System.out.println("Scale: " + s);
			testGeneratedLooseTicks(0.123456789005 * s, 0.1234567891 * s, 6, scale("0.12345678900", p),
					scale("0.12345678902", p), scale("0.12345678904", p), scale("0.12345678906", p),
					scale("0.12345678908", p), scale("0.12345678910", p));
			testGeneratedTightTicks(0.123456789005 * s, 0.1234567891 * s, 6, scale("0.12345678902", p),
					scale("0.12345678904", p), scale("0.12345678906", p), scale("0.12345678908", p),
					scale("0.12345678910", p));

			testGeneratedLooseTicks(-0.123456789005 * s, 0.1234567891 * s, 7, scale("-0.2", p), scale("-0.1", p),
					scale("0.0", p), scale("0.1", p), scale("0.2", p));
			testGeneratedTightTicks(-0.123456789005 * s, 0.1234567891 * s, 7, scale("-0.1", p), scale("0.0", p),
					scale("0.1", p));

			testGeneratedLooseTicks(0.123456789012305 * s, 0.123456789012383 * s, 6, scale("0.12345678901230", p),
					scale("0.12345678901232", p), scale("0.12345678901234", p), scale("0.12345678901236", p),
					scale("0.12345678901238", p), scale("0.12345678901240", p));
			testGeneratedTightTicks(0.123456789012305 * s, 0.123456789012383 * s, 6, scale("0.12345678901232", p),
					scale("0.12345678901234", p), scale("0.12345678901236", p), scale("0.12345678901238", p));

			testGeneratedLooseTicks(-0.123456789012305 * s, 0.123456789012383 * s, 7, scale("-0.2", p),
					scale("-0.1", p), scale("0.0", p), scale("0.1", p), scale("0.2", p));
			testGeneratedTightTicks(-0.123456789012305 * s, 0.123456789012383 * s, 7, scale("-0.1", p), scale("0.0", p),
					scale("0.1", p));

			// this is on the limit!
			testGeneratedLooseTicks(false, 0.12345678901234505 * s, 0.12345678901234583 * s, 6, scale("0.1234567890123450", p),
					scale("0.1234567890123452", p), scale("0.1234567890123454", p), scale("0.1234567890123456", p),
					scale("0.1234567890123458", p), scale("0.1234567890123460", p));  // tick positions do not match
			testGeneratedTightTicks(0.12345678901234505 * s, 0.12345678901234583 * s, 6, scale("0.1234567890123452", p),
					scale("0.1234567890123454", p), scale("0.1234567890123456", p), scale("0.1234567890123458", p));

			testGeneratedLooseTicks(-0.12345678901234505 * s, 0.12345678901234583 * s, 7, scale("-0.2", p),
					scale("-0.1", p), scale("0.0", p), scale("0.1", p), scale("0.2", p));
			testGeneratedTightTicks(-0.12345678901234505 * s, 0.12345678901234583 * s, 7, scale("-0.1", p),
					scale("0.0", p), scale("0.1", p));

			// this is past the limit!
			testGeneratedLooseTicks(-0.12345678901234561 * s, 0.12345678901234569 * s, 7, scale("-0.2", p),
					scale("-0.1", p), scale("0.0", p), scale("0.1", p), scale("0.2", p));
			testGeneratedTightTicks(-0.12345678901234561 * s, 0.12345678901234569 * s, 7, scale("-0.1", p),
					scale("0.0", p), scale("0.1", p));

			// when equal
			testGeneratedBothTicks(0.12345678901234561 * s, 0.12345678901234561 * s, 6);
		}

		// this is past the limit! NB the end points do not match given values
		testGeneratedBothTicks(0.12345678901234561, 0.12345678901234569, 6, "0.12345678901234560",
				"0.12345678901234570");
		testGeneratedBothTicks(1.2345678901234561e-20, 1.2345678901234569e-20, 6, "1.2345678901234560e-20",
				"1.2345678901234570e-20");
		testGeneratedBothTicks(1.2345678901234561e20, 1.2345678901234569e20, 6, "1.2345678901234560e+20",
				"1.2345678901234568e+20");
	}

	@Test
	public void testIndexTicks() {
		testGeneratedIndexBasedTicks(0, 1, 4, "0", "1");

		testGeneratedIndexBasedTicks(0, 3, 4, "0", "2");

		testGeneratedIndexBasedTicks(0.1, 1, 4, "1");

		testGeneratedIndexBasedTicks(0.1, 0.9, 4);
	}

	@Test
	public void testLogTicks() {
		testGeneratedLogTicks(true,  1, 2, 4, "1");
		testGeneratedLogTicks(false, 1, 2, 4, "1", "10");

		testGeneratedLogTicks(true,  1, 20, 4, "1", "10");
		testGeneratedLogTicks(false, 1, 20, 4, "1", "10", "100");

		testGeneratedLogTicks(true,  1e-3, 2e2, 7, "0.001", "0.010", "0.100", "1.000", "10.000", "100.000");
		testGeneratedLogTicks(false, 1e-3, 2e2, 7, "0.001", "0.010", "0.100", "1.000", "10.000", "100.000", "1000.000");

		testGeneratedLogTicks(true,  1e-3, 2e2, 3, "0.001", "0.100", "10.000");
		testGeneratedLogTicks(false, 1e-3, 2e2, 3, "0.001", "0.100", "10.000", "1000.000");

		testGeneratedLogTicks(true,  1e-4, 2e2, 3, "1e-04", "1e-01", "1e+02");
		testGeneratedLogTicks(false, 1e-4, 2e2, 3, "1e-04", "1e-01", "1e+02", "1e+05");

		testGeneratedLogTicks(true,  1e-5, 2e4, 3, "1e-05", "1e-01", "1e+03");
		testGeneratedLogTicks(false, 1e-5, 2e4, 3, "1e-05", "1e-01", "1e+03", "1e+07");

		testGeneratedLogTicks(true,  2.3e-5, 4.5e-5, 4, "1e-05");
		testGeneratedLogTicks(false, 2.3e-5, 4.5e-5, 4, "1e-05", "1e-04");

		testGeneratedLogTicks(true,  2.3e5, 4.5e5, 4, "1e+05");
		testGeneratedLogTicks(false, 2.3e5, 4.5e5, 4, "1e+05", "1e+06");

		testGeneratedLogTicks(true,  1.5e-317, 183783, 7, "1e-317", "1e-270", "1e-223", "1e-176", "1e-129", "1e-82",
				"1e-35");
		testGeneratedLogTicks(false, 1.5e-317, 183783, 7, "1e-317", "1e-270", "1e-223", "1e-176", "1e-129", "1e-82",
				"1e-35", "1e+12");

		testGeneratedLogTicks(true,  7.5e-128, 2.1e237, 8, "1e-128", "1e-82", "1e-36", "1e+10", "1e+56", "1e+102",
				"1e+148", "1e+194");
		testGeneratedLogTicks(false, 7.5e-128, 2.1e237, 8, "1e-128", "1e-82", "1e-36", "1e+10", "1e+56", "1e+102",
				"1e+148", "1e+194", "1e+240");

		testGeneratedLogTicks(true,  2.23e-308, 1.79e308, 8, "1e-308", "1e-231", "1e-154", "1e-77", "1e+00", "1e+77",
				"1e+154", "1e+231", "1e+308");
		testGeneratedLogTicks(false, 2.23e-308, 1.79e308, 8, "1e-308", "1e-231", "1e-154", "1e-77", "1e+00", "1e+77",
				"1e+154", "1e+231", "1e+308");

		testGeneratedLogTicks(true,  4.95e-324, 1.79e308, 8, "1e-316", "1e-238", "1e-160", "1e-82", "1e-04", "1e+74",
				"1e+152", "1e+230", "1e+308");
		testGeneratedLogTicks(false, 4.95e-324, 1.79e308, 8, "1e-316", "1e-238", "1e-160", "1e-82", "1e-04", "1e+74",
				"1e+152", "1e+230", "1e+308");

		testGeneratedLogTicks(false, 1e-311, 183783, 1, "1e-311", "1e+06");
		testGeneratedLogTicks(false, 1e-312, 183783, 1, "1e-312", "1e+06");
		testGeneratedLogTicks(false, 1e-313, 183783, 1, "1e-313", "1e+06");
		testGeneratedLogTicks(true, 1e-313, 183783, 1, "1e-313");

		testGeneratedLogTicks(false, 1.01e-311, 183783, 1, "1e-311", "1e+06");
		testGeneratedLogTicks(false, 1.01e-312, 183783, 1, "1e-312", "1e+06");
		testGeneratedLogTicks(false, 1.01e-313, 183783, 1, "1e-313", "1e+06");
		testGeneratedLogTicks(true, 1.01e-313, 183783, 1, "1e-313");

		testGeneratedLogTicks(false, 9.999999999999e-311, 183783, 1, "1e-311", "1e+06");
		testGeneratedLogTicks(false, 9.99999999999e-312, 183783, 1, "1e-312", "1e+06");
		testGeneratedLogTicks(false, 9.9999999999e-313, 183783, 1, "1e-313", "1e+06");
		testGeneratedLogTicks(true, 9.9999999999e-313, 183783, 1, "1e-313");
	}

	private String scale(String s, int p) {
		if (p == 0)
			return s;
		// tweak output by manually manipulating string
		if (s.startsWith("-0.")) {
			if (s.length() < 5)
				return String.format("-%ce%+02d", s.charAt(3), p - 1);
			return String.format("-%c.%se%+02d", s.charAt(3), s.substring(4), p - 1);
		} else if (s.startsWith("0.")) {
			if (s.charAt(2) == '0') { // special case of zero
				return "0e+00";
			}
			if (s.length() < 4)
				return String.format("%ce%+02d", s.charAt(2), p - 1);
			return String.format("%c.%se%+02d", s.charAt(2), s.substring(3), p - 1);
		}
		return String.format("%se%+02d", s, p);
	}

	private void testGeneratedLooseTicks(boolean checkPosition, double lower, double upper, int nTicks, final String... out) {
		testGeneratedTicks(checkPosition, false, lower, upper, nTicks, out);
	}

	private void testGeneratedLooseTicks(double lower, double upper, int nTicks, final String... out) {
		testGeneratedTicks(true, false, lower, upper, nTicks, out);
	}

	private void testGeneratedTightTicks(double lower, double upper, int nTicks, final String... out) {
		testGeneratedTicks(true, true, lower, upper, nTicks, out);
	}

	private void testGeneratedBothTicks(double lower, double upper, int nTicks, final String... out) {
		testGeneratedTicks(true, false, lower, upper, nTicks, out);
		testGeneratedTicks(true, true, lower, upper, nTicks, out);
	}

	private void testGeneratedTicks(final boolean checkPosition, boolean tight, double lower, double upper, int nTicks, final String... out) {
		TickFactory tf = new TickFactory(TickFormatting.autoMode, null);
		String[] values = new String[out.length];
		List<Tick> t;
		List<Tick> ot;

		t = tf.generateTicks(lower, upper, nTicks, true, tight);
		checkTickValues(t, out);

		if (upper != lower) {
			ot = t;
			t = tf.generateTicks(upper, lower, nTicks, true, tight);
			for (int i = 0; i < out.length; i++) {
				values[i] = out[out.length - 1 - i];
			}
			checkTickValues(t, values);
			if (checkPosition && !tight) {
				checkTickPositions(ot, t);
			}
		}

		t = tf.generateTicks(-lower, -upper, nTicks, true, tight);
		for (int i = 0; i < out.length; i++) {
			values[i] = negate(out[i]);
		}
		checkTickValues(t, values);

		if (upper != lower) {
			ot = t;
			t = tf.generateTicks(-upper, -lower, nTicks, true, tight);
			for (int i = 0; i < out.length; i++) {
				values[i] = negate(out[out.length - 1 - i]);
			}
			checkTickValues(t, values);
			if (checkPosition && !tight) {
				checkTickPositions(ot, t);
			}
		}
	}

	private void testGeneratedLogTicks(boolean tight, double lower, double upper, int nTicks, final String... out) {
		TickFactory tf = new TickFactory(TickFormatting.autoMode, null);
		String[] values = new String[out.length];
		List<Tick> t;

		t = tf.generateLogTicks(lower, upper, nTicks, true, tight);
		checkTickValues(t, out);

		if (upper != lower) {
			List<Tick> ot = t;
			t = tf.generateLogTicks(upper, lower, nTicks, true, tight);
			for (int i = 0; i < out.length; i++) {
				values[i] = out[out.length - 1 - i];
			}
			checkTickValues(t, values);
			if (!tight) {
				checkTickPositions(ot, t);
			}
		}
	}

	private void testGeneratedIndexBasedTicks(double lower, double upper, int nTicks, final String... out) {
		TickFactory tf = new TickFactory(TickFormatting.autoMode, null);
		String[] values = new String[out.length];
		List<Tick> t;

		t = tf.generateIndexBasedTicks(lower, upper, nTicks);
		checkTickValues(t, out);

		if (upper != lower) {
			t = tf.generateIndexBasedTicks(upper, lower, nTicks);
			for (int i = 0; i < out.length; i++) {
				values[i] = out[out.length - 1 - i];
			}
			checkTickValues(t, values);
		}
	}

	private static final String MINUS = "-";

	private String negate(String s) {
		if (Double.valueOf(s) == 0)
			return s;
		if (s.startsWith(MINUS)) {
			return s.substring(1);
		}
		return MINUS.concat(s);
	}

	private void checkTickValues(List<Tick> ticks, String[] values) {
		System.out.println(ticks);
		Assert.assertEquals("Number of ticks", values.length, ticks.size());
		int i = 0;
		for (Tick t : ticks) {
			Assert.assertEquals(values[i], t.getText());
			double v = Double.valueOf(values[i++]);
			double verr = v == 0 ? ERROR : ERROR * Math.abs(v);
			Assert.assertEquals(v, t.getValue(), verr);
		}
	}

	private static final double COARSE_ERROR = 1e-3;
	private void checkTickPositions(List<Tick> oTicks, List<Tick> nTicks) {
		int imax = oTicks.size();
		Assert.assertEquals("Number of ticks", imax, nTicks.size());
		for (int i = 0; i < imax; i++) {
			Tick ot = oTicks.get(i);
			Tick nt = nTicks.get(i);
			double v = ot.getPosition();
			double verr = Math.max(v == 0 ? COARSE_ERROR : COARSE_ERROR * Math.abs(v), 1e-6);
			Assert.assertEquals(v, nt.getPosition(), verr);
		}
	}
}
