/**********************************************************************************************************************
 *
 * WARNING:
 * Copyright Ⓒ 2016 by D.DeRuiter
 * Do not use, modify, or distribute in any way without express written consent.
 *
 * PROJECT:
 * QMBES (Quine McCluskey Boolean Expression Simplifier)
 *
 * DESCRIPTION:
 * Test class for storing custom Boolean expressions.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.test;

/**
 * Class for test Boolean expressions.
 *
 * Only used for testing purposes.
 * (In the future, could make JUnit tests for each of these expressions.)
 *
 * Date:       Author:        Change:
 * --------    -----------    ---------------
 * 04/26/16    D. DeRuiter    Initial coding.
 */
public class BooleanExpressions
{
	/**
	 * Retrieves a custom Boolean expression.
	 *
	 * @return a custom Boolean expression.
	 */
	@SuppressWarnings("unused")
	public static String getTestBoolExpression()
	{
		String expression0 = "";
		String expression1 = "xyz + xy'z + x'yz + x'y'z + x'y'z'"; // Math book problem
		String expression2 = "x'y'z' + xy'z + x'yz + xyz + x'y'z";
		String expression3 = "yz + x'z + y'z' + y + xz";
		String expression4 = "xyz + x'yz + xy'z + xyz' + x'y'z + xy'z' + x'yz' + x'y'z'"; // Goes to step 4
		String expression5 = "x'yz + xy'z + xyz' + x'y'z + xy'z' + x'yz' + x'y'z'";
		String expression6 = "xyz + x'yz + xy'z' + x'yz' + x'y'z'"; // Optional Prime implication chart
		String expression7 = "x'y'z' + x'y'z + xy'z + xyz'";
		String expression8 = "abcde + abe + c' + bd + b'd'";
		String expression9 = "abcdefg + abcd'efg + ab'cd'efg' + a'b'c'd'e'fg' + a'b'cdefg + a'b'c'defg "
			+ "+ a'b'c'd'efg' + a'b'c'd'efg + a'b'cd'e'fg' + ab'cd'e'fg' + a'b'cd'e'fg' + a'b'c'd'e'fg";
		String expression10 = "xyz + xyz";
		String expression11 = "wxyz + wxyz' + wx'yz' + w'xyz' + w'xy'z + w'xy'z' + w'x'yz + w'x'yz'";
		String expression12 = "wxyz + wxyz' + wx'yz' + w'xyz' + z'xy'z + w'xy'z' + w'x'yz + w'x'yz'"; // Error
		String expression13 = "a'b' + a'c' + b'c + bc' + ac + ab";
		String expression14 = "abcdefg + abcd'efg + ab'cd'efg' + a'b'c'd'e'fg' + a'b'cdefg + a'b'c'defg "
				+ "+ a'b'c'd'efg' + a'b'c'd'efg + a'b'cd'e'fg' + ab'cd'e'fg'";
		String expression15 = "a'b'c'd' + a'cd + abc' + abd' + bcd";
		String expression16 = "a'b'c'd + a'b'cd + a'bc'd' + a'bcd' + ab'c'd' + ab'cd' + abc'd + abcd' + abcd";

		// Single optional term row used to produce error
		String expression17 = "a'bc'de + a'b'c'de' + ab'cde' + a'b'c'de + a'b'cd'e + a'bc'd'e + ab'cd'e' + abc'd'e' + a'bcd'e + a'b'c'd'e + abcde + ab'c'd'e'";

		// Solution to #18 = abc-e' + abc'-e + a'bcd'e + a-c'de' + a'b'cd- + a'b'c'd'e + a-cde
		String expression18 = "a'b'cde + a'b'cde' + abc'de' + a'b'c'd'e + abc'd'e + abcd'e' + abcde' + abc'de + a'bcd'e + ab'cde + abcde + ab'c'de'";

		// Very Long Time
		String expression19 = "abcde + abc'de + ab'c'de + a'bc'de + ab'c'd'e' + a'bcd'e' + a'bc'd'e' + ab'cde + a'b'cd'e' + a'bcde' + a'b'c'd'e' + abcd'e' + abc'd'e' + abc'd'e + ab'c'd'e + ab'cd'e + a'b'cd'e + a'bcd'e + a'bcde + a'b'cde' + abcde' + ab'c'de' + abcd'e + a'b'c'de' + ab'cde' + a'b'c'd'e";

		// Short Time
		String expression20 = "ab'cde + a'b'cd'e' + a'bcde' + a'b'c'd'e' + abcd'e' + abc'd'e' + abc'd'e + ab'c'd'e + ab'cd'e + a'b'cd'e + a'bcd'e + a'bcde + a'b'cde' + abcde' + ab'c'de' + abcd'e + a'b'c'de' + ab'cde' + a'b'c'd'e";

		String expression21 = "a'b'cd' + a'bc'd' + ab'c'd' + ab'cd' + ab'cd + abc'd' + abc'd + abcd";
		String expression22 = "a'b'c + a'bc' + a'bc + ab'c' + abc'";

		// Medium Time
		String expression23 = "abcd'e + ab'cde + a'b'cd'e' + ab'cd'e + a'bc'd'e' + a'bc'd'e + ab'c'd'e + a'bcd'e + a'b'c'de' + ab'c'd'e' + ab'cde' + a'bcde + ab'c'de + abcde' + a'b'cd'e + abcde + abc'd'e + abcd'e' + a'b'c'd'e' + abc'de' + a'bcde' + abc'de + a'b'cde' + a'bc'de' + a'b'c'de + ab'c'de' + a'b'c'd'e";

		// Minimal Solution: -b'c'-- + abc-- + a'b'-d'- + ---de' + a'-c'd'- + -bc-e + a---e
		// Minimal Solution: -b'c'-- + abc-- + a'b'--e' + ---d'e + a'-c'-e' + -bcd- + a--d-
		String expression24 = "abcd'e + ab'cde + a'b'cd'e' + ab'cd'e + a'bc'd'e' + a'bc'd'e + ab'c'd'e + a'bcd'e + a'b'c'de' + ab'c'd'e' + ab'cde' + a'bcde + ab'c'de + abcde' + a'b'cd'e + abcde + abc'd'e + abcd'e' + a'b'c'd'e' + abc'de' + a'bcde' + abc'de + a'b'cde' + a'bc'de' + a'b'c'de + ab'c'de' + a'b'c'd'e";

		// Minimal Solution: a'-c + a-c' + a'b'-
		// Minimal Solution: a'-c + a-c' + -b'c'
		String expression25 = "a'b'c + a'b'c' + ab'c' + abc' + a'bc";

		String expression26 = "a'b'c'd + a'b'cd' + a'b'cd + a'bc'd' + a'bc'd + a'bcd + ab'cd' + abc'd' + abc'd + abcd'";
		String expression27 = "abc'd'e' + abcd'e' + ab'cd'e' + a'b'cde' + a'bcde + abc'de' + abcde' + a'bc'd'e + ab'c'd'e' + a'bc'de' + a'b'c'd'e' + a'bc'de + a'bcd'e' + a'b'cd'e' + ab'c'd'e + a'bcde' + a'b'c'de + a'b'cde + a'b'c'd'e + abc'd'e";

		// Long Time
		String expression28 = "abcde + abc'de + ab'c'de + a'bc'de + ab'c'd'e' + a'bcd'e' + a'bc'd'e' + ab'cde + a'bcde' + a'b'c'd'e' + abcd'e' + abc'd'e' + abc'd'e + ab'c'd'e + ab'cd'e + a'b'cd'e + a'bcd'e + a'bcde + a'b'cde' + abcde' + ab'c'de' + abcd'e + a'b'c'de' + ab'cde' + a'b'c'd'e";

		String testExpression = expression25;

		return testExpression;
	}
}
