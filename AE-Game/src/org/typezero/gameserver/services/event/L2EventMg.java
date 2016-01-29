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
public class L2EventMg {
	private static final Logger log = LoggerFactory.getLogger(L2EventMg.class);
	private static List<float[]> floatArray = new ArrayList<float[]>();
	private static final String PIG_EVENT_SCHEDULE = EventsConfig.PIG_EVENT_SCHEDULE;
	private static int WORLD_ID = 600040000;
	private static int NPC_ID = 219158;
	private static int[] rewards = {
164002096, 164002097, 164002093, 164002094, 164002099, 164002100, 164002093, 164002094, 164002099, 164002100,
186000030, 186000031, 164000076, 164000134, 164000073, 186000143, 162000080, 161000003, 162000077, 162000078,
186000030, 186000031, 164000076, 164000134, 164000073, 186000143, 162000080, 160009014, 162000077, 162000078,
186000030, 186000031, 164000076, 186000096, 186000147, 186000143, 162000080, 166000090, 166000100, 166000110,
186000030, 186000031, 164000076, 164000134, 164000073, 186000143, 162000080, 161000003, 162000077, 162000078,
164002096, 164002097, 164002093, 164002094, 164002099, 164002100, 164002093, 164002094, 164002099, 164002100,
186000030, 186000031, 164000076, 164000134, 164000073, 186000143, 162000080, 160009016, 162000077, 162000078,
186000030, 186000031, 164000076, 186000096, 186000147, 186000143, 162000080, 166000090, 166000100, 166000110,
164002096, 164002097, 164002093, 164002094, 164002099, 164002100, 164002093, 164002094, 164002099, 164002100,
188051508, 188051509, 188051510, 188051396, 188051411, 188051412, 188051416, 188051389, 188051430, 188051395, 188051398,
164002096, 164002097, 164002093, 164002094, 164002099, 164002100, 164002093, 164002094, 164002099, 164002100,
188052074, 188052075, 188052718, 188052438, 188100099};
        private static Npc mainN;

        public static void ScheduleCron(){
             CronService.getInstance().schedule(new Runnable(){

                  @Override
                  public void run() {
                       startEvent(); //To change body of generated methods, choose Tools | Templates.
                  }

             },PIG_EVENT_SCHEDULE);
             log.info("Pig Event start to:" + EventsConfig.PIG_EVENT_SCHEDULE + " duration 30 min");
        }

        public static void startEvent(){
                initCoordinates();

                World.getInstance().doOnAllPlayers(new Visitor<Player>(){

                        @Override
                        public void visit(Player object) {
                                PacketSendUtility.sendYellowMessageOnCenter(object, MuiService.getInstance().getMessage("PIG_EVENT_START"));
                        }
                });

                initPig();

                ThreadPoolManager.getInstance().schedule(new Runnable(){

                     @Override
                     public void run() {
                          endEvent(); //To change body of generated methods, choose Tools | Templates.
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
                                                        PacketSendUtility.sendYellowMessageOnCenter(object, player.getName() + MuiService.getInstance().getMessage("PIG_EVENT_REWARD",id));
                                                }
                                        });
                                }
                                mainN.getObserveController().removeObserver(this);
                                //mainN.setSpawn(null);
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
                                PacketSendUtility.sendYellowMessageOnCenter(object, MuiService.getInstance().getMessage("PIG_EVENT_STOP"));
                        }
                });

                mainN.getController().onDelete();
        }

        private static void initCoordinates(){
				floatArray.add(new float[] { 1287.7842f, 896.32697f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 1266.8105f, 999.3153f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 1224.5043f, 1063.8138f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 1158.3838f, 1154.4282f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 1100.2507f, 1209.3225f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 959.8825f, 1277.393f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 875.1307f, 1301.4321f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 812.5414f, 1306.32f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 658.94275f, 1299.6395f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 555.50305f, 1282.7351f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 438.0395f, 1225.2784f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 293.6351f, 1088.0068f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 240.17729f, 984.6521f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 218.67097f, 900.68524f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 220.86354f, 618.2111f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 249.5082f, 511.47714f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 300.70718f, 440.32077f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 436.24792f, 300.0017f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 510.4865f, 271.12964f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 641.7248f, 229.65675f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 843.592f, 234.03636f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 947.5368f, 254.0872f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 1128.1399f, 346.94043f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 1191.4955f, 427.04333f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 1262.0094f, 533.66895f, 1183.8578f, 0f } );
				floatArray.add(new float[] { 1282.23f, 628.17596f, 1183.8578f, 0f } );
        }
}
