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
package com.aionemu.commons.objects.filter;

/**
 * This filter is used to combine a few ObjectFilters into one. Its acceptObject method returns true only if all
 * filters, that were passed through constructor return true
 *
 * @param <T>
 * @author Luno
 */
public class AndObjectFilter<T> implements ObjectFilter<T> {

	/**
	 * All filters that are used when running acceptObject() method
	 */
	private ObjectFilter<? super T>[] filters;

	/**
	 * Constructs new <tt>AndObjectFilter</tt> object, that uses given filters.
	 *
	 * @param filters
	 */
	public AndObjectFilter(ObjectFilter<? super T>... filters) {
		this.filters = filters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean acceptObject(T object) {
		for (ObjectFilter<? super T> filter : filters) {
			if (filter != null && !filter.acceptObject(object))
				return false;
		}
		return true;
	}
}
