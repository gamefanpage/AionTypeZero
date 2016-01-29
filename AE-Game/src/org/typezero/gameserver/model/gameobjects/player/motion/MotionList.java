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

package org.typezero.gameserver.model.gameobjects.player.motion;

import com.aionemu.commons.database.dao.DAOManager;
import javolution.util.FastMap;
import org.typezero.gameserver.dao.MotionDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_MOTION;
import org.typezero.gameserver.taskmanager.tasks.ExpireTimerTask;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.Collections;
import java.util.Map;

/**
 * @author MrPoke
 *
 */
public class MotionList {

	private Player owner;
	private Map<Integer, Motion> activeMotions;
	private Map<Integer, Motion> motions;

	/**
	 * @param owner
	 */
	public MotionList(Player owner) {
		this.owner = owner;
	}

	/**
	 * @return the activeMotions
	 */
	public Map<Integer, Motion> getActiveMotions() {
		if (activeMotions == null)
			return Collections.emptyMap();
		return activeMotions;
	}

	/**
	 * @return the motions
	 */
	public Map<Integer, Motion> getMotions() {
		if (motions == null)
			return Collections.emptyMap();
		return motions;
	}

	public void add(Motion motion, boolean persist){
		if (motions == null)
			motions = new FastMap<Integer, Motion>();
		if (motions.containsKey(motion.getId()) && motion.getExpireTime() == 0){
			remove(motion.getId());
		}
		motions.put(motion.getId(), motion);
		if (motion.isActive()){
			if (activeMotions == null)
				activeMotions = new FastMap<Integer, Motion>();
			Motion old = activeMotions.put(Motion.motionType.get(motion.getId()), motion);
			if (old != null){
				old.setActive(false);
				DAOManager.getDAO(MotionDAO.class).updateMotion(owner.getObjectId(), old);
			}
		}
		if (persist){
			if (motion.getExpireTime() != 0)
				ExpireTimerTask.getInstance().addTask(motion, owner);
			DAOManager.getDAO(MotionDAO.class).storeMotion(owner.getObjectId(), motion);
		}
	}

	public boolean remove(int motionId){
		Motion motion = motions.remove(motionId);
		if (motion != null){
			PacketSendUtility.sendPacket(owner, new SM_MOTION((short) motionId));
			DAOManager.getDAO(MotionDAO.class).deleteMotion(owner.getObjectId(), motionId);
			if (motion.isActive()){
				activeMotions.remove(Motion.motionType.get(motionId));
				return true;
			}
		}
		return false;
	}

	public void setActive(int motionId, int motionType){
		if (motionId != 0)
		{
			Motion motion = motions.get(motionId);
			if (motion == null || motion.isActive())
				return;
			if (activeMotions == null)
				activeMotions = new FastMap<Integer, Motion>();
			Motion old = activeMotions.put(motionType, motion);
			if (old != null){
				old.setActive(false);
				DAOManager.getDAO(MotionDAO.class).updateMotion(owner.getObjectId(), old);
			}
			motion.setActive(true);
			DAOManager.getDAO(MotionDAO.class).updateMotion(owner.getObjectId(), motion);
		}
		else if (activeMotions != null){
			Motion old = activeMotions.remove(motionType);
			if (old == null)
				return; //TODO packet hack??
			old.setActive(false);
			DAOManager.getDAO(MotionDAO.class).updateMotion(owner.getObjectId(), old);
		}
		PacketSendUtility.sendPacket(owner, new SM_MOTION((short) motionId, (byte)motionType));
		PacketSendUtility.broadcastPacket(owner, new SM_MOTION(owner.getObjectId(), activeMotions), true);
	}
}
