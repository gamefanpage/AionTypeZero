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

package org.typezero.gameserver.model.templates.zone;

import org.typezero.gameserver.configs.main.WorldConfig;
import org.typezero.gameserver.dataholders.DataManager;

/**
 * @author Rolandas
 */
public class WorldZoneTemplate extends ZoneTemplate {

	public WorldZoneTemplate(int size, Integer mapId) {
		float maxZ = Math.round((float) size / WorldConfig.WORLD_REGION_SIZE) * WorldConfig.WORLD_REGION_SIZE;
		points = new Points(-1, maxZ + 1);
		Point2D point = new Point2D();
		point.x = -1;
		point.y = -1;
		points.getPoint().add(point);
		point = new Point2D();
		point.x = -1;
		point.y = size + 1;
		points.getPoint().add(point);
		point = new Point2D();
		point.x = size + 1;
		point.y = size + 1;
		points.getPoint().add(point);
		point = new Point2D();
		point.x = size + 1;
		point.y = -1;
		points.getPoint().add(point);
		zoneType = ZoneClassName.DUMMY;
		mapid = mapId;
		flags = DataManager.WORLD_MAPS_DATA.getTemplate(mapId).getFlags();
		setXmlName(mapId.toString());
	}

}
