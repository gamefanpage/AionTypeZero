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

package org.typezero.gameserver.model.templates.item.actions;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.controllers.observer.ItemUseObserver;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.*;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.templates.item.DecomposableType;
import org.typezero.gameserver.model.templates.item.ExtractedItemsCollection;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.item.RandomItem;
import org.typezero.gameserver.model.templates.item.RandomType;
import org.typezero.gameserver.model.templates.item.ResultedItem;
import org.typezero.gameserver.network.aion.serverpackets.SM_DECOMPOSABLE_LIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import java.util.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Romanz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DecomposeAction")
public class DecomposeAction extends AbstractItemAction {

	private static final Logger log = LoggerFactory.getLogger(DecomposeAction.class);

	private static final int USAGE_DELAY = 1500;
	private static Map<Race, int[]> chunkEarth = new HashMap<Race, int[]>();
	static {
		chunkEarth.put(Race.ASMODIANS, new int[] {152000051, 152000052, 152000053, 152000451, 152000453, 152000551,
			152000651, 152000751, 152000752, 152000753, 152000851, 152000852, 152000853, 152001051, 152001052,
			152000201, 152000102, 152000054, 152000055, 152000455, 152000457, 152000552, 152000652, 152000754,
			152000755, 152000854, 152000855, 152000102, 152000202, 152000056, 152000057, 152000459, 152000461,
			152000553, 152000653, 152000756, 152000757, 152000856, 152000857, 152000104, 152000204, 152000058,
			152000059, 152000463, 152000465, 152000554, 152000654, 152000758, 152000759, 152000760, 152000858,
			152001053, 152000107, 152000207, 152003004, 152003005, 152003006, 152000061, 152000062, 152000063,
			152000468, 152000470, 152000556, 152000656, 152000657, 152000762, 152000763, 152000860, 152000861,
			152000862, 152001055, 152001056, 152000113, 152000117, 152000214, 152000606, 152000713,	152000811 });

		chunkEarth.put(Race.ELYOS, new int[] { 152000001, 152000002, 152000003, 152000401, 152000403, 152000501,
			152000601, 152000701, 152000702, 152000703, 152000801, 152000802, 152000803, 152001001, 152001002,
			152000101, 152000201, 152000004, 152000005, 152000405, 152000407, 152000502, 152000602, 152000704,
			152000705, 152000804, 152000805, 152000102, 152000202, 152000006, 152000007, 152000409, 152000411,
			152000503, 152000603, 152000706, 152000707, 152000806, 152000807, 152000104, 152000204, 152000008,
			152000009, 152000413, 152000415, 152000504, 152000604, 152000708, 152000709, 152000710, 152000808,
			152001003, 152000107, 152000207, 152003004, 152003005, 152003006, 152000010, 152000011, 152000012,
			152000417, 152000419, 152000505, 152000605, 152000607, 152000711, 152000712, 152000809, 152000810,
			152000812, 152001004, 152001005, 152000113, 152000117, 152000214, 152000606, 152000713,	152000811 });
	}

	private static Map<Race, int[]> chunkSand = new HashMap<Race, int[]>();
	static {

		chunkSand.put(Race.ASMODIANS, new int[] { 152000452, 152000454, 152000301, 152000302, 152000303 , 152000456,
		152000458, 152000103, 152000203, 152000304, 152000305, 152000306, 152000460, 152000462, 152000105,
		152000205, 152000307, 152000309, 152000311, 152000464, 152000466, 152000108, 152000208, 152000313,
		152000315, 152000317, 152000469, 152000471, 152000114, 152000215, 152000320, 152000322,	152000324 });

		chunkSand.put(Race.ELYOS, new int[] { 152000402, 152000404, 152000301, 152000302, 152000303, 152000406,
		152000408, 152000103, 152000203, 152000304, 152000305, 152000306, 152000410, 152000412, 152000105,
		152000205, 152000307, 152000309, 152000311, 152000414, 152000416, 152000108, 152000208, 152000313,
		152000315, 152000317, 152000418, 152000420, 152000114, 152000215, 152000320, 152000322,	152000324 });
	}
	private static int[] chunkRock = { 152000106, 152000206, 152000308, 152000310, 152000312, 152000109,
		152000209, 152000314, 152000316, 152000318, 152000115, 152000216, 152000219, 152000321,	152000323,
		152000325 };

	private static int[] chunkGemstone = { 152000112, 152000213, 152000116, 152000212, 152000217, 152000326,
		152000327, 152000328 };

	private static int[] scrolls = {164002002, 164002058, 164002010, 164002056, 164002057, 164002003, 164002059,
		164002011, 164002004, 164002012, 164002012, 164000122, 164000131, 164000118 };

	private static int[] potion = {162000045, 162000079, 162000016, 162000021, 162000015, 162000027, 162000020,
		162000044, 162000043, 162000026, 162000019, 162000014, 162000023, 162000022 };

	private static int[] ancient_manastone_rare_70 = {167020041, 167020042, 167020043, 167020044, 167020045, 167020046, 167020047, 167020048, 167020049,
		167020050, 167020051, 167020052, 167020053, 167020054, 167020055, 167020066, 167020067, 167020068 };

 	private static int[] ancient_manastone_legend_70 = {167020024, 167020025, 167020026, 167020027, 167020028, 167020029, 167020030, 167020031, 167020032,
		167020033, 167020034, 167020035, 167020036, 167020037, 167020038, 167020039, 167020040, 167020067, 167020070, 167020071 };

 	private static int[] ancient_manastone_gold_70 = {167020056, 167020057, 167020058, 167020059, 167020060, 167020061, 167020062, 167020063, 167020064,
		167020065 };

 	private static int[] ancient_manastone_epic_70 = {167020072, 167020073, 167020074, 167020075, 167020076, 167020077, 167020078, 167020079, 167020080,
		167020081, 167020082, 167020083 };

	private static int[] god_stone_50 = {168000117, 168000118, 168000119, 168000120, 168000121, 168000122,
		168000123, 168000124, 168000125, 168000126, 168000127, 168000128, 168000129, 168000130, 168000131,
		168000132, 168000133, 168000134, 168000135, 168000136, /*168000137, 168000138,*/ 168000139, 168000140 };

	private static int[] god_stone_60 = {168000141, 168000142, 168000143, 168000144, 168000145, 168000146,
		168000147, 168000148, 168000149, 168000150, 168000151, 168000152, 168000153, 168000154, 168000155,
		168000156 };

	private static int[] god_stone_legend = {168000028, 168000029, 168000030, 168000031, 168000032, 168000033,
		168000034, 168000035, 168000036, 168000039, 168000040, 168000041, 168000042,
		168000043, 168000044, 168000045, 168000046, 168000047, 168000048, 168000049, 168000050, 168000059,
		168000060, 168000061, 168000062, 168000063, 168000064, 168000065, 168000066, 168000067, 168000068,
		168000069, 168000070, 168000071, 168000072, 168000073, 168000074, 168000075 };

	private static int[] god_stone_unique = {168000051, 168000052, 168000053, 168000054, 168000055, 168000056,
		168000057, 168000058, 168000117, 168000118, 168000119, 168000120, 168000121, 168000122,
		168000123, 168000124, 168000125, 168000126, 168000127, 168000128, 168000129, 168000130, 168000131,
		168000132, 168000133 };

	private static int[] relic_all = {186000051, 186000052, 186000053, 186000054, 186000055, 186000056,
		186000057, 186000058, 186000059, 186000060, 186000061, 186000062, 186000063, 186000064, 186000065,
		186000066, 186000247, 186000248, 186000249, 186000250 };

	private static int[] manastone = {167000226, 167000227, 167000228, 167000229, 167000230, 167000231,
		167000232, 167000233, 167000235, 167000525, 167000526, 167000258, 167000259, 167000260, 167000261, 167000262, 167000263,
		167000264, 167000265, 167000267, 167000527, 167000528, 167000290, 167000291, 167000292, 167000293, 167000294, 167000295,
		167000296, 167000297, 167000299, 167000529, 167000530, 167000322, 167000323, 167000324, 167000325, 167000326, 167000327,
		167000328, 167000329, 167000331, 167000531, 167000532, 167000354, 167000355, 167000356, 167000357, 167000358, 167000359,
		167000360, 167000361, 167000363, 167000533, 167000534, 167000418, 167000419, 167000420, 167000421, 167000423, 167000424,
		167000425, 167000427, 167000535, 167000536, 167000450, 167000451, 167000452, 167000453, 167000454, 167000455,
		167000456, 167000457, 167000459, 167000465, 167000537, 167000538, 167000482, 167000483, 167000484, 167000485, 167000487, 167000488,
		167000489, 167000491, 167000497, 167000539, 167000540, 167000514, 167000515, 167000516, 167000517, 167000518, 167000519,
		167000520, 167000521, 167000522, 167000523, 167000541, 167000542 };

	private static int[] manastone_common_grade_10 = {167000226, 167000227, 167000228, 167000229, 167000230, 167000231,
		167000232, 167000233, 167000235, 167000525, 167000526 };

	private static int[] manastone_common_grade_20 = {167000258, 167000259, 167000260, 167000261, 167000262, 167000263,
		167000264, 167000265, 167000267, 167000527, 167000528 };

	private static int[] manastone_common_grade_30 = {167000290, 167000291, 167000292, 167000293, 167000294, 167000295,
		167000296, 167000297, 167000299, 167000529, 167000530 };

	private static int[] manastone_common_grade_40 = {167000322, 167000323, 167000324, 167000325, 167000326, 167000327,
		167000328, 167000329, 167000331, 167000531, 167000532 };

	private static int[] manastone_common_grade_50 = {167000354, 167000355, 167000356, 167000357, 167000358, 167000359,
		167000360, 167000361, 167000363, 167000533, 167000534 };

	private static int[] manastone_common_grade_60 = {167000543, 167000544, 167000545, 167000546, 167000547, 167000548,
		167000549, 167000550 };

	private static int[] manastone_common_grade_70 = {167000758, 167000759, 167000760, 167000761, 167000762, 167000763,
		167000764, 167000765 };

	private static int[] manastone_rare_grade_20 = {167000418, 167000419, 167000420, 167000421, 167000423, 167000424,
		167000425, 167000427, 167000535, 167000536 };

	private static int[] manastone_rare_grade_30 = {167000450, 167000451, 167000452, 167000453, 167000454, 167000455,
		167000456, 167000457, 167000459, 167000465, 167000537, 167000538 };

	private static int[] manastone_rare_grade_40 = {167000482, 167000483, 167000484, 167000485, 167000487, 167000488,
		167000489, 167000491, 167000497, 167000539, 167000540 };

	private static int[] manastone_rare_grade_50 = {167000514, 167000515, 167000516, 167000517, 167000518, 167000519,
		167000520, 167000521, 167000522, 167000523, 167000541, 167000542 };

	private static int[] manastone_rare_grade_60 = {167000551, 167000552, 167000553, 167000554, 167000555, 167000556,
		167000557, 167000558, 167000559, 167000560, 167000561, 167000563 };

	private static int[] manastone_rare_grade_70 = {167000766, 167000767, 167000768, 167000769, 167000770, 167000771,
		167000772, 167000773, 167000775, 167000776, 167000777, 167000778 };

	private static int[] manastone_legend_grade_30 = {167000578, 167000579, 167000580, 167000581, 167000582, 167000583,
		167000584, 167000585, 167000586, 167000587, 167000588, 167000589, 167000590, 167000591, 167000592, 167000593, 167000594,
167000595, 167000596, 167000597, 167000598, 167000599, 167000600, 167000601, 167000602, 167000603, 167000604, 167000605, 167000606,
167000607, 167000608, 167000609, 167000610, 167000611, 167000612, 167000613 };

	private static int[] manastone_legend_grade_40 = {167000614, 167000615, 167000616, 167000617, 167000618, 167000619,
		167000620, 167000621, 167000622, 167000623, 167000624, 167000625, 167000626, 167000627, 167000628, 167000629, 167000630,
167000631, 167000632, 167000633, 167000634, 167000635, 167000636, 167000637, 167000638, 167000639, 167000640, 167000641, 167000642,
167000643, 167000644, 167000645, 167000745, 167000746, 167000747, 167000748, 167000779, 167000780, 167000781, 167000782, 167000780,
167000783, 167000784, 167000785, 167000786, 167000787, 167000788, 167000789, 167000790, 167000791, 167000792, 167000793, 167000794,
167000795, 167000796, 167000797, 167000798, 167000799, 167000800, 167000801, 167000802, 167000803, 167000804, 167000805, 167000806,
167000808, 167000809, 167000810, 167000811 };

	private static int[] manastone_legend_grade_50 = {167000646, 167000647, 167000648, 167000649, 167000650, 167000651,
		167000652, 167000653, 167000654, 167000655, 167000656, 167000657, 167000658, 167000659, 167000660, 167000661, 167000662,
167000663, 167000664, 167000665, 167000666, 167000667, 167000668, 167000669, 167000670, 167000671, 167000672, 167000673, 167000674,
167000675, 167000676, 167000677, 167000678, 167000679, 167000680, 167000681, 167000812, 167000813, 167000814, 167000815, 167000816,
167000817, 167000818, 167000819, 167000820, 167000821, 167000822, 167000823, 167000824, 167000825, 167000826, 167000827, 167000828,
167000829, 167000830, 167000831, 167000832, 167000833, 167000834, 167000835, 167000836, 167000837, 167000838, 167000839, 167000840,
167000841, 167000842, 167000843, 167000844 };

	private static int[] manastone_legend_grade_60 = {167000682, 167000683, 167000684, 167000685, 167000686, 167000687,
		167000688, 167000689, 167000690, 167000691, 167000692, 167000693, 167000694, 167000695, 167000696, 167000697, 167000698,
167000699, 167000700, 167000701, 167000702, 167000703, 167000704, 167000705, 167000706, 167000707, 167000708, 167000709, 167000710,
167000711, 167000712, 167000713, 167000714, 167000749, 167000750, 167000751, 167000845, 167000846, 167000847, 167000848, 167000849,
167000850, 167000851, 167000852, 167000853, 167000854, 167000855, 167000856, 167000857, 167000858, 167000859, 167000860, 167000861,
167000862, 167000863, 167000864, 167000865, 167000866, 167000867, 167000868, 167000869, 167000870, 167000871, 167000871, 167000872,
167000873, 167000874, 167000875, 167000876, 167000877 };

	private static int[] manastone_legend_grade_70 = {167000715, 167000716, 167000717, 167000718, 167000719, 167000720,
		167000721, 167000722, 167000723, 167000724, 167000725, 167000726, 167000727, 167000728, 167000729, 167000730, 167000731,
167000732, 167000733, 167000734, 167000735, 167000736, 167000737, 167000738, 167000739, 167000740, 167000741, 167000742, 167000743,
167000744, 167000752, 167000753, 167000754, 167000755, 167000756, 167000757 };

	private static int[] shining_manastone_legend_grade_70 = {167000754, 167000715, 167000717, 167000732, 167000735, 167000738,
		167000720, 167000730, 167000724, 167000850 };

	private static int[] epic_craft_item_47 = {152012616, 152012617, 152012618, 152012619, 152012620, 152012621, 152012594, 152012595,
            152012598, 152012599, 152012600, 152012601, 152012602, 152012603, 152012604, 152012605, 152012605 };

	private static int[] idian_stone_50_60 = {166050031, 166050032, 166050033, 166050034, 166050035, 166050036, 166050037, 166050038,
            166050039, 166050040, 166050041, 166050042, 166050043, 166050044, 166050045, 166050046 };

	private static int[] mystic_stone_40 = {168000212, 168000213, 168000214, 168000215, 168000216, 168000217, 168000218, 168000219,
            168000220, 168000221, 168000222, 168000223, 168000224, 168000225, 168000226, 168000227, 168000228 };

	private static int[] mystic_stone_50 = {168000229, 168000230, 168000231, 168000232, 168000233, 168000234, 168000235, 168000236,
            168000237, 168000238, 168000239, 168000240, 168000241, 168000242, 168000243, 168000244, 168000245 };

	private static int[] mystic_stone_65 = {168000161, 168000162, 168000163, 168000164, 168000165, 168000166, 168000167, 168000168,
            168000169, 168000170, 168000171, 168000172, 168000173, 168000174, 168000175, 168000176, 168000177 };

	private static int[] mystic_stone_bonus = {168000178, 168000179, 168000180, 168000181, 168000182, 168000183, 168000184, 168000185,
            168000186, 168000187, 168000188, 168000189, 168000190, 168000191, 168000192, 168000193, 168000194 };

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		List<ExtractedItemsCollection> itemsCollections = DataManager.DECOMPOSABLE_ITEMS_DATA.getInfoByItemId(parentItem.getItemId());
		if (itemsCollections == null || itemsCollections.isEmpty()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_INVALID_STANCE(parentItem.getNameId()));
			return false;
		}
		return true;
	}

	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		player.getController().cancelUseItem();
		List<ExtractedItemsCollection> itemsCollections = DataManager.DECOMPOSABLE_ITEMS_DATA.getInfoByItemId(parentItem
			.getItemId());

		Collection<ExtractedItemsCollection> levelSuitableItems = filterItemsByLevel(player, itemsCollections);
		final ExtractedItemsCollection selectedCollection = selectItemByChance(levelSuitableItems);

		PacketSendUtility.broadcastPacketAndReceive(player,
			new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), USAGE_DELAY,
				0, 0));

		final ItemUseObserver observer = new ItemUseObserver() {

			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
						parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 2, 0), true);
				player.getObserveController().removeObserver(this);
			}

		};

		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				player.getObserveController().removeObserver(observer);
				boolean validAction = postValidate(player, parentItem);
				if (validAction) {
                    if (selectedCollection.getItems().size() > 0)
                    {
                        Collection<ResultedItem> itemByType = new ArrayList();//fix me

                        for (ResultedItem resultItem : selectedCollection.getItems())
                        {
                            if (canAcquire(player, resultItem))
                            {
                                if (DataManager.DECOMPOSABLE_ITEMS_DATA.getItemByType(parentItem.getItemId()) == DecomposableType.SELECT)
                                {
                                    itemByType.add(resultItem);
                                    continue;
                                }

                                if(resultItem.getResultCount() > 1 && player.getInventory().getItemCountByItemId(resultItem.getItemId()) > 0){
                                    PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1390005, player, "[item: " + resultItem.getItemId() + "] ", + resultItem.getResultCount()));
                                }
                                else if(resultItem.getResultCount() == 1 && player.getInventory().getItemCountByItemId(resultItem.getItemId()) > 0)
                                {
                                    PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1390004, player, "[item: " + resultItem.getItemId() + "]"));
                                }

                                ItemService.addItem(player, resultItem.getItemId(), resultItem.getResultCount(),new ItemService.ItemUpdatePredicate());

                            }
                        }

                        if (!itemByType.isEmpty())
                        {
                            player.putTempStorage(parentItem.getObjectId(), selectedCollection);
                            PacketSendUtility.sendPacket(player, new SM_DECOMPOSABLE_LIST(parentItem.getObjectId(), itemByType));
                        }

                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_SUCCEED(parentItem.getNameId()));
                    }
					else if (selectedCollection.getRandomItems().size() > 0) {
						for (RandomItem randomItem : selectedCollection.getRandomItems()) {
							RandomType randomType = randomItem.getType();
							if (randomType != null) {
								int randomId = 0;
								int i = 0;
								int itemLvl = parentItem.getItemTemplate().getLevel();
								switch (randomItem.getType()) {
									case ENCHANTMENT: {
										do {
											randomId = 166000000 + itemLvl + Rnd.get(50);
											i++;
											if (i > 50) {
												randomId = 0;
												log.warn("DecomposeAction random item id not found. " + parentItem.getItemId());
												break;
											}
										}
										while (!ItemService.checkRandomTemplate(randomId));
										break;
									}
									case MANASTONE: {
										randomId = manastone[Rnd.get(manastone.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_COMMON_GRADE_10: {
										randomId = manastone_common_grade_10[Rnd.get(manastone_common_grade_10.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_COMMON_GRADE_20: {
										randomId = manastone_common_grade_20[Rnd.get(manastone_common_grade_20.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_COMMON_GRADE_30: {
										randomId = manastone_common_grade_30[Rnd.get(manastone_common_grade_30.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_COMMON_GRADE_40: {
										randomId = manastone_common_grade_40[Rnd.get(manastone_common_grade_40.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_COMMON_GRADE_50: {
										randomId = manastone_common_grade_50[Rnd.get(manastone_common_grade_50.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_COMMON_GRADE_60: {
										randomId = manastone_common_grade_60[Rnd.get(manastone_common_grade_60.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_COMMON_GRADE_70: {
										randomId = manastone_common_grade_70[Rnd.get(manastone_common_grade_70.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_RARE_GRADE_20: {
										randomId = manastone_rare_grade_20[Rnd.get(manastone_rare_grade_20.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_RARE_GRADE_30: {
										randomId = manastone_rare_grade_30[Rnd.get(manastone_rare_grade_30.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_RARE_GRADE_40: {
										randomId = manastone_rare_grade_40[Rnd.get(manastone_rare_grade_40.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_RARE_GRADE_50: {
										randomId = manastone_rare_grade_50[Rnd.get(manastone_rare_grade_50.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_RARE_GRADE_60: {
										randomId = manastone_rare_grade_60[Rnd.get(manastone_rare_grade_60.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_RARE_GRADE_70: {
										randomId = manastone_rare_grade_70[Rnd.get(manastone_rare_grade_70.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_LEGEND_GRADE_30: {
										randomId = manastone_legend_grade_30[Rnd.get(manastone_legend_grade_30.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_LEGEND_GRADE_40: {
										randomId = manastone_legend_grade_40[Rnd.get(manastone_legend_grade_40.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_LEGEND_GRADE_50: {
										randomId = manastone_legend_grade_50[Rnd.get(manastone_legend_grade_50.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_LEGEND_GRADE_60: {
										randomId = manastone_legend_grade_60[Rnd.get(manastone_legend_grade_60.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MANASTONE_LEGEND_GRADE_70: {
										randomId = manastone_legend_grade_70[Rnd.get(manastone_legend_grade_70.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case SHINING_MANASTONE_LEGEND_GRADE_70: {
										randomId = shining_manastone_legend_grade_70[Rnd.get(shining_manastone_legend_grade_70.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case CHUNK_EARTH: {
										int[] earth = chunkEarth.get(player.getRace());

										randomId = earth[Rnd.get(earth.length)];
										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case CHUNK_SAND: {
										int[] sand = chunkSand.get(player.getRace());

										randomId = sand[Rnd.get(sand.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case CHUNK_ROCK: {
										randomId = chunkRock[Rnd.get(chunkRock.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case CHUNK_GEMSTONE: {
										randomId = chunkGemstone[Rnd.get(chunkGemstone.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case SCROLLS: {
										randomId = scrolls[Rnd.get(scrolls.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case POTION: {
										randomId = potion[Rnd.get(potion.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case ANCIENT_MANASTONE_RARE_70: {
										randomId = ancient_manastone_rare_70[Rnd.get(ancient_manastone_rare_70.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case ANCIENT_MANASTONE_LEGEND_70: {
										randomId = ancient_manastone_legend_70[Rnd.get(ancient_manastone_legend_70.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case ANCIENT_MANASTONE_GOLD_70: {
										randomId = ancient_manastone_gold_70[Rnd.get(ancient_manastone_gold_70.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case ANCIENT_MANASTONE_EPIC_70: {
										randomId = ancient_manastone_epic_70[Rnd.get(ancient_manastone_epic_70.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case GOD_STONE_50: {
										randomId = god_stone_50[Rnd.get(god_stone_50.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case GOD_STONE_60: {
										randomId = god_stone_60[Rnd.get(god_stone_60.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case GOD_STONE_LEGEND: {
										randomId = god_stone_legend[Rnd.get(god_stone_legend.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case GOD_STONE_UNIQUE: {
										randomId = god_stone_unique[Rnd.get(god_stone_unique.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case RELIC: {
										randomId = relic_all[Rnd.get(relic_all.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case ANCIENTITEMS: {
										do {
											randomId = Rnd.get(186000051, 186000066);
											i++;
											if (i > 50) {
												randomId = 0;
												log.warn("DecomposeAction random item id not found. " + parentItem.getItemId());
												break;
											}
										}
										while (!ItemService.checkRandomTemplate(randomId));
										break;
									}
                                                                        case ENCHANTMENT_50_100: {
										do {
											randomId = 166000050 + Rnd.get(50);
											i++;
											if (i > 50) {
												randomId = 0;
												log.warn("DecomposeAction random item id not found. " + parentItem.getItemId());
												break;
											}
										}
										while (!ItemService.checkRandomTemplate(randomId));
										break;
									}
									case ENCHANTMENT_100_150: {
										do {
											randomId = 166000100 + Rnd.get(50);
											i++;
											if (i > 50) {
												randomId = 0;
												log.warn("DecomposeAction random item id not found. " + parentItem.getItemId());
												break;
											}
										}
										while (!ItemService.checkRandomTemplate(randomId));
										break;
									}
									case EPIC_CRAFT_ITEM_47: {
										randomId = epic_craft_item_47[Rnd.get(epic_craft_item_47.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case IDIAN_STONE_50_60: {
										randomId = idian_stone_50_60[Rnd.get(idian_stone_50_60.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MYSTIC_STONE_40: {
										randomId = mystic_stone_40[Rnd.get(mystic_stone_40.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MYSTIC_STONE_50: {
										randomId = mystic_stone_50[Rnd.get(mystic_stone_50.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MYSTIC_STONE_65: {
										randomId = mystic_stone_65[Rnd.get(mystic_stone_65.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case MYSTIC_STONE_BONUS: {
										randomId = mystic_stone_bonus[Rnd.get(mystic_stone_bonus.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
								}
								if (randomId != 0 && randomId != 167000524)
									ItemService.addItem(player, randomId, randomItem.getResultCount());
							}
						}
					}
				}
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
					parentItem.getObjectId(), parentItem.getItemId(), 0, validAction ? 1 : 2, 0));
			}

			private boolean canAcquire(Player player, ResultedItem resultItem) {
				Race race = resultItem.getRace();
				if (race != Race.PC_ALL && !race.equals(player.getRace())) {
					return false;
				}
				PlayerClass playerClass = resultItem.getPlayerClass();

				if (!playerClass.equals(PlayerClass.ALL) && !playerClass.equals(player.getPlayerClass())) {
					return false;
				}
				return true;
			}

			boolean postValidate(Player player, Item parentItem) {
				if (!canAct(player, parentItem, targetItem)) {
					return false;
				}
				Storage inventory = player.getInventory();
                if (DataManager.DECOMPOSABLE_ITEMS_DATA.getItemByType(parentItem.getItemId()) != DecomposableType.SELECT){
				int slotReq = calcMaxCountOfSlots(selectedCollection, player, false);
				int specialSlotreq = calcMaxCountOfSlots(selectedCollection, player, true);
				if ((slotReq > 0 && inventory.getFreeSlots() < slotReq) && (specialSlotreq < 0 && inventory.getSpecialCubeFreeSlots() < specialSlotreq)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_WAREHOUSE_FULL_INVENTORY);
					return false;
				}
				if (specialSlotreq > 0 && inventory.getSpecialCubeFreeSlots() < specialSlotreq) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DECOMPRESS_INVENTORY_IS_FULL);
					return false;
				}}
				if (player.getLifeStats().isAlreadyDead() || !player.isSpawned()) {
					return false;
				}
                if (DataManager.DECOMPOSABLE_ITEMS_DATA.getItemByType(parentItem.getItemId()) != DecomposableType.SELECT){
				if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_NO_TARGET_ITEM);
					return false;
				}
				if (selectedCollection.getItems().isEmpty() && selectedCollection.getRandomItems().isEmpty()) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_FAILED(parentItem.getNameId()));
					return false;
				}}
				return true;
			}
		}, USAGE_DELAY));
	}

	/**
	 * Add to result collection only items wich suits player's level
	 */
	private Collection<ExtractedItemsCollection> filterItemsByLevel(Player player,
		List<ExtractedItemsCollection> itemsCollections) {
		int playerLevel = player.getLevel();
		Collection<ExtractedItemsCollection> result = new ArrayList<ExtractedItemsCollection>();
		for (ExtractedItemsCollection collection : itemsCollections) {
			if (collection.getMinLevel() > playerLevel) {
				continue;
			}
			if (collection.getMaxLevel() > 0 && collection.getMaxLevel() < playerLevel) {
				continue;
			}
			result.add(collection);
		}
		return result;
	}

	/**
	 * Select only 1 item based on chance attributes
	 */
	private ExtractedItemsCollection selectItemByChance(Collection<ExtractedItemsCollection> itemsCollections) {
		float sumOfChances = calcSumOfChances(itemsCollections);
		float currentSum = 0f;
		float rnd = (float) Rnd.get(0, (int)(sumOfChances - 1) * 1000) / 1000;
		ExtractedItemsCollection selectedCollection = null;
		for (ExtractedItemsCollection collection : itemsCollections) {
			currentSum += collection.getChance();
			if (rnd < currentSum) {
				selectedCollection = collection;
				break;
			}
		}
		return selectedCollection;
	}

	private int calcMaxCountOfSlots(ExtractedItemsCollection itemsCollections, Player player, boolean special) {
		int maxCount = 0;
		for (ResultedItem item : itemsCollections.getItems()) {
			if (item.getRace().equals(Race.PC_ALL)
					|| player.getRace().equals(item.getRace())) {
				if (item.getPlayerClass().equals(PlayerClass.ALL)
						|| player.getPlayerClass().equals(item.getPlayerClass())) {
					ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(item.getItemId());
					if (special && template.getExtraInventoryId() > 0) {
						maxCount++;
					}
					else if (template.getExtraInventoryId() < 1){
						maxCount++;
					}
				}
			}
		}
		return maxCount;
	}

	private float calcSumOfChances(Collection<ExtractedItemsCollection> itemsCollections) {
		float sum = 0;
		for (ExtractedItemsCollection collection : itemsCollections)
			sum += collection.getChance();
		return sum;
	}
}
