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

package org.typezero.gameserver.model.templates.serial_killer;

import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.skillengine.change.Func;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Dtem
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RankPenaltyAttr")
public class RankPenaltyAttr {

    @XmlAttribute(required = true)
    protected StatEnum stat;
    @XmlAttribute(required = true)
    protected Func func;
    @XmlAttribute(required = true)
    protected int value;

    /**
     * Gets the value of the stat property.
     *
     * @return
     *     possible object is
     *     {@link StatEnum }
     *
     */
    public StatEnum getStat() {
        return stat;
    }

    /**
     * Sets the value of the stat property.
     *
     * @param value
     *     allowed object is
     *     {@link StatEnum }
     *
     */
    public void setStat(StatEnum value) {
        this.stat = value;
    }

    /**
     * Gets the value of the func property.
     *
     * @return
     *     possible object is
     *     {@link Func }
     *
     */
    public Func getFunc() {
        return func;
    }

    /**
     * Sets the value of the func property.
     *
     * @param value
     *     allowed object is
     *     {@link Func }
     *
     */
    public void setFunc(Func value) {
        this.func = value;
    }

    /**
     * Gets the value of the value property.
     *
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     */
    public void setValue(int value) {
        this.value = value;
    }

}
