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

package admincommands;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMap;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldMapType;

/**
 * Goto command
 *
 * @author Dwarfpicker
 * @rework Imaginary
 */
public class GoTo extends AdminCommand{

	public GoTo() {
		super("goto");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax //goto <location>");
			return;
		}

		StringBuilder sbDestination = new StringBuilder();
		for(String p : params)
			sbDestination.append(p + " ");

		String destination = sbDestination.toString().trim();

		/**
		 * Elysea
		 */
		// Sanctum
		if (destination.equalsIgnoreCase("Sanctum"))
			goTo(player, WorldMapType.SANCTUM.getId(), 1322, 1511, 568);
		// Kaisinel
		else if (destination.equalsIgnoreCase("Kaisinel"))
			goTo(player, WorldMapType.KAISINEL.getId(), 2155, 1567, 1205);
		// Poeta
		else if (destination.equalsIgnoreCase("Poeta"))
			goTo(player, WorldMapType.POETA.getId(), 806, 1242, 119);
		else if (destination.equalsIgnoreCase("Melponeh"))
			goTo(player, WorldMapType.POETA.getId(), 426, 1740, 119);
		// Verteron
		else if (destination.equalsIgnoreCase("Verteron"))
			goTo(player, WorldMapType.VERTERON.getId(), 1643, 1500, 119);
		else if (destination.equalsIgnoreCase("Cantas") || destination.equalsIgnoreCase("Cantas Coast"))
			goTo(player, WorldMapType.VERTERON.getId(), 2384, 788, 102);
		else if (destination.equalsIgnoreCase("Ardus") || destination.equalsIgnoreCase("Ardus Shrine"))
			goTo(player, WorldMapType.VERTERON.getId(), 2333, 1817, 193);
		else if (destination.equalsIgnoreCase("Pilgrims") || destination.equalsIgnoreCase("Pilgrims Respite"))
			goTo(player, WorldMapType.VERTERON.getId(), 2063, 2412, 274);
		else if (destination.equalsIgnoreCase("Tolbas") || destination.equalsIgnoreCase("Tolbas Village"))
			goTo(player, WorldMapType.VERTERON.getId(), 1291, 2206, 142);
		// Eltnen
		else if (destination.equalsIgnoreCase("Eltnen"))
			goTo(player, WorldMapType.ELTNEN.getId(), 343, 2724, 264);
		else if (destination.equalsIgnoreCase("Golden") || destination.equalsIgnoreCase("Golden Bough Garrison"))
			goTo(player, WorldMapType.ELTNEN.getId(), 688, 431, 332);
		else if (destination.equalsIgnoreCase("Eltnen Observatory"))
			goTo(player, WorldMapType.ELTNEN.getId(), 1779, 883, 422);
		else if (destination.equalsIgnoreCase("Novan"))
			goTo(player, WorldMapType.ELTNEN.getId(), 947, 2215, 252);
		else if (destination.equalsIgnoreCase("Agairon"))
			goTo(player, WorldMapType.ELTNEN.getId(), 1921, 2045, 361);
		else if (destination.equalsIgnoreCase("Kuriullu"))
			goTo(player, WorldMapType.ELTNEN.getId(), 2411, 2724, 361);
		// Theobomos
		else if (destination.equalsIgnoreCase("Theobomos"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 1398, 1557, 31);
		else if (destination.equalsIgnoreCase("Jamanok") || destination.equalsIgnoreCase("Jamanok Inn"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 458, 1257, 127);
		else if (destination.equalsIgnoreCase("Meniherk"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 1396, 1560, 31);
		else if (destination.equalsIgnoreCase("obsvillage"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 2234, 2284, 50);
		else if (destination.equalsIgnoreCase("Josnack"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 901, 2774, 62);
		else if (destination.equalsIgnoreCase("Anangke"))
			goTo(player, WorldMapType.THEOBOMOS.getId(), 2681, 847, 138);
		// Heiron
		else if (destination.equalsIgnoreCase("Heiron"))
			goTo(player, WorldMapType.HEIRON.getId(), 2540, 343, 411);
		else if (destination.equalsIgnoreCase("Heiron Observatory"))
			goTo(player, WorldMapType.HEIRON.getId(), 1423, 1334, 175);
		else if (destination.equalsIgnoreCase("Senemonea"))
			goTo(player, WorldMapType.HEIRON.getId(), 971, 686, 135);
		else if (destination.equalsIgnoreCase("Jeiaparan"))
			goTo(player, WorldMapType.HEIRON.getId(), 1635, 2693, 115);
		else if (destination.equalsIgnoreCase("Changarnerk"))
			goTo(player, WorldMapType.HEIRON.getId(), 916, 2256, 157);
		else if (destination.equalsIgnoreCase("Kishar"))
			goTo(player, WorldMapType.HEIRON.getId(), 1999, 1391, 118);
		else if (destination.equalsIgnoreCase("Arbolu"))
			goTo(player, WorldMapType.HEIRON.getId(), 170, 1662, 120);


		/**
		 * Asmodae
		 */
		// Pandaemonium
		else if  (destination.equalsIgnoreCase("Pandaemonium"))
			goTo(player, WorldMapType.PANDAEMONIUM.getId(), 1633, 1400, 194);
		// Marchutran
		else if (destination.equalsIgnoreCase("Marchutan"))
			goTo(player, WorldMapType.MARCHUTAN.getId(), 1557, 1429, 266);
		// Ishalgen
		else if (destination.equalsIgnoreCase("Ishalgen"))
			goTo(player, WorldMapType.ISHALGEN.getId(), 529, 2449, 281);
		else if (destination.equalsIgnoreCase("Anturoon"))
			goTo(player, WorldMapType.ISHALGEN.getId(), 940, 1707, 259);
		// Altgard
		else if (destination.equalsIgnoreCase("Altgard"))
			goTo(player, WorldMapType.ALTGARD.getId(), 1748, 1807, 254);
		else if (destination.equalsIgnoreCase("Basfelt"))
			goTo(player, WorldMapType.ALTGARD.getId(), 1903, 696, 260);
		else if (destination.equalsIgnoreCase("Trader"))
			goTo(player, WorldMapType.ALTGARD.getId(), 2680, 1024, 311);
		else if (destination.equalsIgnoreCase("Impetusiom"))
			goTo(player, WorldMapType.ALTGARD.getId(), 2643, 1658, 324);
		else if (destination.equalsIgnoreCase("Altgard Observatory"))
			goTo(player, WorldMapType.ALTGARD.getId(), 1468, 2560, 299);
		// Morheim
		else if (destination.equalsIgnoreCase("Morheim"))
			goTo(player, WorldMapType.MORHEIM.getId(), 308, 2274, 449);
		else if (destination.equalsIgnoreCase("Desert"))
			goTo(player, WorldMapType.MORHEIM.getId(), 634, 900, 360);
		else if (destination.equalsIgnoreCase("Slag"))
			goTo(player, WorldMapType.MORHEIM.getId(), 1772, 1662, 197);
		else if (destination.equalsIgnoreCase("Kellan"))
			goTo(player, WorldMapType.MORHEIM.getId(), 1070, 2486, 239);
		else if (destination.equalsIgnoreCase("Alsig"))
			goTo(player, WorldMapType.MORHEIM.getId(), 2387, 1742, 102);
		else if (destination.equalsIgnoreCase("Morheim Observatory"))
			goTo(player, WorldMapType.MORHEIM.getId(), 2794, 1122, 171);
		else if (destination.equalsIgnoreCase("Halabana"))
			goTo(player, WorldMapType.MORHEIM.getId(), 2346, 2219, 127);
		// Brusthonin
		else if (destination.equalsIgnoreCase("Brusthonin"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 2917, 2421, 15);
		else if (destination.equalsIgnoreCase("Baltasar"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 1413, 2013, 51);
		else if (destination.equalsIgnoreCase("Bollu"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 840, 2016, 307);
		else if (destination.equalsIgnoreCase("Edge"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 1523, 374, 231);
		else if (destination.equalsIgnoreCase("Bubu"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 526, 848, 76);
		else if (destination.equalsIgnoreCase("Settlers"))
			goTo(player, WorldMapType.BRUSTHONIN.getId(), 2917, 2417, 15);
		// Beluslan
		else if (destination.equalsIgnoreCase("Beluslan"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 398, 400, 222);
		else if (destination.equalsIgnoreCase("Besfer"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 533, 1866, 262);
		else if (destination.equalsIgnoreCase("Kidorun"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 1243, 819, 260);
		else if (destination.equalsIgnoreCase("Red Mane"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 2358, 1241, 470);
		else if (destination.equalsIgnoreCase("Kistenian"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 1942, 513, 412);
		else if (destination.equalsIgnoreCase("Hoarfrost"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 2431, 2063, 579);

		/**
		 * Balaurea
		 */
		// Inggison
		else if (destination.equalsIgnoreCase("Inggison"))
			goTo(player, WorldMapType.INGGISON.getId(), 1335, 276, 590);
		else if (destination.equalsIgnoreCase("Ufob"))
			goTo(player, WorldMapType.INGGISON.getId(), 382, 951, 460);
		else if (destination.equalsIgnoreCase("Soteria"))
			goTo(player, WorldMapType.INGGISON.getId(), 2713, 1477, 382);
		else if (destination.equalsIgnoreCase("Hanarkand"))
			goTo(player, WorldMapType.INGGISON.getId(), 1892, 1748, 327);
		// Gelkmaros
		else if (destination.equalsIgnoreCase("Gelkmaros"))
			goTo(player, WorldMapType.GELKMAROS.getId(), 1763, 2911, 554);
		else if (destination.equalsIgnoreCase("Subterranea"))
			goTo(player, WorldMapType.GELKMAROS.getId(), 2503, 2147, 464);
		else if (destination.equalsIgnoreCase("Rhonnam"))
			goTo(player, WorldMapType.GELKMAROS.getId(), 845, 1737, 354);
		// Silentera
		else if (destination.equalsIgnoreCase("Silentera"))
			goTo(player, 600010000, 583, 767, 300);

		/**
		 * Abyss
		 */
		else if (destination.equalsIgnoreCase("Reshanta"))
			goTo(player, WorldMapType.RESHANTA.getId(), 951, 936, 1667);
		else if (destination.equalsIgnoreCase("Abyss 1"))
			goTo(player, WorldMapType.RESHANTA.getId(), 2867, 1034, 1528);
		else if (destination.equalsIgnoreCase("Abyss 2"))
			goTo(player, WorldMapType.RESHANTA.getId(), 1078, 2839, 1636);
		else if (destination.equalsIgnoreCase("Abyss 3"))
			goTo(player, WorldMapType.RESHANTA.getId(), 1596, 2952, 2943);
		else if (destination.equalsIgnoreCase("Abyss 4"))
			goTo(player, WorldMapType.RESHANTA.getId(), 2054, 660, 2843);
		else if (destination.equalsIgnoreCase("Eye of Reshanta") ||destination.equalsIgnoreCase("Eye"))
			goTo(player, WorldMapType.RESHANTA.getId(), 1979, 2114, 2291);
		else if (destination.equalsIgnoreCase("Divine Fortress") || destination.equalsIgnoreCase("Divine"))
			goTo(player, WorldMapType.RESHANTA.getId(), 2130, 1925, 2322);

		/**
		 * Instances
		 */
		else if (destination.equalsIgnoreCase("Haramel"))
			goTo(player, 300200000, 176, 21, 144);
		else if (destination.equalsIgnoreCase("Nochsana") || destination.equalsIgnoreCase("NTC"))
			goTo(player, 300030000, 513, 668, 331);
		else if (destination.equalsIgnoreCase("Arcanis") || destination.equalsIgnoreCase("Sky Temple of Arcanis"))
			goTo(player, 320050000, 177, 229, 536);
		else if (destination.equalsIgnoreCase("Fire Temple") ||destination.equalsIgnoreCase("FT"))
			goTo(player, 320100000, 144, 312, 123);
		else if (destination.equalsIgnoreCase("Kromede") || destination.equalsIgnoreCase("Kromede Trial"))
			goTo(player, 300230000, 248, 244, 189);
		// Steel Rake
		else if (destination.equalsIgnoreCase("Steel Rake") || destination.equalsIgnoreCase("SR"))
			goTo(player, 300100000, 237, 506, 948);
		else if (destination.equalsIgnoreCase("Steel Rake Lower") || destination.equalsIgnoreCase("SR Low"))
			goTo(player, 300100000, 283, 453, 903);
		else if (destination.equalsIgnoreCase("Steel Rake Middle") || destination.equalsIgnoreCase("SR Mid"))
			goTo(player, 300100000, 283, 453, 953);
		else if (destination.equalsIgnoreCase("Indratu") || destination.equalsIgnoreCase("Indratu Fortress"))
			goTo(player, 310090000, 562, 335, 1015);
		else if (destination.equalsIgnoreCase("Azoturan") || destination.equalsIgnoreCase("Azoturan Fortress"))
			goTo(player, 310100000, 458, 428, 1039);
		else if (destination.equalsIgnoreCase("Bio Lab") || destination.equalsIgnoreCase("Aetherogenetics Lab"))
			goTo(player, 310050000, 225, 244, 133);
		else if (destination.equalsIgnoreCase("Adma") || destination.equalsIgnoreCase("Adma Stronghold"))
			goTo(player, 320130000, 450, 200, 168);
		else if (destination.equalsIgnoreCase("Alquimia") || destination.equalsIgnoreCase("Alquimia Research Center"))
			goTo(player, 320110000, 603, 527, 200);
		else if (destination.equalsIgnoreCase("Draupnir") || destination.equalsIgnoreCase("Draupnir Cave"))
			goTo(player, 320080000, 491, 373, 622);
		else if (destination.equalsIgnoreCase("Theobomos Lab") || destination.equalsIgnoreCase("Theobomos Research Lab"))
			goTo(player, 310110000, 477, 201, 170);
		else if (destination.equalsIgnoreCase("Dark Poeta") || destination.equalsIgnoreCase("DP"))
			goTo(player, 300040000, 1214, 412, 140);
		// Lower Abyss
		else if (destination.equalsIgnoreCase("Sulfur") || destination.equalsIgnoreCase("Sulfur Tree Nest"))
			goTo(player, 300060000, 462, 345, 163);
		else if (destination.equalsIgnoreCase("Right Wing") || destination.equalsIgnoreCase("Right Wing Chamber"))
			goTo(player, 300090000, 263, 386, 103);
		else if (destination.equalsIgnoreCase("Left Wing") || destination.equalsIgnoreCase("Left Wing Chamber"))
			goTo(player, 300080000, 672, 606, 321);
		// Upper Abyss
		else if (destination.equalsIgnoreCase("Asteria Chamber"))
			goTo(player, 300050000, 469, 568, 202);
		else if (destination.equalsIgnoreCase("Miren Chamber"))
			goTo(player, 300130000, 527, 120, 176);
		else if (destination.equalsIgnoreCase("Kysis Chamber"))
			goTo(player, 300120000, 528, 121, 176);
		else if (destination.equalsIgnoreCase("Krotan Chamber"))
			goTo(player, 300140000, 528, 109, 176);
		else if (destination.equalsIgnoreCase("Roah Chamber"))
			goTo(player, 300070000, 504, 396, 94);
		// Divine
		else if (destination.equalsIgnoreCase("Abyssal Splinter") || destination.equalsIgnoreCase("Core"))
			goTo(player, 300220000, 704, 153, 453);
		else if (destination.equalsIgnoreCase("Dredgion"))
			goTo(player, 300110000, 414, 193, 431);
		else if (destination.equalsIgnoreCase("Chantra") || destination.equalsIgnoreCase("Chantra Dredgion"))
			goTo(player, 300210000, 414, 193, 431);
		else if (destination.equalsIgnoreCase("Terath") || destination.equalsIgnoreCase("Terath Dredgion"))
			goTo(player, 300440000, 414, 193, 431);
		else if (destination.equalsIgnoreCase("Taloc") || destination.equalsIgnoreCase("Taloc's Hollow"))
			goTo(player, 300190000, 200, 214, 1099);
		// Udas
		else if (destination.equalsIgnoreCase("Udas") || destination.equalsIgnoreCase("Udas Temple"))
			goTo(player, 300150000, 637, 657, 134);
		else if (destination.equalsIgnoreCase("Udas Lower") || destination.equalsIgnoreCase("Udas Lower Temple"))
			goTo(player, 300160000, 1146, 277, 116);
		else if (destination.equalsIgnoreCase("Beshmundir") || destination.equalsIgnoreCase("BT") || destination.equalsIgnoreCase("Beshmundir Temple"))
			goTo(player, 300170000, 1477, 237, 243);
		// Padmaraska Cave
		else if (destination.equalsIgnoreCase("Padmaraska Cave"))
			goTo(player, 320150000, 385, 506, 66);

		/**
		 * Quest Instance Maps
		 */
		// TODO : Changer id maps
		else if (destination.equalsIgnoreCase("Karamatis 0"))
			goTo(player, 310010000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("Karamatis 1"))
			goTo(player, 310020000, 312, 274, 206);
		else if (destination.equalsIgnoreCase("Karamatis 2"))
			goTo(player, 310120000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("Aerdina"))
			goTo(player, 310030000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("Geranaia"))
			goTo(player, 310040000, 275, 168, 205);
		// Stigma quest
		else if (destination.equalsIgnoreCase("Sliver") || destination.equalsIgnoreCase("Sliver of Darkness"))
			goTo(player, 310070000, 247, 249, 1392);
		else if (destination.equalsIgnoreCase("Space") || destination.equalsIgnoreCase("Space of Destiny"))
			goTo(player, 320070000, 246, 246, 125);
		else if (destination.equalsIgnoreCase("Ataxiar 1"))
			goTo(player, 320010000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("Ataxiar 2"))
			goTo(player, 320020000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("Bregirun"))
			goTo(player, 320030000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("Nidalber"))
			goTo(player, 320040000, 275, 168, 205);

		/**
		 * Arenas
		 */
		else if (destination.equalsIgnoreCase("Sanctum Arena"))
			goTo(player, 310080000, 275, 242, 159);
		else if (destination.equalsIgnoreCase("Triniel Arena"))
			goTo(player, 320090000, 275, 239, 159);
		// Empyrean Crucible
		else if (destination.equalsIgnoreCase("Crucible 1-0"))
			goTo(player, 300300000, 380, 350, 95);
		else if (destination.equalsIgnoreCase("Crucible 1-1"))
			goTo(player, 300300000, 346, 350, 96);
		else if (destination.equalsIgnoreCase("Crucible 5-0"))
			goTo(player, 300300000, 1265, 821, 359);
		else if (destination.equalsIgnoreCase("Crucible 5-1"))
			goTo(player, 300300000, 1256, 797, 359);
		else if (destination.equalsIgnoreCase("Crucible 6-0"))
			goTo(player, 300300000, 1596, 150, 129);
		else if (destination.equalsIgnoreCase("Crucible 6-1"))
			goTo(player, 300300000, 1628, 155, 126);
		else if (destination.equalsIgnoreCase("Crucible 7-0"))
			goTo(player, 300300000, 1813, 797, 470);
		else if (destination.equalsIgnoreCase("Crucible 7-1"))
			goTo(player, 300300000, 1785, 797, 470);
		else if (destination.equalsIgnoreCase("Crucible 8-0"))
			goTo(player, 300300000, 1776, 1728, 304);
		else if (destination.equalsIgnoreCase("Crucible 8-1"))
			goTo(player, 300300000, 1776, 1760, 304);
		else if (destination.equalsIgnoreCase("Crucible 9-0"))
			goTo(player, 300300000, 1357, 1748, 320);
		else if (destination.equalsIgnoreCase("Crucible 9-1"))
			goTo(player, 300300000, 1334, 1741, 316);
		else if (destination.equalsIgnoreCase("Crucible 10-0"))
			goTo(player, 300300000, 1750, 1255, 395);
		else if (destination.equalsIgnoreCase("Crucible 10-1"))
			goTo(player, 300300000, 1761, 1280, 395);
		// Arena Of Chaos
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 1"))
			goTo(player, 300350000, 1332, 1078, 340);
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 2"))
			goTo(player, 300350000, 599, 1854, 227);
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 3"))
			goTo(player, 300350000, 663, 265, 512);
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 4"))
			goTo(player, 300350000, 1840, 1730, 302);
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 5"))
			goTo(player, 300350000, 1932, 1228, 270);
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 6"))
			goTo(player, 300350000, 1949, 946, 224);

		/**
		 * Miscellaneous
		 */
		// Prison
		else if (destination.equalsIgnoreCase("Prison LF") || destination.equalsIgnoreCase("Prison Elyos"))
			goTo(player, 510010000, 256, 256, 49);
		else if (destination.equalsIgnoreCase("Prison DF") || destination.equalsIgnoreCase("Prison Asmos"))
			goTo(player, 520010000, 256, 256, 49);
		// Test
		else if (destination.equalsIgnoreCase("test1"))
			goTo(player, 900020000, 104, 66, 25);
		else if (destination.equalsIgnoreCase("test2"))
			goTo(player, 900020000, 144, 136, 20);
		else if (destination.equalsIgnoreCase("test3"))
			goTo(player, 900030000, 228, 171, 49);
		else if (destination.equalsIgnoreCase("test"))
			goTo(player, 900100000, 196, 187, 20);
		// Unknown
		else if (destination.equalsIgnoreCase("IDAbPro"))
			goTo(player, 300010000, 270, 200, 206);
		// GM zone
		else if (destination.equalsIgnoreCase("gm"))
			goTo(player, 120020000, 1458, 1198, 299);

		/**
		 * 2.5 Maps
		 */
		else if (destination.equalsIgnoreCase("Kaisinel Academy"))
			goTo(player, 110070000, 459, 251, 128);
		else if (destination.equalsIgnoreCase("Marchutan Priory"))
			goTo(player, 120080000, 577, 250, 94);
		else if (destination.equalsIgnoreCase("Esoterrace"))
			goTo(player, 300250000, 333, 437, 326);

		/**
		 * 3.0 Maps
		 */
		else if (destination.equalsIgnoreCase("Pernon"))
			goTo(player, 710010000, 1069, 1539, 98);
		else if (destination.equalsIgnoreCase("Oriel"))
			goTo(player, 700010000, 1261, 1845, 98);
		else if (destination.equalsIgnoreCase("Sarpan"))
			goTo(player, 600020000, 1374, 1455, 600);
		else if (destination.equalsIgnoreCase("Tiamaranta"))
			goTo(player, 600030000, 40, 1732, 297);
		else if (destination.equalsIgnoreCase("Tiamaranta Eye"))
			goTo(player, 600040000, 159, 768, 1202);
		else if (destination.equalsIgnoreCase("Steel Rake Cabin") || destination.equalsIgnoreCase("Steel Rake Solo"))
			goTo(player, 300460000, 248, 244, 189);
		else if (destination.equalsIgnoreCase("Aturam") || destination.equalsIgnoreCase("Aturam Sky Fortress"))
			goTo(player, 300240000, 636, 446, 655);
		else if (destination.equalsIgnoreCase("Elementis") || destination.equalsIgnoreCase("Elementis Forest"))
			goTo(player, 300260000, 176, 612, 231);
		else if (destination.equalsIgnoreCase("Argent") || destination.equalsIgnoreCase("Argent Manor"))
			goTo(player, 300270000, 1005, 1089, 70);
		else if (destination.equalsIgnoreCase("Rentus") || destination.equalsIgnoreCase("Rentus Base"))
			goTo(player, 300280000, 579, 606, 153);
		else if (destination.equalsIgnoreCase("Raksang"))
			goTo(player, 300310000, 665, 735, 1188);
		else if (destination.equalsIgnoreCase("Muada") || destination.equalsIgnoreCase("Muada's Trencher"))
			goTo(player, 300380000, 492, 553, 106);
		else if (destination.equalsIgnoreCase("Satra"))
			goTo(player, 300470000, 510, 180, 159);

		/**
		* 4.3
		*/
		else if (destination.equalsIgnoreCase("Katalam"))
			goTo(player, WorldMapType.NORHTERN_KATALAM.getId(), 400, 2717, 143);
		else if (destination.equalsIgnoreCase("Katalamd"))
			goTo(player, WorldMapType.NORHTERN_KATALAM.getId(), 363, 385, 281);
		else if (destination.equalsIgnoreCase("Danaria"))
			goTo(player, WorldMapType.SOUTHERN_KATALAM.getId(), 2544, 1699, 142);
		else if (destination.equalsIgnoreCase("Underl"))
			goTo(player, 210090000, 673, 642, 514);
		else if (destination.equalsIgnoreCase("Underd"))
			goTo(player, 220100000, 673, 642, 514);
		else if (destination.equalsIgnoreCase("Katalamadg"))
			goTo(player, 300800000, 118, 114, 131);
		else if (destination.equalsIgnoreCase("KatalamadgR"))
			goTo(player, 300900000, 147, 145, 124);
		else if (destination.equalsIgnoreCase("Runadium"))
			goTo(player, 301110000, 256, 256, 241);
		else if (destination.equalsIgnoreCase("KamarBat"))
			goTo(player, 301120000, 1333, 1507, 593);
		else if (destination.equalsIgnoreCase("Run"))
			goTo(player, 301140000, 1056, 847, 282);
		else if (destination.equalsIgnoreCase("Lukibuki1"))
			goTo(player, 301160000, 470, 566, 201);
		else if (destination.equalsIgnoreCase("Lukibuki2"))
			goTo(player, 301200000, 470, 566, 201);
		else if (destination.equalsIgnoreCase("Idgel"))
			goTo(player, 301170000, 534, 456, 102);
		else if (destination.equalsIgnoreCase("Pustota"))
			goTo(player, 301180000, 252, 196, 340);
		else if (destination.equalsIgnoreCase("Hram"))
			goTo(player, 301190000, 101, 150, 236);
		else if (destination.equalsIgnoreCase("RozaS1"))
			goTo(player, 301010000, 282, 454, 902);
		else if (destination.equalsIgnoreCase("RozaS2"))
			goTo(player, 301020000, 241, 504, 948);
		else if (destination.equalsIgnoreCase("RozaG1"))
			goTo(player, 301040000, 240, 507, 948);
		else if (destination.equalsIgnoreCase("RozaG2"))
			goTo(player, 301050000, 572, 505, 1023);
		/**
		 * 3.5
		 */
		else if(destination.equalsIgnoreCase("Dragon Lords Refuge")){
			goTo(player, 300520000, 506, 516, 242);
		}
		else if(destination.equalsIgnoreCase("Throne of Blood") || destination.equalsIgnoreCase("Tiamat")){
			goTo(player, 300520000, 495, 528, 417);
		}
            // New map 4.7
		    else if (destination.equalsIgnoreCase("kaldord"))
			    goTo(player, 600090000, 397, 1380, 163);
		    else if (destination.equalsIgnoreCase("kaldor"))
			    goTo(player, 600090000, 1269, 1340, 195);
		    else if (destination.equalsIgnoreCase("gerha"))
			    goTo(player, 600100000, 102, 108, 348);
		    else if (destination.equalsIgnoreCase("gerhad"))
			    goTo(player, 600100000, 1836, 1782, 306);
		    else if (destination.equalsIgnoreCase("pangea"))
			    goTo(player, 400020000, 1238, 1232, 1518);
		    else if (destination.equalsIgnoreCase("pangeasub"))
			    goTo(player, 400030000, 509, 513, 675);
		    else if (destination.equalsIgnoreCase("Aspida"))
			    goTo(player, 400040000, 1238, 1232, 1518);
		    else if (destination.equalsIgnoreCase("pangea3"))
			    goTo(player, 400050000, 1238, 1232, 1518);
		    else if (destination.equalsIgnoreCase("pangea4"))
			    goTo(player, 400060000, 1238, 1232, 1518);
		    else if (destination.equalsIgnoreCase("gerhainv1"))
			    goTo(player, 220080000, 1158, 1075, 304);
		    else if (destination.equalsIgnoreCase("gerhainv2"))
			    goTo(player, 600100000, 681, 1001, 276);
		    else if (destination.equalsIgnoreCase("gerhainv3"))
			    goTo(player, 600100000, 387, 1809, 227);
		    else if (destination.equalsIgnoreCase("gerhainv4"))
			    goTo(player, 600100000, 1838, 141, 243);

		    else if (destination.equalsIgnoreCase("singea"))
			    goTo(player, 210070000, 2914, 808, 570);
		    else if (destination.equalsIgnoreCase("enshar"))
			    goTo(player, 220080000, 454, 2261, 222);
		else
			PacketSendUtility.sendMessage(player, "Could not find the specified destination !");
	}

	private static void goTo(final Player player, int worldId, float x, float y, float z) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
		if (destinationMap.isInstanceType())
			TeleportService2.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z);
		else
			TeleportService2.teleportTo(player, worldId, x, y, z);
	}

	private static int getInstanceId(int worldId, Player player) {
		if (player.getWorldId() == worldId)	{
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
			if (registeredInstance != null)
				return registeredInstance.getInstanceId();
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, player);
		return newInstance.getInstanceId();
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax : //goto <location>");
	}
}
