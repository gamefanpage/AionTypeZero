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

package org.typezero.gameserver.model.templates.item.actions;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemActions")
public class ItemActions {

	@XmlElements({
		@XmlElement(name = "skilllearn", type = SkillLearnAction.class),
		@XmlElement(name = "extract", type = ExtractAction.class),
		@XmlElement(name = "skilluse", type = SkillUseAction.class),
		@XmlElement(name = "enchant", type = EnchantItemAction.class),
		@XmlElement(name = "queststart", type = QuestStartAction.class),
		@XmlElement(name = "dye", type = DyeAction.class),
		@XmlElement(name = "craftlearn", type = CraftLearnAction.class),
		@XmlElement(name = "toypetspawn", type = ToyPetSpawnAction.class),
		@XmlElement(name = "decompose", type = DecomposeAction.class),
		@XmlElement(name = "titleadd", type = TitleAddAction.class),
		@XmlElement(name = "learnemotion", type = EmotionLearnAction.class),
		@XmlElement(name = "read", type = ReadAction.class),
		@XmlElement(name = "fireworkact", type = FireworksUseAction.class),
		@XmlElement(name = "instancetimeclear", type = InstanceTimeClear.class),
		@XmlElement(name = "expandinventory", type = ExpandInventoryAction.class),
		@XmlElement(name = "animation", type = AnimationAddAction.class),
		@XmlElement(name = "cosmetic", type = CosmeticItemAction.class),
		@XmlElement(name = "charge", type = ChargeAction.class),
		@XmlElement(name = "ride", type = RideAction.class),
		@XmlElement(name = "houseobject", type = SummonHouseObjectAction.class),
		@XmlElement(name = "housedeco", type = DecorateAction.class),
		@XmlElement(name = "assemble", type = AssemblyItemAction.class),
		@XmlElement(name = "adoptpet", type = AdoptPetAction.class),
		@XmlElement(name = "apextract", type = ApExtractAction.class),
		@XmlElement(name = "remodel", type = RemodelAction.class),
		@XmlElement(name = "expextract", type = ExpExtractAction.class),
		@XmlElement(name = "expreturn", type = ReturnExpAction.class),
		@XmlElement(name = "polish", type = PolishAction.class),
		@XmlElement(name = "composition", type = CompositionAction.class),
		@XmlElement(name = "pack", type = PackAction.class),
		@XmlElement(name = "stenchant", type=AuthorizeAction.class),
		@XmlElement(name = "multi_return_item", type = MultiReturnAction.class),
		@XmlElement(name = "reidentiry", type=AuthorizeAction.class),
		@XmlElement(name = "godsocket", type = GodstoneAction.class)})

	protected List<AbstractItemAction> itemActions;

	/**
	 * Gets the value of the itemActions property. Objects of the following type(s) are allowed in the list
	 * {@link SkillLearnAction } {@link SkillUseAction }
	 */
	public List<AbstractItemAction> getItemActions() {
		if (itemActions == null) {
			itemActions = new ArrayList<AbstractItemAction>();
		}
		return this.itemActions;
	}

	public List<ToyPetSpawnAction> getToyPetSpawnActions() {
		List<ToyPetSpawnAction> result = new ArrayList<ToyPetSpawnAction>();
		if (itemActions == null)
			return result;

		for (AbstractItemAction action : itemActions)
			if (action instanceof ToyPetSpawnAction)
				result.add((ToyPetSpawnAction) action);
		return result;
	}

	public EnchantItemAction getEnchantAction() {
		if (itemActions == null)
			return null;
		for (AbstractItemAction action : itemActions) {
			if (action instanceof EnchantItemAction)
				return (EnchantItemAction) action;
		}
		return null;
	}

	public SummonHouseObjectAction getHouseObjectAction() {
		if (itemActions == null)
			return null;
		for (AbstractItemAction action : itemActions) {
			if (action instanceof SummonHouseObjectAction)
				return (SummonHouseObjectAction) action;
		}
		return null;
	}

	public CraftLearnAction getCraftLearnAction() {
		if (itemActions == null)
			return null;
		for (AbstractItemAction action : itemActions) {
			if (action instanceof CraftLearnAction)
				return (CraftLearnAction) action;
		}
		return null;
	}

	public DecorateAction getDecorateAction() {
		if (itemActions == null)
			return null;
		for (AbstractItemAction action : itemActions) {
			if (action instanceof DecorateAction)
				return (DecorateAction) action;
		}
		return null;
	}

	public DyeAction getDyeAction() {
		if (itemActions == null)
			return null;
		for (AbstractItemAction action : itemActions) {
			if (action instanceof DyeAction)
				return (DyeAction) action;
		}
		return null;
	}

	public AdoptPetAction getAdoptPetAction() {
		if (itemActions == null)
			return null;
		for (AbstractItemAction action : itemActions) {
			if (action instanceof AdoptPetAction)
				return (AdoptPetAction) action;
		}
		return null;
	}

	public RemodelAction getRemodelAction() {
		if (itemActions == null)
			return null;
		for (AbstractItemAction action : itemActions) {
			if (action instanceof RemodelAction)
				return (RemodelAction) action;
		}
		return null;
	}

	public PolishAction getPolishAction() {
		if (itemActions == null)
			return null;
		for (AbstractItemAction action : itemActions) {
			if (action instanceof PolishAction)
				return (PolishAction) action;
		}
		return null;
	}

}
