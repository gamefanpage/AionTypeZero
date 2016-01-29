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

package org.typezero.gameserver.controllers.observer;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.flyring.FlyRing;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.utils3d.Point3D;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.utils.MathUtil;

/**
 * @author xavier, Source
 */
public class FlyRingObserver extends ActionObserver {

	private Player player;

	private FlyRing ring;

	private Point3D oldPosition;

	SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(260);

	public FlyRingObserver() {
		super(ObserverType.MOVE);
		this.player = null;
		this.ring = null;
		this.oldPosition = null;
	}

	public FlyRingObserver(FlyRing ring, Player player) {
		super(ObserverType.MOVE);
		this.player = player;
		this.ring = ring;
		this.oldPosition = new Point3D(player.getX(), player.getY(), player.getZ());
	}

	@Override
	public void moved() {
		Point3D newPosition = new Point3D(player.getX(), player.getY(), player.getZ());
		boolean passedThrough = false;

		if (ring.getPlane().intersect(oldPosition, newPosition)) {
			Point3D intersectionPoint = ring.getPlane().intersection(oldPosition, newPosition);
			if (intersectionPoint != null) {
				double distance = Math.abs(ring.getPlane().getCenter().distance(intersectionPoint));

				if (distance < ring.getTemplate().getRadius()) {
					passedThrough = true;
				}
			}
			else {
				if (MathUtil.isIn3dRange(ring, player, ring.getTemplate().getRadius())) {
					passedThrough = true;
				}
			}
		}

		if (passedThrough) {
			if (ring.getTemplate().getMap() == 400010000 || isQuestactive() || isInstancetactive()) {
				Effect speedUp = new Effect(player, player, skillTemplate, skillTemplate.getLvl(), 0);
				speedUp.initialize();
				speedUp.addAllEffectToSucess();
				speedUp.applyEffect();
			}

			QuestEngine.getInstance().onPassFlyingRing(new QuestEnv(null, player, 0, 0), ring.getName());
		}

		oldPosition = newPosition;
	}

	private boolean isInstancetactive() {
		return ring.getPosition().getWorldMapInstance().getInstanceHandler().onPassFlyingRing(player, ring.getName());
	}

	private boolean isQuestactive() {
		int questId = player.getRace() == Race.ASMODIANS ? 2042 : 1044;
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null)
			return false;

		return qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) >= 2 && qs.getQuestVarById(0) <= 8;
	}

}