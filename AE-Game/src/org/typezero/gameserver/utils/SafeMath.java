/*
 * Copyright (c) 2015, TypeZero Engine (game.developpers.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of TypeZero Engine nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.typezero.gameserver.utils;

/**
 * @author MrPoke
 */
public class SafeMath {

	public static int addSafe(int source, int value) throws OverfowException {
		 long s = (long)source+(long)value;
     if (s < Integer.MIN_VALUE ||
         s > Integer.MAX_VALUE) {
         throw new OverfowException(source + " + " + value + " = " + ((long) source + (long) value));
     }
     return (int)s;
	}

	public static long addSafe(long source, long value) throws OverfowException {
		if ((source > 0 && value > Long.MAX_VALUE - source) || (source < 0 && value < Long.MIN_VALUE - source)) {
			throw new OverfowException(source + " + " + value + " = " + ((long) source + (long) value));
		}
		return source + value;
	}

	public static int multSafe(int source, int value) throws OverfowException {
		 long m = ((long)source)*((long)value);
     if (m < Integer.MIN_VALUE ||
         m > Integer.MAX_VALUE) {
         throw new OverfowException(source + " * " + value + " = " + ((long) source * (long) value));
     }
     return (int)m;
	}

	public static long multSafe(long a, long b) throws OverfowException {

		long ret;
		String msg = "overflow: multiply";
		if (a > b) {
			// use symmetry to reduce boundry cases
			ret = multSafe(b, a);
		}
		else {
			if (a < 0) {
				if (b < 0) {
					// check for positive overflow with negative a, negative b
					if (a >= Long.MAX_VALUE / b) {
						ret = a * b;
					}
					else {
						throw new OverfowException(msg);
					}
				}
				else if (b > 0) {
					// check for negative overflow with negative a, positive b
					if (Long.MIN_VALUE / b <= a) {
						ret = a * b;
					}
					else {
						throw new OverfowException(msg);

					}
				}
				else {
					ret = 0;
				}
			}
			else if (a > 0) {
				// check for positive overflow with positive a, positive b
				if (a <= Long.MAX_VALUE / b) {
					ret = a * b;
				}
				else {
					throw new OverfowException(msg);
				}
			}
			else {
				ret = 0;
			}
		}
		return ret;
	}
}
