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
package com.aionemu.commons.utils;

import java.util.List;

/**
 * @author Balancer
 */
public class Rnd
{
  private static final MTRandom rnd = new MTRandom();

  public static float get()
  {
    return rnd.nextFloat();
  }

  public static int get(int n)
  {
    return (int)Math.floor(rnd.nextDouble() * n);
  }

  public static int get(int min, int max)
  {
    return min + (int)Math.floor(rnd.nextDouble() * (max - min + 1));
  }

  public static boolean chance(int chance)
  {
    return (chance >= 1) && ((chance > 99) || (nextInt(99) + 1 <= chance));
  }

  public static boolean chance(double chance)
  {
    return nextDouble() <= chance / 100.0D;
  }

  public static <E> E get(E[] list)
  {
    return list[get(list.length)];
  }

  public static int get(int[] list)
  {
    return list[get(list.length)];
  }

  public static <E> E get(List<E> list)
  {
    return list.get(get(list.size()));
  }

  public static int nextInt(int n)
  {
    return (int)Math.floor(rnd.nextDouble() * n);
  }

  public static int nextInt()
  {
    return rnd.nextInt();
  }

  public static double nextDouble()
  {
    return rnd.nextDouble();
  }

  public static double nextGaussian()
  {
    return rnd.nextGaussian();
  }

  public static boolean nextBoolean()
  {
    return rnd.nextBoolean();
  }
}
