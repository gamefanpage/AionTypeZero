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

package com.aionengine.loginserver.taskmanager.trigger.implementations;

import com.aionengine.loginserver.taskmanager.trigger.TaskFromDBTrigger;
import com.aionengine.loginserver.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * @author nrg
 */
public class FixedInTimeTrigger extends TaskFromDBTrigger {

    private static Logger log = LoggerFactory.getLogger(FixedInTimeTrigger.class);
    private final int DAY_IN_MSEC = 1 * 24 * 60 * 60 * 1000;
    private int hour, minute, second;

    @Override
    public boolean isValidTrigger() {
        if (params.length == 1) {
            try {
                String time[] = params[0].split(":");
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
                second = Integer.parseInt(time[2]);
                return true;
            } catch (NumberFormatException e) {
                log.warn("Could not parse the time for a FixedInTimeTrigger from DB", e);
            } catch (Exception e) {
                log.warn("A time for FixedInTimeTrigger is missing or invalid", e);
            }
        }
        log.warn("Not exact 1 parameter for FixedInTimeTrigger received, task is not registered");
        return false;
    }

    /**
     * Run a fixed in the time (HH:MM:SS) task
     */
    @Override
    public void initTrigger() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();

        if (delay < 0) {
            delay += DAY_IN_MSEC;
        }

        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, delay, DAY_IN_MSEC);
    }
}
