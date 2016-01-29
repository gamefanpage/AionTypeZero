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

package org.typezero.gameserver.model.items;

//added by Blackhive original credits to xTr 2.0.0.5 mod by Tomate

public class ItemMask {

	public static final int LIMIT_ONE = 1;
	public static final int TRADEABLE = (1 << 1);
	public static final int SELLABLE = (1 << 2);
	public static final int STORABLE_IN_WH = (1 << 3);
	public static final int STORABLE_IN_AWH = (1 << 4);
	public static final int STORABLE_IN_LWH = (1 << 5);
	public static final int BREAKABLE = (1 << 6);
	public static final int SOUL_BOUND = (1 << 7);
	public static final int REMOVE_LOGOUT = (1 << 8);
	public static final int NO_ENCHANT = (1 << 9);
	public static final int CAN_PROC_ENCHANT = (1 << 10);
	public static final int CAN_COMPOSITE_WEAPON = (1 << 11);
	public static final int REMODELABLE = (1 << 12);
	public static final int CAN_SPLIT = (1 << 13);
	public static final int DELETABLE = (1 << 14);
	public static final int DYEABLE = (1 << 15);
	public static final int CAN_AP_EXTRACT = (1 << 16); // not sure
	public static final int CAN_POLISH = (1 << 17); // not sure
}
