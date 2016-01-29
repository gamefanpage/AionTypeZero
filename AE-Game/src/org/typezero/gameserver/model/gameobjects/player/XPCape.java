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

package org.typezero.gameserver.model.gameobjects.player;


/**
 * @author MrPoke
 *
 */
public enum XPCape {
	_0(0),
	_1(130),	//retail
	_2(284),	//retail
	_3(418),	//retail
	_4(561),	//retail
	_5(721),	//retail
	_6(970),
	_7(1200),
	_8(1450),
	_9(1750),
	_10(2007),	//retail
	_11(2362),	//retail
	_12(2592),
	_13(2909),
	_14(3336),
	_15(3899),
	_16(4896),
	_17(5555),
	_18(6721),
	_19(8169),
	_20(9947),
	_21(12108),
	_22(14035), //retail
	_23(17820),
	_24(21506),
	_25(25847),
	_26(30924),
	_27(36829),
	_28(43659),
	_29(51517),
	_30(60517),
	_31(70779),
	_32(82430),
	_33(95606),
	_34(110455),
	_35(127128),
	_36(145791),
	_37(166615),
	_38(189783),
	_39(215488),
	_40(243932),
	_41(275329),
	_42(309904),
	_43(347891),
	_44(389536),
	_45(435099),
	_46(484848),
	_47(539067),
	_48(598049),
	_49(662103),
	_50(731547),
	_51(806716),
	_52(887956),
	_53(975628),
	_54(1070106),
	_55(1171780),
	_56(1280906),
	_57(1397740),
	_58(1522538),
	_59(1655556),
	_60(1797050),
	_61(1947276),
	_62(2106490),
	_63(2274948),
	_64(2452906),
	_65(2640620);

	private int id;

	private XPCape(int id) {
		this.id = id;
	}

	public int value() {
		return id;
	}
}
