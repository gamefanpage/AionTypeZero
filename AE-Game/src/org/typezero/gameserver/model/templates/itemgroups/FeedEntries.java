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

package org.typezero.gameserver.model.templates.itemgroups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 */

public final class FeedEntries {

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "FeedFluid")
	public static class FeedFluid extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "FeedArmor")
	public static class FeedArmor extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "FeedThorn")
	public static class FeedThorn extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "FeedBalaur")
	public static class FeedBalaur extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "FeedBone")
	public static class FeedBone extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "FeedSoul")
	public static class FeedSoul extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "FeedExclude")
	public static class FeedExclude extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "StinkingJunk")
	public static class StinkingJunk extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "HealthyFoodAll")
	public static class HealthyFoodAll extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "HealthyFoodSpicy")
	public static class HealthyFoodSpicy extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "AetherPowderBiscuit")
	public static class AetherPowderBiscuit extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "AetherCrystalBiscuit")
	public static class AetherCrystalBiscuit extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "AetherGemBiscuit")
	public static class AetherGemBiscuit extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "PoppySnack")
	public static class PoppySnack extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "PoppySnackTasty")
	public static class PoppySnackTasty extends ItemRaceEntry {
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "PoppySnackNutritious")
	public static class PoppySnackNutritious extends ItemRaceEntry {
	}
}
