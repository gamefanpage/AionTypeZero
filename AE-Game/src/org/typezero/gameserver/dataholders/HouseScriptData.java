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
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.typezero.gameserver.model.templates.housing.LBox;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "lboxes")
public class HouseScriptData {

	private static final Logger log = LoggerFactory.getLogger(HouseScriptData.class);
	private static Marshaller marshaller;

	static {
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		JAXBContext jc = null;

		try {
			schema = sf.newSchema(new File("./data/static_data/housing/scripts.xsd"));
			jc = JAXBContext.newInstance(HouseScriptData.class);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-16");
		}
		catch (Exception e) {
			log.error("Could not instantiate HouseScriptData : \n" + e);
		}
	}

	@XmlElement(name = "lbox", required = true)
	protected List<LBox> scriptData;

	@XmlTransient
	private final Map<Integer, LBox> defaultTemplates = new HashMap<Integer, LBox>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (LBox template : scriptData) {
			defaultTemplates.put(template.getId(), template);
		}
		scriptData.clear();
		scriptData = null;
	}

	public static class XmlFormatter {

		private static final Logger log = LoggerFactory.getLogger(XmlFormatter.class);
		private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		private static DocumentBuilder db;

		static {
			try {
				db = dbf.newDocumentBuilder();
			}
			catch (ParserConfigurationException e) {
				log.error("Could not instantiate XmlFormatter : \n" + e);
			}
		}

		public static String format(String unformattedXml) {
			try {
				final Document document = parseXmlFile(unformattedXml);

				OutputFormat format = new OutputFormat(document);
				format.setIndenting(true);
				format.setIndent(2);
				format.setEncoding("UTF-16");
				Writer out = new StringWriter();
				XMLSerializer serializer = new XMLSerializer(out, format);
				serializer.serialize(document);
				return out.toString();
			}
			catch (IOException e) {
			}
			return null;
		}

		private static Document parseXmlFile(String in) {
			try {
				InputSource is = new InputSource(new StringReader(in));
				return db.parse(is);
			}
			catch (SAXException e) {
				throw new RuntimeException(e);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public String createScript(int scriptId, int position, int iconId) {
		LBox template = defaultTemplates.get(scriptId);
		LBox result = (LBox) template.clone();
		result.setId(position);
		result.setIcon(iconId);

		HouseScriptData fragment = new HouseScriptData();
		fragment.scriptData = new ArrayList<LBox>();
		fragment.scriptData.add(result);

		Writer writer = new StringWriter();
		try {
			marshaller.marshal(fragment, writer);
		}
		catch (JAXBException e) {
		}
		return XmlFormatter.format(writer.toString());
	}

	public int size() {
		return defaultTemplates.size();
	}

}
