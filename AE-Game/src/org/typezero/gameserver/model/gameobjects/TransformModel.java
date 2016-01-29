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

package org.typezero.gameserver.model.gameobjects;

import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.model.TransformType;

/**
 * @author Rolandas
 */
public class TransformModel {

	private int modelId;
	private int originalModelId;
	private TransformType originalType;
	private TransformType transformType;
	private int panelId;
	private boolean isActive = false;
	private TribeClass transformTribe;
	private TribeClass overrideTribe;

	public TransformModel(Creature creature) {
		if (creature instanceof Player) {
			this.originalType = TransformType.PC;
		}
		else {
			this.originalType = TransformType.NONE;
		}
		this.originalModelId = creature.getObjectTemplate().getTemplateId();
		this.transformType = TransformType.NONE;
	}

	/**
	 * @return the modelId
	 */
	public int getModelId() {
		if (isActive && modelId > 0)
			return modelId;
		return originalModelId;
	}

	public void setModelId(int modelId) {
		if (modelId == 0 || modelId == originalModelId) {
			modelId = originalModelId;
			isActive = false;
		}
		else {
			this.modelId = modelId;
			isActive = true;
		}
	}

	/**
	 * @return the type
	 */
	public TransformType getType() {
		if (isActive)
			return transformType;
		return originalType;
	}

	public void setTransformType(TransformType transformType) {
		this.transformType = transformType;
	}

	/**
	 * @return the panelId
	 */
	public int getPanelId() {
		if (isActive)
			return panelId;
		return 0;
	}

	public void setPanelId(int id) {
		this.panelId = id;
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the transformTribe
	 */
	public TribeClass getTribe() {
		if (isActive && transformTribe != null)
			return transformTribe;
		return overrideTribe;
	}

	/**
	 * @param transformTribe the transformTribe to set
	 */
	public void setTribe(TribeClass transformTribe, boolean override) {
		if (override)
			this.overrideTribe = transformTribe;
		else
			this.transformTribe = transformTribe;
	}

}
