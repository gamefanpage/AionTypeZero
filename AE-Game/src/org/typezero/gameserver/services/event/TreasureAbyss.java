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


package org.typezero.gameserver.services.event;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.configs.main.EventsConfig;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Romanz
 */
public class TreasureAbyss {
	private static final Logger log = LoggerFactory.getLogger(TreasureAbyss.class);
	private static List<float[]> floatArray = new ArrayList<float[]>();
	private static final String ABYSS_EVENT_SCHEDULE = EventsConfig.ABYSS_EVENT_SCHEDULE;
	private static int WORLD_ID = 400010000;
	private static int NPC_ID = 801988;
	private static int[] rewards = {186000097, 186000235, 186000051, 186000052, 186000053, 186000054, 186000055, 186000056, 186000057, 186000058, 186000059, 186000060,
	186000061, 186000062, 186000063, 186000064, 186000065, 186000066, 186000147, 186000242, 166000085, 166000090, 166000095, 166000100, 166000105, 166000110, 166000115};
        private static Npc mainN;

        public static void ScheduleCron(){
             CronService.getInstance().schedule(new Runnable(){

                  @Override
                  public void run() {
                       startEvent();
                  }

             },ABYSS_EVENT_SCHEDULE);
             log.info("Treasure Abyss Event start to:" + EventsConfig.ABYSS_EVENT_SCHEDULE + " duration 30 min");
        }

        public static void startEvent(){
                initCoordinates();

                World.getInstance().doOnAllPlayers(new Visitor<Player>(){

                        @Override
                        public void visit(Player object) {
                                PacketSendUtility.sendYellowMessageOnCenter(object, MuiService.getInstance().getMessage("ABYSS_EVENT_START"));
                        }
                });

                initPig();

                ThreadPoolManager.getInstance().schedule(new Runnable(){

                     @Override
                     public void run() {
                          endEvent();
                     }
                }, 30 * 60 * 1000);

        }

        private static void initPig() {
                float[] coords = floatArray.get(Rnd.get(floatArray.size()));
                SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(WORLD_ID, NPC_ID, coords[0], coords[1], coords[2], (byte) coords[3]);
                VisibleObject mainObject = SpawnEngine.spawnObject(spawn, 1);
                if(mainObject instanceof Npc) {
                      mainN = (Npc) mainObject;
                }
                ActionObserver observer = new ActionObserver(ObserverType.ATTACKED){

                        @Override
                        public void attacked(Creature creature) {
                                if(creature instanceof Player) {
                                        final Player player = (Player) creature;
                                        final int id = rewards[Rnd.get(rewards.length)];
                                        ItemService.addItem(player, id, 1);
                                        World.getInstance().doOnAllPlayers(new Visitor<Player>(){

                                                @Override
                                                public void visit(Player object) {
                                                        PacketSendUtility.sendYellowMessageOnCenter(object, player.getName() + MuiService.getInstance().getMessage("ABYSS_EVENT_REWARD",id));
                                                }
                                        });
                                }
                                mainN.getObserveController().removeObserver(this);
                                mainN.getController().onDelete();
                                initPig();
                        }
                };
                if(mainN != null) {
                        mainN.getObserveController().attach(observer);
                }
        }

        public static void endEvent(){
                World.getInstance().doOnAllPlayers(new Visitor<Player>(){

                        @Override
                        public void visit(Player object) {
                                PacketSendUtility.sendYellowMessageOnCenter(object, MuiService.getInstance().getMessage("ABYSS_EVENT_STOP"));
                        }
                });

                mainN.getController().onDelete();
        }

        private static void initCoordinates(){
				floatArray.add(new float[] { 1125.5845f, 2092.2786f, 2886.942f, 0f } );
				floatArray.add(new float[] { 898.8214f, 2055.8772f, 2947.6978f, 0f } );
				floatArray.add(new float[] { 1037.4642f, 1913.2968f, 2921.2324f, 0f } );
				floatArray.add(new float[] { 1077.2566f, 1884.0425f, 2918.736f, 0f } );
				floatArray.add(new float[] { 1081.3489f, 1929.2251f, 2918.9656f, 0f } );
				floatArray.add(new float[] { 1052.0624f, 1958.3436f, 2914.1443f, 0f } );
				floatArray.add(new float[] { 925.501f, 1941.7659f, 2900.174f, 0f } );
				floatArray.add(new float[] { 910.02704f, 1973.1254f, 2926.4226f, 0f } );
				floatArray.add(new float[] { 926.10034f, 1912.359f, 2922.1255f, 0f } );
				floatArray.add(new float[] { 864.4592f, 2132.6792f, 2877.5903f, 0f } );
				floatArray.add(new float[] { 813.31464f, 2103.9673f, 2904.7732f, 0f } );
				floatArray.add(new float[] { 637.50116f, 2190.0144f, 2740.6816f, 0f } );
				floatArray.add(new float[] { 659.5982f, 2058.945f, 2727.988f, 0f } );
				floatArray.add(new float[] { 703.89545f, 2009.956f, 2732.8267f, 0f } );
				floatArray.add(new float[] { 452.45828f, 2054.4714f, 2748.3901f, 0f } );
				floatArray.add(new float[] { 879.64886f, 2072.6255f, 2986.4968f, 0f } );
				floatArray.add(new float[] { 871.85144f, 1966.9083f, 2915.3523f, 0f } );
				floatArray.add(new float[] { 1024.3107f, 1924.4081f, 2935.2603f, 0f } );
				floatArray.add(new float[] { 1066.7672f, 1907.3596f, 2939.5806f, 0f } );
				floatArray.add(new float[] { 1007.0575f, 1909.2692f, 2928.553f, 0f } );
				floatArray.add(new float[] { 844.80316f, 2223.1123f, 2866.799f, 0f } );
				floatArray.add(new float[] { 789.19257f, 2296.9492f, 2849.082f, 0f } );
				floatArray.add(new float[] { 799.8223f, 2342.593f, 2857.1492f, 0f } );
				floatArray.add(new float[] { 729.75464f, 2390.949f, 2903.714f, 0f } );
				floatArray.add(new float[] { 1189.0593f, 2034.7089f, 2871.6875f, 0f } );
				floatArray.add(new float[] { 1211.694f, 2169.0952f, 2881.6824f, 0f } );
				floatArray.add(new float[] { 1146.1387f, 2222.8052f, 2893.3176f, 0f } );
				floatArray.add(new float[] { 965.65607f, 2079.0503f, 2859.521f, 0f } );
				floatArray.add(new float[] { 1070.2789f, 2073.4214f, 2868.2988f, 0f } );
				floatArray.add(new float[] { 1080.5293f, 2122.5925f, 2868.7004f, 0f } );
        }
}
