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

package org.typezero.gameserver.network;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.PropertiesUtils;
import org.typezero.gameserver.configs.main.SecurityConfig;

/**
 * @author KID
 */
public class PacketFloodFilter {

	private static PacketFloodFilter pff = new PacketFloodFilter();

	private final Logger log = LoggerFactory.getLogger(PacketFloodFilter.class);

	public static PacketFloodFilter getInstance() {
		return pff;
	}

	private int[] packets;
	private short maxClientRequest = 0x2ff;

	public PacketFloodFilter() {
		if(SecurityConfig.PFF_ENABLE) {
			int cnt = 0;
			packets = new int[maxClientRequest];
			try {
				java.util.Properties props = PropertiesUtils.load("config/administration/pff.properties");
				for(Object key : props.keySet()){
					String str = (String) key;
					packets[Integer.decode(str)] = Integer.valueOf(props.getProperty(str).trim());
					cnt++;
				}
			} catch (IOException e) {
				log.error("Can't read pff.properties", e);
			}
			log.info("PacketFloodFilter initialized with "+cnt+" packets.");
		} else
			log.info("PacketFloodFilter disabled.");
	}

	public final int[] getPackets() {
		return this.packets;
	}
}
