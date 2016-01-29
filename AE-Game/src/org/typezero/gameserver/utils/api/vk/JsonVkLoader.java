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

package org.typezero.gameserver.utils.api.vk;

import org.typezero.gameserver.configs.main.JsonConfig;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.utils.api.ReadUrl;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javolution.util.FastList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Dr2co
 */


public class JsonVkLoader {

    public static class VkItem {

        private int id;
        private long date;
        private String text;
        private int comments;
        private int like;
        private String groupName;
        private String name;

        public VkItem(int id, long date, String text, int comments, int like, String groupName, String name) {
            this.id = id;
            this.date = date;
            this.text = text;
            this.comments = comments;
            this.like = like;
            this.groupName = groupName;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public long getDate() {
            return date;
        }

        public String getText() {
            return text;
        }

        public int getCommnets() {
            return comments;
        }

        public int getLikes() {
            return like;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getName() {
            return name;
        }

        public String getTimeFormat() {
            Date dt = new Date(getDate() * 1000L);
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            return format.format(dt);
        }
    }
    private static FastList<VkItem> itemList;

    public static FastList<VkItem> load(int groupId, int count) {
        itemList = FastList.newInstance();
        try {
            JSONObject js = new JSONObject(ReadUrl.read("http://api.vk.com/method/wall.get?owner_id=-" + groupId + "&filter=owner&count=" + count + ""));
            JSONArray response = js.getJSONArray("response");
            JSONObject js2 = new JSONObject(ReadUrl.read("http://api.vk.com/method/groups.getById?gid=" + groupId));
            JSONArray response2 = js2.getJSONArray("response");
            for (int i = 1; i < response.length(); i++) {
                JSONObject jo = response.getJSONObject(i);
                String text = "";
                if (jo.getString("text").length() > 0) {
                    try {
                        text = jo.getString("text").substring(0, JsonConfig.VK_TEXT_LENGTH) + "...";
                    } catch (Exception ex) {
                        text = jo.getString("text");
                    }
                }

                VkItem item = new VkItem(jo.getInt("id"), jo.getLong("date"), text, jo.getJSONObject("comments").getInt("count"), jo.getJSONObject("likes").getInt("count"), response2.getJSONObject(0).getString("screen_name"), response2.getJSONObject(0).getString("name"));
                itemList.add(item);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return itemList;
    }
}
