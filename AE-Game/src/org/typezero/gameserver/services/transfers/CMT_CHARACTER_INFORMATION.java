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

package org.typezero.gameserver.services.transfers;

import java.util.List;

import javolution.util.FastList;

import org.slf4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.PlayerTransferConfig;
import org.typezero.gameserver.dao.InventoryDAO;
import org.typezero.gameserver.dao.PlayerBindPointDAO;
import org.typezero.gameserver.dao.PlayerNpcFactionsDAO;
import org.typezero.gameserver.dao.PlayerTitleListDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Gender;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.account.Account;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.AbyssRank;
import org.typezero.gameserver.model.gameobjects.player.BindPointPosition;
import org.typezero.gameserver.model.gameobjects.player.MacroList;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerAppearance;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.gameobjects.player.PlayerSettings;
import org.typezero.gameserver.model.gameobjects.player.QuestStateList;
import org.typezero.gameserver.model.gameobjects.player.RecipeList;
import org.typezero.gameserver.model.gameobjects.player.emotion.EmotionList;
import org.typezero.gameserver.model.gameobjects.player.motion.Motion;
import org.typezero.gameserver.model.gameobjects.player.motion.MotionList;
import org.typezero.gameserver.model.gameobjects.player.npcFaction.ENpcFactionQuestState;
import org.typezero.gameserver.model.gameobjects.player.npcFaction.NpcFaction;
import org.typezero.gameserver.model.gameobjects.player.npcFaction.NpcFactions;
import org.typezero.gameserver.model.gameobjects.player.title.Title;
import org.typezero.gameserver.model.gameobjects.player.title.TitleList;
import org.typezero.gameserver.model.skill.PlayerSkillList;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.AccountService;
import org.typezero.gameserver.services.item.ItemSocketService;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.utils.idfactory.IDFactory;

/**
 * @author KID
 */
public class CMT_CHARACTER_INFORMATION extends AionClientPacket {

	protected CMT_CHARACTER_INFORMATION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() { }

	@Override
	protected void runImpl() { }

	public Player readInfo(String name, int targetAccount, String accountName, List<Integer> rsList, Logger textLog) {
		long st = System.currentTimeMillis();
		PlayerCommonData playerCommonData = new PlayerCommonData(IDFactory.getInstance().nextId());
		playerCommonData.setName(name);
		playerCommonData.setExp(readQ());
		playerCommonData.setPlayerClass(PlayerClass.getPlayerClassById((byte) readD()));
		playerCommonData.setRace(readD() == 0 ? Race.ELYOS : Race.ASMODIANS);
		playerCommonData.setGender(readD() == 0 ? Gender.MALE : Gender.FEMALE);
		playerCommonData.setTitleId(readD());
		playerCommonData.setDp(readD());
		playerCommonData.setQuestExpands(readD());
		playerCommonData.setNpcExpands(readD());
		playerCommonData.setAdvencedStigmaSlotSize(readD());
		playerCommonData.setWarehouseSize(readD());

		PlayerAppearance playerAppearance = new PlayerAppearance();
		playerAppearance.setSkinRGB(readD());
		playerAppearance.setHairRGB(readD());
		playerAppearance.setEyeRGB(readD());
		playerAppearance.setLipRGB(readD());
		playerAppearance.setFace(readC());
		playerAppearance.setHair(readC());
		playerAppearance.setDeco(readC());
		playerAppearance.setTattoo(readC());
		playerAppearance.setFaceContour(readC());
		playerAppearance.setExpression(readC());
		playerAppearance.setJawLine(readC());
		playerAppearance.setForehead(readC());
		playerAppearance.setEyeHeight(readC());
		playerAppearance.setEyeSpace(readC());
		playerAppearance.setEyeWidth(readC());
		playerAppearance.setEyeSize(readC());
		playerAppearance.setEyeShape(readC());
		playerAppearance.setEyeAngle(readC());
		playerAppearance.setBrowHeight(readC());
		playerAppearance.setBrowAngle(readC());
		playerAppearance.setBrowShape(readC());
		playerAppearance.setNose(readC());
		playerAppearance.setNoseBridge(readC());
		playerAppearance.setNoseWidth(readC());
		playerAppearance.setNoseTip(readC());
		playerAppearance.setCheek(readC());
		playerAppearance.setLipHeight(readC());
		playerAppearance.setMouthSize(readC());
		playerAppearance.setLipSize(readC());
		playerAppearance.setSmile(readC());
		playerAppearance.setLipShape(readC());
		playerAppearance.setJawHeigh(readC());
		playerAppearance.setChinJut(readC());
		playerAppearance.setEarShape(readC());
		playerAppearance.setHeadSize(readC());
		playerAppearance.setNeck(readC());
		playerAppearance.setNeckLength(readC());
		playerAppearance.setShoulderSize(readC());
		playerAppearance.setTorso(readC());
		playerAppearance.setChest(readC());
		playerAppearance.setWaist(readC());
		playerAppearance.setHips(readC());
		playerAppearance.setArmThickness(readC());
		playerAppearance.setHandSize(readC());
		playerAppearance.setLegThicnkess(readC());
		playerAppearance.setFootSize(readC());
		playerAppearance.setFacialRate(readC());
		playerAppearance.setArmLength(readC());
		playerAppearance.setLegLength(readC());
		playerAppearance.setShoulders(readC());
		playerAppearance.setFaceShape(readC());
		playerAppearance.setVoice(readC());
		playerAppearance.setHeight(readF());

		Account account = AccountService.loadAccount(targetAccount);
		account.setName(accountName);
		Player player = PlayerService.newPlayer(playerCommonData, playerAppearance, account);
		player.getPosition().setXYZH(readF(), readF(), readF(), readSC());
		player.getPosition().setMapId(readD());

		if (!PlayerService.storeNewPlayer(player, accountName, targetAccount)) {
			textLog.info("failed to store new player to "+accountName);
			IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
			return null;
		}

		int cnt = readD();
		FastList<String> itemOut = FastList.newInstance();
		for(int a = 0; a < cnt; a++) { //inventory
			int objIdOld = readD();
			int itemId = readD();
			long itemCnt = readQ();
			int itemColor = readD();
			String itemCreator = readS();
			int itemExpireTime = readD();
			int itemActivationCnt = readD();
			boolean itemEquipped = readD() == 1;
			boolean itemSoulBound = readD() == 1;
			long equipSlot = readQ();
			int location = readD();
			int enchant = readD();
			int skinId = readD();
			int fusionId = readD();
			int optSocket = readD();
			int optFusion = readD();
			int charge = readD();
			FastList<int[]> manastones = FastList.newInstance(), fusions = FastList.newInstance();
			int len = readD();
			for(int b = 0; b < len; b++) {
				manastones.add(new int[] { readD(), readD() });
			}
			len = readD();
			for(int b = 0; b < len; b++) {
				fusions.add(new int[] { readD(), readD() });
			}
			int godstone = 0;
			if(readC() == 1)
				godstone = readD();

			int colorExpires = readD();
			int rndBonus = readD();
			Integer bonus = null;
			if(rndBonus != -1)
				bonus = rndBonus;

			if(PlayerTransferConfig.ALLOW_INV) {
				ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(itemId);
				if(template == null) {
					textLog.info("(cube"+targetAccount+")item with id "+itemId+" was not found in dp");
					continue;
				}

				if(template.isStigma() && !PlayerTransferConfig.ALLOW_STIGMA) {
					continue;
				}

				int newId = IDFactory.getInstance().nextId();
				// bonus probably is lost, don't know [RR]
				// dye expiration is lost
				Item item = new Item(newId, itemId, itemCnt, itemColor, colorExpires, itemCreator, itemExpireTime, itemActivationCnt, itemEquipped, itemSoulBound, equipSlot, location, enchant, skinId, fusionId, optSocket, optFusion, charge, bonus, 0);
				if(manastones.size() > 0) {
					for(int[] stone : manastones) {
						ItemSocketService.addManaStone(item, stone[0], stone[1]);
					}
				}
				if(fusions.size() > 0) {
					for(int[] stone : fusions) {
						ItemSocketService.addFusionStone(item, stone[0], stone[1]);
					}
				}
				if(godstone != 0) {
					item.addGodStone(godstone);
				}

				String itemTxt = "(cube)#itemId="+itemId+"; objectIdChange["+objIdOld+"->"+newId+"] "+item.getItemCount()+";"+item.getItemColor()+";"+item.getItemCreator()+";"+item.getExpireTime()+";"+item.getActivationCount()+";"+item.getEnchantLevel()+";"+item.getItemSkinTemplate().getTemplateId()+";"+item.getFusionedItemTemplate()+";"+item.getOptionalSocket()+";"+item.getOptionalFusionSocket()+";"+item.getChargePoints();
				itemOut.add(itemTxt);
				item.setPersistentState(PersistentState.NEW);
				player.getInventory().add(item);
			}
		}

		cnt = readD();
		for(int a = 0; a < cnt; a++) { //warehouse
			int objIdOld = readD();
			int itemId = readD();
			long itemCnt = readQ();
			int itemColor = readD();
			String itemCreator = readS();
			int itemExpireTime = readD();
			int itemActivationCnt = readD();
			boolean itemEquipped = readD() == 1;
			boolean itemSoulBound = readD() == 1;
			long equipSlot = readQ(); //OMG
			int location = readD();
			int enchant = readD();
			int skinId = readD();
			int fusionId = readD();
			int optSocket = readD();
			int optFusion = readD();
			int charge = readD();
			FastList<int[]> manastones = FastList.newInstance(), fusions = FastList.newInstance();
			int len = readD();
			for(int b = 0; b < len; b++) {
				manastones.add(new int[] { readD(), readD() });
			}
			len = readD();
			for(int b = 0; b < len; b++) {
				fusions.add(new int[] { readD(), readD() });
			}

			int godstone = 0;
			if(readC() == 1)
				godstone = readD();

			if(PlayerTransferConfig.ALLOW_WAREHOUSE) {
				ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(itemId);
				if(template == null) {
					textLog.info("(warehouse"+targetAccount+")item with id "+itemId+" was not found in dp");
					continue;
				}

				if(template.isStigma() && !PlayerTransferConfig.ALLOW_STIGMA) {
					continue;
				}

				int newId = IDFactory.getInstance().nextId();
				// bonus probably is lost, don't know [RR]
				// dye expiration is lost
				Item item = new Item(newId, itemId, itemCnt, itemColor, 0, itemCreator, itemExpireTime, itemActivationCnt, itemEquipped, itemSoulBound, equipSlot, location, enchant, skinId, fusionId, optSocket, optFusion, charge, 0, 0);
				if(manastones.size() > 0) {
					for(int[] stone : manastones) {
						ItemSocketService.addManaStone(item, stone[0], stone[1]);
					}
				}
				if(fusions.size() > 0) {
					for(int[] stone : fusions) {
						ItemSocketService.addFusionStone(item, stone[0], stone[1]);
					}
				}
				if(godstone != 0) {
					item.addGodStone(godstone);
				}

				String itemTxt = "(warehouse)#itemId="+itemId+"; objectIdChange["+objIdOld+"->"+newId+"] "+item.getItemCount()+";"+item.getItemColor()+";"+item.getItemCreator()+";"+item.getExpireTime()+";"+item.getActivationCount()+";"+item.getEnchantLevel()+";"+item.getItemSkinTemplate().getTemplateId()+";"+item.getFusionedItemTemplate()+";"+item.getOptionalSocket()+";"+item.getOptionalFusionSocket()+";"+item.getChargePoints();
				itemOut.add(itemTxt);
				item.setPersistentState(PersistentState.NEW);
				player.getWarehouse().add(item);
			}
		}
		DAOManager.getDAO(InventoryDAO.class).store(player);

		for(String s : itemOut)
			textLog.info(s);

		FastList.recycle(itemOut);
		cnt = readD();
		textLog.info("EmotionList:"+cnt);
		player.setEmotions(new EmotionList(player));
		for(int a = 0; a < cnt; a++) { //emotes
			if(PlayerTransferConfig.ALLOW_EMOTIONS)
				player.getEmotions().add(readD(), readD(), true);
			else {
				readQ();
			}
		}

		cnt = readD();
		textLog.info("MotionList:"+cnt);
		player.setMotions(new MotionList(player));
		for(int i = 0; i < cnt; i++) { //motions
			if(PlayerTransferConfig.ALLOW_MOTIONS)
				player.getMotions().add(new Motion(readD(), readD(), readC() == 1), true);
			else
				readB(9);
		}

		cnt = readD();
		textLog.info("MacroList:"+cnt);
		player.setMacroList(new MacroList());
		for(int a = 0; a < cnt; a++) { //macros
			if(PlayerTransferConfig.ALLOW_MACRO)
				PlayerService.addMacro(player, readD(), readS());
			else {
				readD(); readS();
			}
		}

		cnt = readD();
		textLog.info("NpcFactions:"+cnt);
		player.setNpcFactions(new NpcFactions(player));
		for(int a = 0; a < cnt; a++) { //npc factions
			if(PlayerTransferConfig.ALLOW_NPCFACTIONS)
				player.getNpcFactions().addNpcFaction(new NpcFaction(readD(), readD(), readD() == 1, ENpcFactionQuestState.valueOf(readS()), readD()));
			else {
				readB(12); readS(); readD();
			}
		}
		if(cnt > 0 && PlayerTransferConfig.ALLOW_NPCFACTIONS)
			DAOManager.getDAO(PlayerNpcFactionsDAO.class).storeNpcFactions(player);

		cnt = readD();
		textLog.info("Pets:"+cnt);
		for(int i = 0; i < cnt; i++) { //pets
			if(PlayerTransferConfig.ALLOW_PETS) {
				int petId = readD();
				int decorationId = readD();
				long bday = readQ();
				if(bday == 0)
					bday = System.currentTimeMillis();
				player.getPetList().addPet(player, petId, decorationId, bday, readS(), 0);
			}
			else {
				readB(16); readS();
			}
		}

		cnt = readD();
		textLog.info("RecipeList:"+cnt);
		player.setRecipeList(new RecipeList());
		for(int a = 0; a < cnt; a++) { //recipes
			if(PlayerTransferConfig.ALLOW_RECIPES)
				player.getRecipeList().addRecipe(player.getObjectId(), readD());
			else
				readD();
		}

		cnt = readD();
		textLog.info("PlayerSkillList:"+cnt);
		player.setSkillList(new PlayerSkillList());
		boolean rsCheck = rsList.size() > 0;
		for(int a = 0; a < cnt; a++) { //skills
			int skillId = readD();
			int skillLvl = readD();

			if(rsCheck && rsList.contains(skillId))
				continue;

			SkillTemplate temp = DataManager.SKILL_DATA.getSkillTemplate(skillId);
			if(!PlayerTransferConfig.ALLOW_SKILLS) {
				if(temp.isPassive())
					player.getSkillList().addSkill(player, skillId, skillLvl);
			}
			else
				player.getSkillList().addSkill(player, skillId, skillLvl);
		}

		cnt = readD();
		textLog.info("TitleList:"+cnt);
		player.setTitleList(new TitleList());
		for(int a = 0; a < cnt; a++) { //titles
			if(PlayerTransferConfig.ALLOW_TITLES)
				player.getTitleList().addEntry(readD(), readD());
			else {
				readB(8);
			}
		}
		if(cnt > 0 && PlayerTransferConfig.ALLOW_TITLES)
			for(Title t : player.getTitleList().getTitles()) {
				DAOManager.getDAO(PlayerTitleListDAO.class).storeTitles(player, t);
			}

		String[] pos = null;
		switch(player.getRace()) {
			case ELYOS:
				pos = PlayerTransferConfig.BIND_ELYOS.split(" ");
				break;
			case ASMODIANS:
				pos = PlayerTransferConfig.BIND_ASMO.split(" ");
				break;

			default:
				break;
		}

		player.setBindPoint(new BindPointPosition(Integer.parseInt(pos[0]), Float.parseFloat(pos[1]), Float.parseFloat(pos[2]), Float.parseFloat(pos[3]), Byte.parseByte(pos[4])));
		DAOManager.getDAO(PlayerBindPointDAO.class).store(player);

		int uilen = readD(), shortlen = readD();
		byte[] ui = readB(uilen), sc = readB(shortlen);
		player.setPlayerSettings(new PlayerSettings(uilen > 0 ? ui : null, shortlen > 0 ? sc : null, null, readD(), readD()));
		player.setAbyssRank(new AbyssRank(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0));

		cnt = readD();
		textLog.info("QuestStateList:"+cnt);
		player.setQuestStateList(new QuestStateList());
		for(int a = 0; a < cnt; a++) { //quests
			int questId = readD();
			if(PlayerTransferConfig.ALLOW_QUESTS) {
				player.getQuestStateList().addQuest(questId, new QuestState(questId, QuestStatus.valueOf(readS()), readD(), readD(), null, readD(), null)); //TODO null timestamp
			}
			else {
				readS(); readB(12);
			}
		}

		PlayerService.storePlayer(player);
		textLog.info("finished in "+(System.currentTimeMillis()-st)+" ms");
		return player;
	}
}
