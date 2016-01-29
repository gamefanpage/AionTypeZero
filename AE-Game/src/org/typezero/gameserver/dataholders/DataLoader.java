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

package org.typezero.gameserver.dataholders;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

/**
 * This class is responsible for loading data from static .txt files.<br>
 * It's used as base class of {@link NpcData} and {@link SpawnData}.<br>
 * <br>
 * <font color="red">NOTICE: </font> This class is used temporarily and later will be removed and npc and spawn data
 * will be loaded with xml loader.<br>
 * <br>
 * <font color="red"><b>Do not use this class for anything else than <tt>NpcData</tt> or <tt>SpawnData</tt></b></font>
 *
 * @author Luno
 */
abstract class DataLoader {

	/** The logger used for <tt>DataLoader</tt> and its subclasses */
	protected Logger log = LoggerFactory.getLogger(getClass().getName());

	/** Relative path to directory containing .txt files with static data */
	private static final String PATH = "./data/static_data/";

	/** File containing data to load ( may be file or directory ) */
	private File dataFile;

	/**
	 * Constructor that is supposed to be called from subclass.
	 *
	 * @param file
	 *          file or directory in the static data directory, containing data that will be loaded
	 */
	DataLoader(String file) {
		this.dataFile = new File(PATH + file);
	}

	/**
	 * This method is supposed to be called from subclass to initialize data loading process.<br>
	 * <br>
	 * This method is using file given in the constructor to load the data and there are two possibilities:
	 * <ul>
	 * <li>Given file is file is in deed the <b>file</b> then it's forwarded to {@link #loadFile(File)} method</li>
	 * <li>Given file is a <b>directory</b>, then this method is obtaining list of all visible .txt files in this
	 * directory and subdirectiores ( except hidden ones and those named "new" ) and call {@link #loadFile(File)} for each
	 * of these files.
	 * </ul>
	 */
	protected void loadData() {
		if (dataFile.isDirectory()) {
			Collection<?> files = FileUtils.listFiles(
				dataFile,
				FileFilterUtils.andFileFilter(FileFilterUtils.andFileFilter(
					FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("new")),
					FileFilterUtils.suffixFileFilter(".txt")), HiddenFileFilter.VISIBLE), HiddenFileFilter.VISIBLE);

			for (Object file1 : files) {
				File f = (File) file1;
				loadFile(f);
			}
		}
		else {
			loadFile(dataFile);
		}
	}

	/**
	 * This method is loading data from particular .txt file.
	 *
	 * @param file
	 *          a file which the data is loaded from.<br>
	 *          The method is loading the file row by row, omitting those started with "#" sign.<br>
	 *          Every read row is then forwarded to {@link #parse(String)} method, which should be overriden in subclcass.
	 */
	private void loadFile(File file) {
		LineIterator it = null;
		try {
			it = FileUtils.lineIterator(file);
			while (it.hasNext()) {
				String line = it.nextLine();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				parse(line);
			}
		}
		catch (IOException e) {
			log.error("Error while loading " + getClass().getSimpleName() + ", file: " + file.getPath(), e);
		}
		finally {
			LineIterator.closeQuietly(it);
		}
	}

	/**
	 * This method must be overriden in every subclass and is responsible for parsing given <tt>dataEntry</tt> String
	 * which represents one row from data file.
	 *
	 * @param dataEntry
	 *          A String containing data about a data entry, that is to be parsed by this method.
	 */
	protected abstract void parse(String dataEntry);

	/**
	 * Saves data to the file. Used only by {@link SpawnData}.
	 *
	 * @return true if the data was successfully saved, false - if some error occurred.
	 */
	public boolean saveData() {
		String desc = PATH + getSaveFile();

		log.info("Saving " + desc);

		FileWriter fr = null;
		try {
			fr = new FileWriter(desc);

			saveEntries(fr);

			fr.flush();

			return true;
		}
		catch (Exception e) {
			log.error("Error while saving " + desc, e);
			return false;
		}
		finally {
			if (fr != null) {
				try {
					fr.close();
				}
				catch (Exception e) {
					log.error("Error while closing save data file", e);
				}
			}
		}
	}

	/**
	 * Name of the file which is used to store data in.<br>
	 * This method must be overriden in sublass if we want to be able to store its data. It's used only in
	 * {@link SpawnData} and should not be used anywhere else.
	 *
	 * @return name of the file
	 */
	protected abstract String getSaveFile();

	/**
	 * This method must be overriden in subclass which we want to be able to save data. It's responsibility is basicly to
	 * put data into given FileWriter instance.
	 *
	 * @param fileWriter
	 * @throws Exception
	 */
	protected void saveEntries(FileWriter fileWriter) throws Exception {
		// TODO Auto-generated method stub

	}
}
