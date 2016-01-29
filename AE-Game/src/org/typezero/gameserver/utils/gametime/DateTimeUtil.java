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

package org.typezero.gameserver.utils.gametime;


import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.configs.main.GSConfig;

/**
 * @author Rolandas
 *
 */
public final class DateTimeUtil
{
	static Logger log = LoggerFactory.getLogger(DateTimeUtil.class);

	private static boolean canApplyZoneChange = false;

	public static void init()
	{
		try
		{
			if (!GSConfig.TIME_ZONE_ID.isEmpty()) {
				// just check the validity on start (if invalid zone specified in the switch, default id used)
				DateTimeZone.forID(System.getProperty("Duser.timezone"));
				DateTimeZone.forID(GSConfig.TIME_ZONE_ID);
				canApplyZoneChange = true;
			}
		}
		catch (Throwable e)
		{
			log.error("Invalid or not supported timezones specified!!!\n" +
					 "Use both -Duser.timezone=\"timezone_id\" switch from command line\n" +
					 "and add a valid value for GSConfig.TIME_ZONE_ID");
		}
	}

	// Get now date and time
	public static DateTime getDateTime()
	{
		DateTime dt = new DateTime();
		if (canApplyZoneChange)
		{
			return dt.withZoneRetainFields(DateTimeZone.forID(GSConfig.TIME_ZONE_ID));
		}
		return dt;
	}

	public static DateTime getDateTime(String isoDateTime)
	{
		DateTime dt = new DateTime(isoDateTime);
		if (canApplyZoneChange)
		{
			return dt.withZoneRetainFields(DateTimeZone.forID(GSConfig.TIME_ZONE_ID));
		}
		return dt;
	}

	public static DateTime getDateTime(GregorianCalendar calendar)
	{
		DateTime dt = new DateTime(calendar);
		if (canApplyZoneChange)
		{
			return dt.withZoneRetainFields(DateTimeZone.forID(GSConfig.TIME_ZONE_ID));
		}
		return dt;
	}

	public static DateTime getDateTime(long millisSinceSeventies)
	{
		DateTime dt = new DateTime(millisSinceSeventies);
		if (canApplyZoneChange)
		{
			return dt.withZoneRetainFields(DateTimeZone.forID(GSConfig.TIME_ZONE_ID));
		}
		return dt;
	}

	public static boolean canApplyZoneChange()
	{
		return canApplyZoneChange;
	}

}
