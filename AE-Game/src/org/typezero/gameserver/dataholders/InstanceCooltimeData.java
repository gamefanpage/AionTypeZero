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

package org.typezero.gameserver.dataholders;

import javolution.util.FastMap;
import org.joda.time.DateTime;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.InstanceCooltime;
import org.typezero.gameserver.services.instance.InstanceService;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author VladimirZ
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "instance_cooltimes")
public class InstanceCooltimeData {

	@XmlElement(name = "instance_cooltime", required = true)
	protected List<InstanceCooltime> instanceCooltime;

	private FastMap<Integer, InstanceCooltime> instanceCooltimes = new FastMap<Integer, InstanceCooltime>();
	private HashMap<Integer, Integer> syncIdToMapId = new HashMap<Integer, Integer>();
    private FastMap<Integer, List<InstanceCooltime>> syncIdToInstances = new FastMap<Integer, List<InstanceCooltime>>();
    private FastMap<Integer, InstanceCooltime> instanceIdToCooltime = new FastMap<Integer, InstanceCooltime>();
	/**
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (InstanceCooltime tmp : instanceCooltime) {
			instanceCooltimes.put(tmp.getWorldId(), tmp);
            instanceIdToCooltime.put(tmp.getId(), tmp);
			syncIdToMapId.put(tmp.getSyncId(), tmp.getWorldId());
            if (tmp.getCount() > 0 && tmp.getSyncId() > 0) {
                if (!syncIdToInstances.containsKey(tmp.getSyncId()))
                    syncIdToInstances.put(tmp.getSyncId(), new ArrayList<InstanceCooltime>());
                syncIdToInstances.get(tmp.getSyncId()).add(tmp);
            }
		}
		instanceCooltime.clear();
	}

    public InstanceCooltime getInstanceCooltimeByInstanceId(int instanceId)
    {
        return instanceIdToCooltime.get(instanceId);
    }

	public InstanceCooltime getInstanceCooltimeByWorldId(int worldId) {
		return instanceCooltimes.get(worldId);
	}

    public FastMap<Integer, InstanceCooltime> getInstanceCooltimes()
    {
        return instanceIdToCooltime;
    }

    public List<InstanceCooltime> getSyncCooltimes(int syncid) {
        return syncIdToInstances.get(syncid);
    }

	public int getWorldId(int syncId)
	{
		if (!syncIdToMapId.containsKey(syncId))
			return 0;
		return syncIdToMapId.get(syncId);
	}

	public long getInstanceEntranceCooltimeById(Player player, int syncId) {
		if (!syncIdToMapId.containsKey(syncId))
			return 0;
		return getInstanceEntranceCooltime(player, syncIdToMapId.get(syncId));
	}

	public long getInstanceEntranceCooltime(Player player, int worldId) {
		int instanceCooldownRate = InstanceService.getInstanceRate(player, worldId);
		long instanceCoolTime = 0;
		InstanceCooltime clt = getInstanceCooltimeByWorldId(worldId);
		if (clt != null) {
			instanceCoolTime = clt.getEntCoolTime();
			if (clt.getCoolTimeType().isDaily()) {
				DateTime now = DateTime.now();
				DateTime repeatDate = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), (int) (instanceCoolTime / 100), 0, 0);
				if (now.isAfter(repeatDate)) {
					repeatDate = repeatDate.plusHours(24);
					instanceCoolTime = repeatDate.getMillis();
				}
				else {
					instanceCoolTime = repeatDate.getMillis();
				}
			}
			else if (clt.getCoolTimeType().isWeekly()) {
				String[] days = clt.getTypeValue().split(",");
				instanceCoolTime = getUpdateHours(days, (int) (instanceCoolTime / 100));
			}
			else {
				instanceCoolTime = System.currentTimeMillis() + (instanceCoolTime * 60 * 1000);
			}
		}
		if (instanceCooldownRate != 1) {
			instanceCoolTime = System.currentTimeMillis() + ((instanceCoolTime - System.currentTimeMillis()) / instanceCooldownRate);
		}
		return instanceCoolTime;
	}

	private long getUpdateHours(String[] days, int hour) {
		DateTime now = DateTime.now();
		DateTime repeatDate = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hour, 0, 0);
		int curentDay = now.getDayOfWeek();
		for (String name : days) {
			int day = getDay(name);
			if (day < curentDay) {
				continue;
			}
			if (day == curentDay) {
				if (now.isBefore(repeatDate)) {
					return repeatDate.getMillis();
				}
			}
			else {
				repeatDate = repeatDate.plusDays(day - curentDay);
				return repeatDate.getMillis();
			}
		}
		return repeatDate.plusDays((7 - curentDay) + getDay(days[0])).getMillis();
	}

	private int getDay(String day) {
		if (day.equals("Mon")) {
			return 1;
		}
		else if (day.equals("Tue")) {
			return 2;
		}
		else if (day.equals("Wed")) {
			return 3;
		}
		else if (day.equals("Thu")) {
			return 4;
		}
		else if (day.equals("Fri")) {
			return 5;
		}
		else if (day.equals("Sat")) {
			return 6;
		}
		else if (day.equals("Sun")) {
			return 7;
		}
		throw new IllegalArgumentException("Invalid Day: " + day);
	}

	public Integer size() {
		return instanceCooltimes.size();
	}
}
