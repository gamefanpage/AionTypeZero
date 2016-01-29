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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.templates.InstanceCooltime;
import org.typezero.gameserver.services.instance.InstanceService;
import javolution.util.FastMap;

import org.typezero.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import org.typezero.gameserver.utils.PacketSendUtility;
import sun.rmi.runtime.Log;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer
 */
public class PortalCooldownList {

	private Player owner;
	private FastMap<Integer, PortalCooldown> portalCooldowns;
    private static final Logger log = LoggerFactory.getLogger(InstanceCooltime.class);

    /**
	 * @param owner
	 */
	PortalCooldownList(Player owner) {
		this.owner = owner;
	}

	public boolean isPortalUseDisabled(int worldId) {
        if (DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(worldId) == null)
        {
            return false;
        }
        if (portalCooldowns == null || portalCooldowns.isEmpty())
            return false;

        int syncId = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(worldId).getSyncId();
        List<InstanceCooltime> cooltimes = DataManager.INSTANCE_COOLTIME_DATA.getSyncCooltimes(syncId);

        for (InstanceCooltime ic : cooltimes) {
            if (portalCooldowns.containsKey(ic.getId())) {
                PortalCooldown coolDown = portalCooldowns.get(ic.getId());
                    if (coolDown.getCount() == 0)
                    return true;
            }
        }

		return false;
	}



	public PortalCooldown getPortalCooldown(int instanceId) {
        if (portalCooldowns == null || !portalCooldowns.containsKey(instanceId))
            return null;

        return portalCooldowns.get(instanceId);
	}

	public FastMap<Integer, PortalCooldown> getPortalCoolDowns() {
        return portalCooldowns;
	}

	public void setPortalCoolDowns(FastMap<Integer, PortalCooldown> portalCoolDowns) {
		this.portalCooldowns = portalCoolDowns;
	}

	public void addPortalCooldown(int worldId, long useDelay) {
        if (DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(worldId) == null)
            return;

        InstanceCooltime cooltime = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(worldId);

        List<InstanceCooltime> cooltimes = DataManager.INSTANCE_COOLTIME_DATA.getSyncCooltimes(cooltime.getSyncId());

        if (cooltimes == null)
        {
            log.warn("Instance cooldown NPE: " + worldId);
        }

        if (portalCooldowns == null) {
            portalCooldowns = new FastMap<Integer, PortalCooldown>();
        }
        for (InstanceCooltime ct : cooltimes)
        {
            if (!portalCooldowns.containsKey(ct.getId())) {
                portalCooldowns.put(ct.getId(), new PortalCooldown(ct.getId(), useDelay, ct.getCount() - 1));
            } else {
                if (InstanceService.getRegisteredInstance(worldId, owner.getObjectId()) == null) {
                    portalCooldowns.get(ct.getId()).minusOne();
                    if (portalCooldowns.get(ct.getId()).getCount() <= ct.getCount())
                        portalCooldowns.get(ct.getId()).setCoolTime(useDelay);
                }
            }
            sendPortInfo(ct.getId());
        }
	}

    public void AddPortCount(int syncId)
    {
        List<InstanceCooltime> cooltimes = DataManager.INSTANCE_COOLTIME_DATA.getSyncCooltimes(syncId);
        for (InstanceCooltime ct : cooltimes) {
            if (portalCooldowns.containsKey(ct.getId())) {
                (portalCooldowns.get(ct.getId())).addCount();
                if (portalCooldowns.get(ct.getId()).getCount() > ct.getCount()) {
                    portalCooldowns.get(ct.getId()).setCoolTime(0);
                }
            } else {
                portalCooldowns.put(ct.getId(), new PortalCooldown(ct.getId(), 0, ct.getCount() + 1));
            }
            sendPortInfo(ct.getId());
        }
    }

    private void sendPortInfo(int instanceId) {
        if (owner.isInTeam()) {
            owner.getCurrentTeam().sendPacket(new SM_INSTANCE_INFO(owner, instanceId, false));
        } else {
            PacketSendUtility.sendPacket(owner, new SM_INSTANCE_INFO(owner, instanceId, false));
        }
    }

	public void removePortalCoolDown(int instanceId) {
		if (portalCooldowns != null) {
			portalCooldowns.remove(instanceId);
		}
	}

	/**
	 * @return
	 */
	public boolean hasCooldowns() {
		return portalCooldowns != null && portalCooldowns.size() > 0;
	}

	/**
	 * @return
	 */
	public int size() {
		return portalCooldowns != null ? portalCooldowns.size() : 0;
	}

}
