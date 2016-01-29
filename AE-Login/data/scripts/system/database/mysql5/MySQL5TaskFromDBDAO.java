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

package mysql5;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionengine.loginserver.dao.TaskFromDBDAO;
import com.aionengine.loginserver.taskmanager.handler.TaskFromDBHandler;
import com.aionengine.loginserver.taskmanager.handler.TaskFromDBHandlerHolder;
import com.aionengine.loginserver.taskmanager.trigger.TaskFromDBTrigger;
import com.aionengine.loginserver.taskmanager.trigger.TaskFromDBTriggerHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Divinity, nrg
 */
public class MySQL5TaskFromDBDAO extends TaskFromDBDAO {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(MySQL5TaskFromDBDAO.class);
    private static final String SELECT_ALL_QUERY = "SELECT * FROM tasks ORDER BY id";

    @Override
    public ArrayList<TaskFromDBTrigger> getAllTasks() {
        final ArrayList<TaskFromDBTrigger> result = new ArrayList<TaskFromDBTrigger>();

        Connection con = null;

        PreparedStatement stmt = null;
        try {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement(SELECT_ALL_QUERY);

            ResultSet rset = stmt.executeQuery();

            while (rset.next()) {
                try {
                    TaskFromDBTrigger trigger = TaskFromDBTriggerHolder.valueOf(rset.getString("trigger_type")).getTriggerClass().newInstance();
                    TaskFromDBHandler handler = TaskFromDBHandlerHolder.valueOf(rset.getString("task_type")).getTaskClass().newInstance();

                    handler.setTaskId(rset.getInt("id"));

                    String execParamsResult = rset.getString("exec_param");
                    if (execParamsResult != null) {
                        handler.setParams(rset.getString("exec_param").split(" "));
                    }

                    trigger.setHandlerToTrigger(handler);

                    String triggerParamsResult = rset.getString("trigger_param");
                    if (triggerParamsResult != null) {
                        trigger.setParams(rset.getString("trigger_param").split(" "));
                    }

                    result.add(trigger);

                } catch (InstantiationException ex) {
                    log.error(ex.getMessage(), ex);
                } catch (IllegalAccessException ex) {
                    log.error(ex.getMessage(), ex);
                }
            }

            rset.close();
            stmt.close();
        } catch (SQLException e) {
            log.error("Loading tasks failed: ", e);
        } finally {
            DatabaseFactory.close(stmt, con);
        }

        return result;
    }

    @Override
    public boolean supports(String s, int i, int i1) {
        return MySQL5DAOUtils.supports(s, i, i1);
    }
}
