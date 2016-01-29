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

package org.typezero.gameserver.geoEngine.math;

import org.typezero.gameserver.configs.main.GeoDataConfig;

import javolution.context.ObjectFactory;
import javolution.lang.Reusable;

/**
 * @author MrPoke
 */
public class Array3f implements Reusable{

	@SuppressWarnings("rawtypes")
	private static final ObjectFactory FACTORY = new ObjectFactory() {

		public Object create() {
			return new Array3f();
		}
	};

	public float a = 0;
	public float b = 0;
	public float c = 0;

	@Override
	public void reset() {
	 a = 0;
	 b = 0;
	 c = 0;
	}

  /**
   * Returns a new, preallocated or {@link #recycle recycled} text builder
   * (on the stack when executing in a {@link javolution.context.StackContext
   * StackContext}).
   *
   * @return a new, preallocated or recycled text builder instance.
   */
  public static Array3f newInstance() {
  	if(GeoDataConfig.GEO_OBJECT_FACTORY_ENABLE)
  		return (Array3f) FACTORY.object();
  	else
  		return new Array3f();
  }

  /**
   * Recycles a text builder {@link #newInstance() instance} immediately
   * (on the stack when executing in a {@link javolution.context.StackContext
   * StackContext}).
   */
  @SuppressWarnings("unchecked")
	public static void recycle(Array3f instance) {
  	if(GeoDataConfig.GEO_OBJECT_FACTORY_ENABLE)
  		FACTORY.recycle(instance);
  	else
  		instance = null;
  }
}
