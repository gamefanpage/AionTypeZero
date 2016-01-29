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

package org.typezero.gameserver.utils.stats.enums;

/**
 * @author ATracer
 */
public enum MAXHP {
	WARRIOR(1.1688f, 1.1688f, 284),
	GLADIATOR(1.3393f, 48.246f, 342),
	TEMPLAR(1.3288f, 51.878f, 281),
	SCOUT(1.0297f, 40.823f, 219),
	ASSASSIN(1.0488f, 40.38f, 222),
	RANGER(0.5f, 38.5f, 133),
	MAGE(0.7554f, 29.457f, 132),
	SORCERER(0.6352f, 24.852f, 112),
	SPIRIT_MASTER(1, 20.6f, 157),
	PRIEST(1.0303f, 40.824f, 201),
	CLERIC(0.9277f, 35.988f, 229),
	CHANTER(0.9277f, 35.988f, 229),
	ENGINEER(1.0854f, 31.851999f, 219),
	GUNNER(0.7554f, 26.534f, 219),
	ARTIST(1.0854f, 31.851999f, 179),
	BARD(0.7554f, 26.534f, 179),
	RIDER(0.7554f, 26.534f, 219);

	private float a;
	private float b;
	private float c;

	private MAXHP(float a, float b, float c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public int getMaxHpFor(int level) {
		return Math.round(a * (level - 1) * (level - 1) + b * (level - 1) + c);
	}
}
