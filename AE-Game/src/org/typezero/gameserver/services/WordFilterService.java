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

package org.typezero.gameserver.services;

import org.typezero.gameserver.model.gameobjects.player.Player;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WordFilterService
{
    private FileInputStream wordFilterFile;

    private Scanner scanner;

    static List<String> banWordList = new ArrayList<String>();

    public WordFilterService()
    {
        try {
            wordFilterFile = new FileInputStream("./config/wordfilter.txt");
            scanner = new Scanner(wordFilterFile, "UTF-8");

            while (scanner.hasNextLine()){
                banWordList.add(scanner.nextLine());
            }
        }catch(IOException ioe){

        }
        finally{
            scanner.close();
        }
    }

    public static String replaceBanWord(Player player, String word)
    {
        for(String bwf : banWordList)
        {
            word = word.replaceAll(bwf, "----");
        }

        return word;
    }

    public static WordFilterService getInstance()
    {
        return SingletonHolder.wfs;
    }

    private static class SingletonHolder
    {
        public static WordFilterService wfs = new WordFilterService();
    }
}
