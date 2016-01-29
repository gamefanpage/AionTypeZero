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

package org.typezero.gameserver.utils.xml;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author Rolandas
 */
public final class CompressUtil {

	public static String Decompress(byte[] bytes) throws Exception {
		Inflater decompressor = new Inflater();
		decompressor.setInput(bytes);

		// Create an expandable byte array to hold the decompressed data
		ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);

		byte[] buffer = new byte[1024];
		try {
			while (true) {
				int count = decompressor.inflate(buffer);
				if (count > 0) {
					bos.write(buffer, 0, count);
				}
				else if (count == 0 && decompressor.finished()) {
					break;
				}
				else {
					throw new RuntimeException("Bad zip data, size: " + bytes.length);
				}
			}
		}
		finally {
			decompressor.end();
		}

		bos.close();
		return bos.toString("UTF-16LE");
	}

	public static byte[] Compress(String text) throws Exception {
		Deflater compressor = new Deflater();
		byte[] bytes = text.getBytes("UTF-16LE");
		compressor.setInput(bytes);

		// Create an expandable byte array to hold the compressed data
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		compressor.finish();

		byte[] buffer = new byte[1024];
		try {
			while(!compressor.finished())
      {
          int count = compressor.deflate(buffer);
          bos.write(buffer, 0, count);
      }
		}
		finally {
			compressor.finish();
		}

		bos.close();
		return bos.toByteArray();
	}
}
