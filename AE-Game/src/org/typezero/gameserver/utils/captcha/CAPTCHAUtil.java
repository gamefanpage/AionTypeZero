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

package org.typezero.gameserver.utils.captcha;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * @author Cura
 */
public class CAPTCHAUtil {

	private final static int DEFAULT_WORD_LENGTH = 6;
	private final static String WORD = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

	private final static int IMAGE_WIDTH = 160;
	private final static int IMAGE_HEIGHT = 80;
	private final static int TEXT_SIZE = 25;
	private final static String FONT_FAMILY_NAME = "Verdana";

	/**
	 * create CAPTCHA
	 *
	 * @param word
	 * @return byte[]
	 */
	public static ByteBuffer createCAPTCHA(String word) {
		ByteBuffer byteBuffer = null;
		BufferedImage bImg = createImage(word);

		byteBuffer = DDSConverter.convertToDxt1NoTransparency(bImg);

		return byteBuffer;
	}

	/**
	 * CAPTCHA image create
	 *
	 * @param word
	 *          text word
	 * @return BufferedImage
	 */
	private static BufferedImage createImage(String word) {
		BufferedImage bImg = null;

		try {
			// image create
			bImg = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics2D g2 = bImg.createGraphics();

			// set backgroup color
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

			// set font family, color, size, antialiasing
			Font font = new Font(FONT_FAMILY_NAME, Font.BOLD, TEXT_SIZE);
			g2.setFont(font);
			g2.setColor(Color.WHITE);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// word drawing
			char[] chars = word.toCharArray();
			int x = 10;
			int y = IMAGE_HEIGHT / 2 + TEXT_SIZE / 2;

			for (int i = 0; i < chars.length; i++) {
				char ch = chars[i];
				g2.drawString(String.valueOf(ch), x + font.getSize() * i, y + (int) Math.pow(-1, i) * (TEXT_SIZE / 6));
			}

			// resource dispose
			g2.dispose();
		}
		catch (Exception e) {
			e.printStackTrace();
			bImg = null;
		}

		return bImg;
	}

	/**
	 * @return String random word
	 */
	public static String getRandomWord() {
		return randomWord(DEFAULT_WORD_LENGTH);
	}

	/**
	 * @return CAPTCHA word
	 */
	private static String randomWord(int wordLength) {
		StringBuffer word = new StringBuffer();

		for (int i = 0; i < wordLength; i++) {
			int index = Math.abs((int) (Math.random() * WORD.length()));
			char ch = WORD.charAt(index);
			word.append(ch);
		}

		return word.toString();
	}
}
