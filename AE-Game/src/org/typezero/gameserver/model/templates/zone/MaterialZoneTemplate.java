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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.geoEngine.bounding.BoundingBox;
import org.typezero.gameserver.geoEngine.math.Vector3f;
import org.typezero.gameserver.geoEngine.scene.Spatial;

/**
 * @author Rolandas
 */
public class MaterialZoneTemplate extends ZoneTemplate {

	public MaterialZoneTemplate(Spatial geometry, int mapId) {
		mapid = mapId;
		flags = DataManager.WORLD_MAPS_DATA.getTemplate(mapId).getFlags();
		setXmlName(geometry.getName() + "_" + mapId);
		BoundingBox box = (BoundingBox) geometry.getWorldBound();
		Vector3f center = box.getCenter();
		// don't use polygons for small areas, they are bugged in Java API
		if (geometry.getName().indexOf("CYLINDER") != -1 || geometry.getName().indexOf("CONE") != -1 ||
			geometry.getName().indexOf("H_COLUME") != -1) {
			areaType = AreaType.CYLINDER;
			cylinder = new Cylinder(center.x, center.y, Math.max(box.getXExtent(), box.getYExtent() + 1), center.z + box.getZExtent() + 1, center.z
				- box.getZExtent() - 1);
		}
		else if (geometry.getName().indexOf("SEMISPHERE") != -1) {
			areaType = AreaType.SEMISPHERE;
			semisphere = new Semisphere(center.x, center.y, center.z, Math.max(Math.max(box.getXExtent(), box.getYExtent()), box.getZExtent()) + 1);
		}
		else {
			areaType = AreaType.SPHERE;
			sphere = new Sphere(center.x, center.y, center.z, Math.max(Math.max(box.getXExtent(), box.getYExtent()), box.getZExtent()) + 1);
		}
	}

}
