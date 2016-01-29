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

package ai.instance.beshmundirTemple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.services.NpcShoutsService;

/**
 * @author Antraxx
 */
@AIName("virhana")
public class VirhanaTheGreatAI2 extends AggressiveNpcAI2 {

    private AtomicBoolean isHome = new AtomicBoolean(true);
    private Future<?> taskRage;
    private Future<?> taskRageExec;
    private int count;
    protected List<Integer> percents = new ArrayList<Integer>();

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, new Integer[]{50});
    }

    private synchronized void checkPercentage(int hpPercentage) {
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 50:
                        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500065, getObjectId(), 0, 1000);
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }

    private void cancelTasks() {
        if ((taskRageExec != null) && !taskRageExec.isDone()) {
            taskRageExec.cancel(true);
        }
        if ((taskRage != null) && !taskRage.isDone()) {
            taskRage.cancel(true);
        }
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            NpcShoutsService.getInstance().sendMsg(getOwner(), 1500064, getObjectId(), 0, 1000);
            scheduleRage();
        }
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDied() {
        cancelTasks();
        super.handleDied();
        percents.clear();
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }

    @Override
    protected void handleBackHome() {
        cancelTasks();
        super.handleBackHome();
        isHome.set(true);
        addPercent();
    }

    private void scheduleRage() {
        if (isAlreadyDead() || isHome.equals(true)) {
            return;
        }
        AI2Actions.useSkill(this, 19121);
        taskRage = ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                startRage();
            }
        }, 70000);
    }

    private void startRage() {
        if (isAlreadyDead() || isHome.equals(true)) {
            return;
        }
        if (count < 12) {
            NpcShoutsService.getInstance().sendMsg(getOwner(), 1500066, getObjectId(), 0, 1000);
            AI2Actions.useSkill(this, 18897);
            count++;
            taskRageExec = ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    startRage();
                }
            }, 10000);
        } else { // restart after a douzen casts
            count = 0;
            scheduleRage();
        }
    }
}
