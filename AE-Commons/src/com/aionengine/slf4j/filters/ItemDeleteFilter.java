package com.aionengine.slf4j.filters;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author Antraxx
 */
public class ItemDeleteFilter extends Filter<ILoggingEvent> {

	@Override
	public FilterReply decide(ILoggingEvent loggingEvent) {
		Object message = loggingEvent.getMessage();
		if (((String) message).startsWith("[ITEMDELETE]")) {
			return FilterReply.ACCEPT;
		}
		return FilterReply.DENY;
	}

}
