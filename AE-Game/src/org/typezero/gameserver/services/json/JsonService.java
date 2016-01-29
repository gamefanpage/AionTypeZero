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

package org.typezero.gameserver.services.json;

import org.typezero.gameserver.cache.HTMLCache;
import org.typezero.gameserver.configs.main.GSConfig;
import org.typezero.gameserver.configs.main.JsonConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.HTMLService;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.utils.api.vk.JsonVkLoader;
import org.typezero.gameserver.utils.api.vk.JsonVkLoader.VkItem;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dr2co
 */

public class JsonService {

    private static final Logger log = LoggerFactory.getLogger(JsonService.class);
    private FastList<VkItem> vkItemList = FastList.newInstance();

    public void load() {
        if (!JsonConfig.ENABLE_JSON) {
            log.info("JSON Service disable...");
            return;
        }

        if (JsonConfig.ENABLE_VK_JSON) {
            vkItemList = JsonVkLoader.load(JsonConfig.VK_GROUP_ID, JsonConfig.VK_GROUP_ITEMS_COUNT);
            log.info("JSON Service loaded " + vkItemList.size() + " vk items");
        }
    }

    public void onPlayerLogin(Player player) {
        if (!JsonConfig.ENABLE_JSON) {
            return;
        }
        String context = HTMLCache.getInstance().getHTML("apinews.xhtml");
        context = context.replace("%title%", MuiService.getInstance().getMessage("JSON_TITLE", GSConfig.SERVER_NAME));

        StringBuilder sb = new StringBuilder();
        for (FastList.Node<VkItem> n = vkItemList.head(), end = vkItemList.tail(); (n = n.getNext()) != end;) {
            String title;
            if (n.getValue().getText().length() == 0) {
                title = n.getValue().getName();
            } else {
                try {
                    title = n.getValue().getText().substring(0, 90) + "...";
                } catch (Exception ex) {
                    title = n.getValue().getText();
                }
            }
            sb.append("<a href='http://vk.com/").append(n.getValue().getGroupName()).append("?w=wall-").append(JsonConfig.VK_GROUP_ID).append("_").append(n.getValue().getId()).append("'><font color='45688E'>").append(MuiService.getInstance().convertFromUTF8(title)).append("</font></a><br>");
            sb.append(MuiService.getInstance().convertFromUTF8(n.getValue().getText())).append("<br>");
            sb.append(MuiService.getInstance().getMessage("VK_COMMENTS_LIKE", n.getValue().getTimeFormat(), n.getValue().getCommnets(), n.getValue().getLikes())).append("<br><br><br><br>");
        }

        context = context.replace("%text%", sb);

        HTMLService.showHTML(player, context);
    }

    private static class SingletonHolder {

        protected static final JsonService instance = new JsonService();
    }

    public static final JsonService getInstance() {
        return SingletonHolder.instance;
    }
}
