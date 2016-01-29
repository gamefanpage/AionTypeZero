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
package com.aionemu.commons.utils.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * This class allows us to read ResourceBundles with custom encodings, so we don't have write \\uxxxx symbols and use
 * utilities like native2ascii to convert files.
 * <p/>
 * <br>
 * Usage: For instance we want to load resource bundle "test" from current deirectory and use english locale. If locale
 * not found, we will use default file (and ignore default locale).
 * <p/>
 * <pre>
 * URLClassLoader loader = new URLClassLoader(new URL[] { new File(&quot;.&quot;).toURI().toURL() });
 *
 * ResourceBundle rb = ResourceBundle.getBundle(&quot;test&quot;, Locale.ENGLISH, loader, new ResourceBundleControl(&quot;UTF-8&quot;));
 *
 * // English locale not found, use default
 * if (!rb.getLocale().equals(Locale.ENGLISH)) {
 * 	rb = ResourceBundle.getBundle(&quot;test&quot;, Locale.ROOT, loader, new ResourceBundleControl(&quot;UTF-8&quot;));
 * }
 *
 * System.out.println(rb.getString(&quot;test&quot;));
 * </pre>
 *
 * @author SoulKeeper
 */
public class ResourceBundleControl extends ResourceBundle.Control {

	/**
	 * Encoding which will be used to read resource bundle, by defaults it's 8859_1
	 */
	private String encoding = "UTF-8";

	/**
	 * Just empty default constructor
	 */
	public ResourceBundleControl() {
	}

	/**
	 * This constructor allows to set encoding that will be used while reading resource bundle
	 *
	 * @param encoding encoding to use
	 */
	public ResourceBundleControl(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * This code is just copy-paste with usage {@link java.io.Reader} instead of {@link java.io.InputStream} to read
	 * properties.<br>
	 * <br> {@inheritDoc}
	 */
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		String bundleName = toBundleName(baseName, locale);
		ResourceBundle bundle = null;
		if (format.equals("java.class")) {
			try {
				@SuppressWarnings({"unchecked"})
				Class<? extends ResourceBundle> bundleClass = (Class<? extends ResourceBundle>) loader.loadClass(bundleName);

				// If the class isn't a ResourceBundle subclass, throw a
				// ClassCastException.
				if (ResourceBundle.class.isAssignableFrom(bundleClass)) {
					bundle = bundleClass.newInstance();
				} else {
					throw new ClassCastException(bundleClass.getName() + " cannot be cast to ResourceBundle");
				}
			} catch (ClassNotFoundException ignored) {
			}
		} else if (format.equals("java.properties")) {
			final String resourceName = toResourceName(bundleName, "properties");
			final ClassLoader classLoader = loader;
			final boolean reloadFlag = reload;
			InputStreamReader isr = null;
			InputStream stream;
			try {
				stream = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {

					@Override
					public InputStream run() throws IOException {
						InputStream is = null;
						if (reloadFlag) {
							URL url = classLoader.getResource(resourceName);
							if (url != null) {
								URLConnection connection = url.openConnection();
								if (connection != null) {
									// Disable caches to get fresh data for
									// reloading.
									connection.setUseCaches(false);
									is = connection.getInputStream();
								}
							}
						} else {
							is = classLoader.getResourceAsStream(resourceName);
						}
						return is;
					}
				});
				if (stream != null) {
					isr = new InputStreamReader(stream, encoding);
				}
			} catch (PrivilegedActionException e) {
				throw (IOException) e.getException();
			}
			if (isr != null) {
				try {
					bundle = new PropertyResourceBundle(isr);
				} finally {
					isr.close();
				}
			}
		} else {
			throw new IllegalArgumentException("unknown format: " + format);
		}
		return bundle;
	}

	/**
	 * Returns encoding that will be used to read .properties resource bundles
	 *
	 * @return encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Sets the encoding that will be used to read properties resource bundles
	 *
	 * @param encoding encoding that will be used for properties
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
