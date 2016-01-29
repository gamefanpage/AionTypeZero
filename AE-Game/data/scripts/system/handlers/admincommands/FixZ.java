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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnGroup2;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Made in Russia.
 *
 */
public class FixZ extends AdminCommand {

    private static final Logger log = Logger.getLogger(FixZ.class.getName());

    private Npc npc = null;
    private int numofspawns = 0;
    private int spawned = 0;

    public FixZ() {
        super("fixz");
    }

    @Override
    public void execute(final Player admin, String[] params) {
        if (admin.getAccessLevel() < 5) { //AdminConfig.COMMAND_FIXZ) {
            PacketSendUtility.sendMessage(admin, "У вас нет прав, чтобы использовать эту команду!");
            return;
        }
        if (admin.getTarget() != null) {
            if (admin.getTarget() instanceof Npc) {
                Npc target = (Npc) admin.getTarget();
                final SpawnTemplate temp = target.getSpawn();
                int respawnTime = 295;
                boolean permanent = true;

                //delete spawn,npc
                target.getController().delete();

                //spawn npc
                int worldId = temp.getWorldId();
                int templateId = temp.getNpcId();
                float x = temp.getX();
                float y = temp.getY();
                float z = admin.getZ();
                byte heading = temp.getHeading();

                SpawnTemplate spawn = SpawnEngine.addNewSpawn(worldId, templateId, x, y, z, heading, respawnTime);

                if (spawn == null) {
                    PacketSendUtility.sendMessage(admin, "There is no template with id " + templateId);
                    return;
                }

                VisibleObject visibleObject = SpawnEngine.spawnObject(spawn, admin.getInstanceId());

                if (visibleObject == null) {
                    PacketSendUtility.sendMessage(admin, "npc id " + templateId + " was not found!");
                }
                else if (permanent) {
                    try {
                        DataManager.SPAWNS_DATA2.saveSpawn(admin, visibleObject, false);
                    }
                    catch (IOException e) {
                        PacketSendUtility.sendMessage(admin, "Could not save spawn" + e);
                    }
                }

                String objectName = visibleObject.getObjectTemplate().getName();
                PacketSendUtility.sendMessage(admin, objectName + " FixZ");
            }
        }
        else if ((params.length == 0 || params.length == 1 || params.length == 2) && "start".equalsIgnoreCase(params[0])) {
            int stop = 0;
            if (params.length == 1) {
                stop = -1;
            }
            else if (params.length == 2 && "start".equalsIgnoreCase(params[0])) {
                stop = Integer.parseInt(params[1]);
            }
            final Player admin2 = admin;
            List<SpawnGroup2> spawngroups = DataManager.SPAWNS_DATA2.getSpawnsByWorldId(admin2.getWorldId());
            List<SpawnTemplate> templates = new ArrayList<SpawnTemplate>();
            PacketSendUtility.sendMessage(admin2, "Fix z coord will start in 10 seconds.");
            //load spawns
            for (final SpawnGroup2 spawngroup : spawngroups) {
                templates.addAll(spawngroup.getSpawnTemplates());
                numofspawns += spawngroup.getSpawnTemplates().size();
            }
            PacketSendUtility.sendMessage(admin2, "Aprox time: " + ((numofspawns * 3.6) / 60) + " minutes.");
            //execute
            int time = 9000;//time before start
            int counter = 0;
            for (final SpawnTemplate template : templates) {
                if (counter >= stop && stop >= 0) {
                    counter = 0;
                    break;
                }
                ++counter;
                time += 800;
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        TeleportService2.teleportTo(admin2, template.getWorldId(), template.getX(), template.getY(), template.getZ());

                        admin2.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
                            @Override
                            public void visit(Npc n) {
                                if (MathUtil.getDistance((int) n.getX(), (int) n.getY(), (int) admin2.getX(), (int) admin2.getY()) < 0.1) {
                                    npc = n;
                                }
                            }
                        });
                        //delete spawn
                        //DataManager.SPAWNS_DATA2.removeSpawn(template);
                    }
                }, time);
                time += 800;
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (npc != null) {
                            PacketSendUtility.broadcastPacketAndReceive(admin2, new SM_FORCED_MOVE(npc, admin2));
                            npc.getController().delete();
                        }
                    }
                }, time);
                time += 3000;
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (npc != null) {
                            //create groupname
                            //ex: Aetherogenetics Lab Entrance (Object Normal lvl:1)
                            StringBuilder comment = new StringBuilder();
                            comment.append(npc.getObjectTemplate().getName()).append(" (");
                            int isObject = npc.getSpawn().getStaticId();
                            if (isObject != 0) {
                                comment.append("Object");
                            }
                            else {
                                comment.append("NPC");
                            }
                            comment.append(" ").append(npc.getObjectTemplate().getRank().name()).append(" ");
                            comment.append("lvl:").append(npc.getLevel()).append(")");
                            //spawn npc
                            int respawnTime = 295;
                            boolean permanent = true;
                            int worldId = template.getWorldId();
                            int templateId = template.getNpcId();
                            float x = template.getX();
                            float y = template.getY();
                            float z = admin.getZ();
                            byte heading = template.getHeading();
                            //check for similar entry
                            SpawnTemplate spawn = SpawnEngine.addNewSpawn(worldId, templateId, x, y, z, heading, respawnTime);
                            if (spawn == null) {
                                log.log(Level.INFO, "[AUDIT]Deleted npc id={0}: //moveto {1} {2} {3} {4}", new Object[]{templateId, worldId, x, y, z});
                                //DAOManager.getDAO(SpawnDAO.class).deleteSpawn(spawnId);
                            }

                            VisibleObject visibleObject = SpawnEngine.spawnObject(spawn, admin.getInstanceId());

                            if (visibleObject == null) {
                                PacketSendUtility.sendMessage(admin, "npc id " + template.getNpcId() + " was not found!");
                            }
                            else if (permanent) {
                                try {
                                    DataManager.SPAWNS_DATA2.saveSpawn(admin, visibleObject, false);
                                }
                                catch (IOException e) {
                                    PacketSendUtility.sendMessage(admin, "Could not save spawn" + e);
                                }
                            }
                            ++spawned;
                            PacketSendUtility.sendMessage(admin2, spawned + ". " + comment.toString() + " spawned");

                            //reset npc
                            npc = null;
                        }
                        else {
                            if (template != null) {
                                log.log(Level.INFO, "[AUDIT]Missing npc id={0}: //moveto {1} {2} {3} {4}", new Object[]{template.getNpcId(), template.getWorldId(), template.getX(), template.getY(), template.getZ()});
                            }
                        }
                    }
                }, time);
            }
            templates = null;
            spawngroups = null;
        }
        else {
            PacketSendUtility.sendMessage(admin, "Syntax: //fixz <start> <counter>");
        }
        PacketSendUtility.sendMessage(admin, "Number of spawns: " + numofspawns);
    }
}
