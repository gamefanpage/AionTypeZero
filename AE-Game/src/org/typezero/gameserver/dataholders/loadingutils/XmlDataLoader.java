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

package org.typezero.gameserver.dataholders.loadingutils;

import org.typezero.gameserver.dataholders.StaticData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileReader;

/**
 * This class is responsible for loading xml files. It uses JAXB to do the job.<br>
 * In addition, it uses @{link {@link XmlMerger} to create input file from all xml files.
 *
 * @author Luno
 */
public class XmlDataLoader {

	private static final Logger log = LoggerFactory.getLogger(XmlDataLoader.class);

	/** File containing xml schema declaration */
	private final static String XML_SCHEMA_FILE = "./data/static_data/static_data.xsd";

	private static final String CACHE_DIRECTORY = "./cache/";
	private static final String CACHE_XML_FILE = "./cache/static_data.xml";
	private static final String MAIN_XML_FILE = "./data/static_data/static_data.xml";

	public static final XmlDataLoader getInstance() {
		return SingletonHolder.instance;
	}

	private XmlDataLoader() {

	}

	/**
	 * Creates {@link StaticData} object based on xml files, starting from static_data.xml
	 *
	 * @return StaticData object, containing all game data defined in xml files
	 */
	public StaticData loadStaticData() {
		makeCacheDirectory();

		File cachedXml = new File(CACHE_XML_FILE);
		File cleanMainXml = new File(MAIN_XML_FILE);

		mergeXmlFiles(cachedXml, cleanMainXml);

		try {
			JAXBContext jc = JAXBContext.newInstance(StaticData.class);
			Unmarshaller un = jc.createUnmarshaller();
			un.setEventHandler(new XmlValidationHandler());
			un.setSchema(getSchema());
			return (StaticData) un.unmarshal(new FileReader(CACHE_XML_FILE));
		}
		/*
		catch (IllegalAnnotationsException e) {
			for (IllegalAnnotationException detail : e.getErrors())
				log.error(detail.getMessage());
			throw new Error("Error while loading static data", e);
		}
		catch (FileNotFoundException e) {
			log.error("Error while loading static data", e);
			throw new Error("Error while loading static data", e);
		}
		catch (JAXBException e) {
			log.error("Error while loading static data", e);
			throw new Error("Error while loading static data", e);
		}
		*/
		catch (Exception e) {
			log.error("Error while loading static data", e);
			return null;
		}
	}

	/**
	 * Creates and returns {@link Schema} object representing xml schema of xml files
	 *
	 * @return a Schema object.
	 */
	private Schema getSchema() {
		Schema schema = null;
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try {
			schema = sf.newSchema(new File(XML_SCHEMA_FILE));
		}
		catch (SAXException saxe) {
			log.error("Error while getting schema", saxe);
			throw new Error("Error while getting schema", saxe);
		}

		return schema;
	}

	/** Creates directory for cache files if it doesn't already exist */
	private void makeCacheDirectory() {
		File cacheDir = new File(CACHE_DIRECTORY);
		if (!cacheDir.exists())
			cacheDir.mkdir();
	}

	/**
	 * Merges xml files(if are newer than cache file) and puts output to cache file.
	 *
	 * @see XmlMerger
	 * @param cachedXml
	 * @param cleanMainXml
	 * @throws Error
	 *           is thrown if some problem occured.
	 */
	private void mergeXmlFiles(File cachedXml, File cleanMainXml) throws Error {
		XmlMerger merger = new XmlMerger(cleanMainXml, cachedXml);
		try {
			merger.process();
		}
		catch (Exception e) {
			log.error("Error while merging xml files", e);
			throw new Error("Error while merging xml files", e);
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final XmlDataLoader instance = new XmlDataLoader();
	}
}
