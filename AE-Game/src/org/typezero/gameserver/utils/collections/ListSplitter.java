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

package org.typezero.gameserver.utils.collections;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author xTz
 */
public class ListSplitter<T> {

	private T[] objects;
	private Class<?> componentType;
	private int splitCount;
	private int curentIndex = 0;
	private int length = 0;

	@SuppressWarnings("unchecked")
	public ListSplitter(Collection<T> collection, int splitCount) {
		if (collection != null && collection.size() > 0) {
			this.splitCount = splitCount;
			length = collection.size();
			this.objects = collection.toArray((T[]) new Object[length]);
			componentType = objects.getClass().getComponentType();
		}
	}

	public List<T> getNext(int splitCount) {
		this.splitCount = splitCount;
		return getNext();
	}

	public List<T> getNext() {
		@SuppressWarnings("unchecked")
		T[] subArray = (T[]) Array.newInstance(componentType, Math.min(splitCount, length - curentIndex));
		if (subArray.length > 0) {
			System.arraycopy(objects, curentIndex, subArray, 0, subArray.length);
			curentIndex += subArray.length;
		}
		return Arrays.asList(subArray);
	}

	public int size() {
		return length;
	}


	public boolean isLast() {
		return curentIndex == length;
	}

}
