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


package org.typezero.gameserver.dataholders;

import org.typezero.gameserver.model.drop.NpcLvlDrop;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.world.World;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.drop.Drop;
import org.typezero.gameserver.model.drop.DropGroup;
import org.typezero.gameserver.model.drop.NpcDrop;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import static org.apache.commons.io.filefilter.FileFilterUtils.suffixFileFilter;

/**
 * @author MrPoke
 *
 */
public class NpcDropData {

	private static Logger log = LoggerFactory.getLogger(DataManager.class);

	private List<NpcDrop> npcDrop;

	/**
	 * @return the npcDrop
	 */
	public List<NpcDrop> getNpcDrop() {
		return npcDrop;
	}


	/**
	 * @param npcDrop the npcDrop to set
	 */
	public void setNpcDrop(List<NpcDrop> npcDrop) {
		this.npcDrop = npcDrop;
	}

	public int size() {
		return npcDrop.size();
	}

	public static NpcDropData load() {

        // start xml-drop load
        HashMap<Integer,NpcDrop> xmlNpcDrops = new HashMap<Integer, NpcDrop>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        File dir = new File("./data/static_data/npc_drop");
        Document doc = null;
        for (File file : FileUtils.listFiles(dir, suffixFileFilter(".xml"), null)) {
            try {
                doc = factory.newDocumentBuilder().parse(file);
            }
            catch(Exception e) {
                System.out.print("Error while parse");
            }

            Node n = doc.getFirstChild();
            if (n != null)
                for(Node npcNode = n.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling())
                    if(npcNode.getNodeName().equals("npc_drop")) {
                        int npc_id = Integer.parseInt(npcNode.getAttributes().getNamedItem("npc_id").getNodeValue());
                        String dropType = npcNode.getAttributes().getNamedItem("droptype").getNodeValue();
                        int dropTypeInt;
                        if (dropType.equals("replace"))
                            dropTypeInt = 1;
                        else
                            dropTypeInt = 2;
                        List<DropGroup> dropGroups = new ArrayList<DropGroup>();
                        for(Node dropGroupNode = npcNode.getFirstChild(); dropGroupNode != null; dropGroupNode = dropGroupNode.getNextSibling())
                            if (dropGroupNode.getNodeName().equals("drop_group")) {
                                String dropGroupName = dropGroupNode.getAttributes().getNamedItem("name").getNodeValue();
                                byte raceId = dropGroupNode.getAttributes().getNamedItem("race") != null ? Byte.parseByte(dropGroupNode.getAttributes().getNamedItem("race").getNodeValue()) : 2;
                                Race dropGroupRace;
                                switch(raceId){
                                    case 0:
                                        dropGroupRace = Race.ELYOS;
                                        break;
                                    case 1:
                                        dropGroupRace = Race.ASMODIANS;
                                        break;
                                    default:
                                        dropGroupRace = Race.PC_ALL;
                                }
                                Boolean dropGroupUseCategory = dropGroupNode.getAttributes().getNamedItem("use_category") != null ? Boolean.parseBoolean(dropGroupNode.getAttributes().getNamedItem("use_category").getNodeValue()) : true;
                                List<Drop> xmlDrops = new ArrayList<Drop>();
                                for(Node dropNode = dropGroupNode.getFirstChild(); dropNode != null; dropNode = dropNode.getNextSibling()) {
                                    if (dropNode.getNodeName().equals("drop")) {
                                        xmlDrops.add(Drop.loadxml(dropNode));
                                    }
                                }
                                DropGroup dropGroupXML = new DropGroup(xmlDrops, dropGroupRace, dropGroupUseCategory, dropGroupName);
                                dropGroups.add(dropGroupXML);
                            }
                        NpcDrop npcDropXML = new NpcDrop(dropGroups, npc_id, dropTypeInt);
                        xmlNpcDrops.put(npc_id, npcDropXML);
                    }
        }
        // end xml-drop read


        List<Drop> drops = new ArrayList<Drop>();
		List<String> names = new ArrayList<String>();
		List<NpcDrop> npcDrops = new ArrayList<NpcDrop>();
		FileChannel roChannel = null;
		MappedByteBuffer buffer;

        try {
            roChannel = new RandomAccessFile("data/static_data/npc_drop.dat", "r").getChannel();
            int size = (int) roChannel.size();
            buffer = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, size).load();
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            int count = buffer.getInt();
            for (int i = 0; i<count; i++){
                drops.add(Drop.load(buffer));
            }

            count = buffer.getInt();

            for (int i = 0; i<count; i++){
                int lenght = buffer.get();
                byte[] byteString = new byte[lenght];
                buffer.get(byteString);
                String name = new String(byteString);
                names.add(name);
            }

            count = buffer.getInt();
            for (int i = 0; i<count; i++){
                int npcId = buffer.getInt();
                List<DropGroup> dropGroupList = new ArrayList<DropGroup>();
                int groupCount = buffer.getInt();
                for (int groupIndex = 0; groupIndex<groupCount; groupIndex++){
                    Race race;
                    byte raceId = buffer.get();
                    switch(raceId){
                        case 0:
                            race = Race.ELYOS;
                            break;
                        case 1:
                            race = Race.ASMODIANS;
                            break;
                        default:
                            race = Race.PC_ALL;
                    }

                    boolean useCategory = buffer.get() == 1 ? true:false;
                    String groupName = names.get(buffer.getShort());

                    int dropCount = buffer.getInt();
                    List<Drop> dropList = new ArrayList<Drop>();
                    for (int dropIndex = 0; dropIndex < dropCount; dropIndex++){
                        dropList.add(drops.get(buffer.getInt()));
                    }
                    DropGroup dropGroup = new DropGroup(dropList, race, useCategory, groupName);
                    dropGroupList.add(dropGroup);
                }

                if (xmlNpcDrops.get(npcId) != null){ //если в xml есть данные для моба
                    if (xmlNpcDrops.get(npcId).getChangeType() == 1)
                        dropGroupList.clear();
                    dropGroupList.addAll(xmlNpcDrops.get(npcId).getDropGroup());
                    xmlNpcDrops.remove(npcId);
                }
                NpcDrop npcDrop = new NpcDrop(dropGroupList, npcId);
                npcDrops.add(npcDrop);

                NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
                if (npcTemplate != null){
                    npcTemplate.setNpcDrop(npcDrop);
                }

            }

            for (Map.Entry<Integer, NpcDrop> nd : xmlNpcDrops.entrySet()) {
                List<DropGroup> dropGroupList = new ArrayList<DropGroup>();
                dropGroupList.addAll(xmlNpcDrops.get(nd.getKey()).getDropGroup());

                NpcDrop npcDrop = new NpcDrop(dropGroupList, nd.getKey());
                npcDrops.add(npcDrop);

                NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(nd.getKey());
                if (npcTemplate != null){
                    npcTemplate.setNpcDrop(npcDrop);
                }

            }

            drops.clear();
            drops = null;
            names.clear();
            names = null;
        }
        catch (FileNotFoundException e) {
            log.error("Drop loader: Missing npc_drop.dat!!!");
        }
        catch (IOException e) {
            log.error("Drop loader: IO error in drop Loading.");
        }
        finally{
            try {
                if (roChannel != null)
                    roChannel.close();

            }
            catch (IOException e) {
                log.error("Drop loader: IO error in drop Loading.");
            }
        }

        // lvl-based drop
        // stage 1: read global drop
        List<NpcLvlDrop> xmlNpcLvlDrops = new ArrayList<NpcLvlDrop>();

        DocumentBuilderFactory lvlfactory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        File file = new File("./data/static_data/global_drops.xml");
        try {
            doc = lvlfactory.newDocumentBuilder().parse(file);
        }
        catch(Exception e) {
            System.out.print("Error while parse global_drops.xml");
        }

        Node n = doc.getFirstChild();
        if (n != null)
            for(Node npcNode = n.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling())
                if(npcNode.getNodeName().equals("drop_level")) {
                    int lvlmin = Integer.parseInt(npcNode.getAttributes().getNamedItem("lvlmin").getNodeValue());
                    int lvlmax = Integer.parseInt(npcNode.getAttributes().getNamedItem("lvlmax").getNodeValue());
                    List<DropGroup> dropGroups = new ArrayList<DropGroup>();
                    String dropGroupName = "GLOBAL_LVL_DROP";
                    Race dropGroupRace = Race.PC_ALL;
                    Boolean dropGroupUseCategory = false;
                    List<Drop> xmlLvlDrops = new ArrayList<Drop>();
                    for (Node dropNode = npcNode.getFirstChild(); dropNode != null; dropNode = dropNode.getNextSibling()) {
                        if (dropNode.getNodeName().equals("globaldropitem")) {
                            xmlLvlDrops.add(Drop.loadxml(dropNode));
                        }
                    }
                    DropGroup dropGroupXML = new DropGroup(xmlLvlDrops, dropGroupRace, dropGroupUseCategory, dropGroupName);
                    dropGroups.add(dropGroupXML);

                    NpcLvlDrop npcDropXML = new NpcLvlDrop(dropGroups, lvlmin, lvlmax, 0);
                    xmlNpcLvlDrops.add(npcDropXML);
                }
        // stage 2: add global drop
        for (NpcTemplate npcTemplate : DataManager.NPC_DATA.getNpcData().valueCollection()) {
            int npclvl = npcTemplate.getLevel();
            for (NpcLvlDrop npcLvlDrop : xmlNpcLvlDrops) {
                if (npclvl >= npcLvlDrop.getMinLvl() && npclvl <= npcLvlDrop.getMaxLvl()) {
                    NpcDrop npcDrop = new NpcDrop(npcLvlDrop.getDropGroup(), npcTemplate.getTemplateId(), 0);
                    npcDrops.add(npcDrop);
                    NpcDrop tmpNpcDrop = npcTemplate.getNpcDrop();
                    if (tmpNpcDrop != null)
                        tmpNpcDrop.getDropGroup().addAll(npcLvlDrop.getDropGroup());
                }
            }
        }
        // end lvl-based drop

		NpcDropData dropData = new NpcDropData();
		log.info("Drop loader: Npc drops loading done.");
		dropData.setNpcDrop(npcDrops);
		return dropData;

	}

	public static void reload(){
		TIntObjectHashMap<NpcTemplate> npcData = DataManager.NPC_DATA.getNpcData();
		npcData.forEachValue(new TObjectProcedure<NpcTemplate>(){

			@Override
			public boolean execute(NpcTemplate npcTemplate) {
				npcTemplate.setNpcDrop(null);
				return false;
			}
		});
		load();
	}
}
