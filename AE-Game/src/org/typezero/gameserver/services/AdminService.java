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

package org.typezero.gameserver.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author KID
 */
public class AdminService {
	private final Logger log = LoggerFactory.getLogger(AdminService.class);
	private static final Logger itemLog = LoggerFactory.getLogger("GMITEMRESTRICTION");
	private FastList<Integer> list;
	private static AdminService instance = new AdminService();

	public static AdminService getInstance() {
		return instance;
	}

	public AdminService() {
		list = FastList.newInstance();
		if(AdminConfig.ENABLE_TRADEITEM_RESTRICTION)
			reload();
	}

	public void reload() {
		if(list.size() > 0)
			list.clear();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("./config/administration/item.restriction.txt"));
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.startsWith("#") || line.trim().length() == 0)
					continue;

				String pt = line.split("#")[0].replaceAll(" ", "");
				list.add(Integer.parseInt(pt));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		log.info("AdminService loaded "+list.size()+" operational items.");
	}

	public boolean canOperate(Player player, Player target, Item item, String type) {
		return canOperate(player, target, item.getItemId(), type);
	}

	public boolean canOperate(Player player, Player target, int itemId, String type) {
		if(!AdminConfig.ENABLE_TRADEITEM_RESTRICTION)
			return true;

		if(target != null && target.getAccessLevel() > 0) //allow between gms
			return true;

		if(player.getAccessLevel() > 0 && player.getAccessLevel() < 4) { // run check only for 1-3 level gms
			boolean value = list.contains(itemId);
			String str = "GM "+player.getName()+"|"+player.getObjectId()+" ("+type+"): "+itemId+"|result="+value;
			if(target != null)
				str += "|target="+target.getName()+"|"+target.getObjectId();
			itemLog.info(str);
			if(!value)
				PacketSendUtility.sendMessage(player, "You cannot use "+type+" with this item.");

			return value;
		}
		else
			return true;
	}
}
