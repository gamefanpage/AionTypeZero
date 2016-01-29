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

package org.typezero.gameserver.model.gameobjects;

import org.typezero.gameserver.model.stats.calc.StatOwner;
import org.typezero.gameserver.model.stats.calc.functions.IStatFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatRateFunction;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Cain
 * Date: 15.10.2014
 */
public class ItemEffect implements StatOwner
{
    private ItemTemplate itemTemplate;
    private List<IStatFunction> modifiers;

    public ItemEffect(Item item)
    {
        itemTemplate = item.getItemTemplate();
    }

    public List<IStatFunction> getModifiers()
    {
        if (modifiers == null)
            Initialization();
        return modifiers;
    }

    public int getItemEffectId()
    {
        if (itemTemplate.isNewPlayerBuffItem())
            return 2;
        if (itemTemplate.isOldPlayerBuffItem())
            return 3;
        if (itemTemplate.isEventBuffItem())
            return 10;
        return 0;
    }

    private void Initialization()
    {
        modifiers = new ArrayList();
        switch (getItemEffectId()) {
            case 2:
            case 3:
                this.modifiers.add(new StatRateFunction(StatEnum.BOOST_DROP_RATE, 10, true));
                break;
            case 10:
                this.modifiers.add(new StatRateFunction(StatEnum.BOOST_DROP_RATE, 10, true));
                this.modifiers.add(new StatRateFunction(StatEnum.SPEED, 5, true));
        }
    }
}
