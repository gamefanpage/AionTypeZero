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
package com.aionemu.commons.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;


/**
 * This class is designed to process classes and interfaces that have fields marked with {@link Property} annotation
 *
 * @author SoulKeeper
 */
public class ConfigurableProcessor {

	private static final Logger log = LoggerFactory.getLogger(ConfigurableProcessor.class);

	/**
	 * This method is an entry point to the parser logic.<br>
	 * Any object or class that have {@link Property} annotation in it or it's parent class/interface can be submitted
	 * here.<br>
	 * If object(new Something()) is submitted, object fields are parsed. (non-static)<br>
	 * If class is submitted(Sotmething.class), static fields are parsed.<br>
	 * <p/>
	 *
	 * @param object     Class or Object that has {@link Property} annotations.
	 * @param properties Properties that should be used while seraching for a {@link Property#key()}
	 */
	public static void process(Object object, Properties... properties) {
		Class<?> clazz;

		if (object instanceof Class) {
			clazz = (Class<?>) object;
			object = null;
		} else {
			clazz = object.getClass();
		}

		process(clazz, object, properties);
	}

	/**
	 * This method uses recurcieve calls to launch search for {@link Property} annotation on itself and
	 * parents\interfaces.
	 *
	 * @param clazz Class of object
	 * @param obj   Object if any, null if parsing class (static fields only)
	 * @param props Properties with keys\values
	 */
	private static void process(Class<?> clazz, Object obj, Properties[] props) {
		processFields(clazz, obj, props);

		// Interfaces can't have any object fields, only static
		// So there is no need to parse interfaces for instances of objects
		// Only classes (static fields) can be located in interfaces
		if (obj == null) {
			for (Class<?> itf : clazz.getInterfaces()) {
				process(itf, obj, props);
			}
		}

		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null && superClass != Object.class) {
			process(superClass, obj, props);
		}
	}

	/**
	 * This method runs throught the declared fields watching for the {@link Property} annotation. It also watches for the
	 * field modifiers like {@link java.lang.reflect.Modifier#STATIC} and {@link java.lang.reflect.Modifier#FINAL}
	 *
	 * @param clazz Class of object
	 * @param obj   Object if any, null if parsing class (static fields only)
	 * @param props Properties with keys\values
	 */
	private static void processFields(Class<?> clazz, Object obj, Properties[] props) {
		for (Field f : clazz.getDeclaredFields()) {
			// Static fields should not be modified when processing object
			if (Modifier.isStatic(f.getModifiers()) && obj != null) {
				continue;
			}

			// Not static field should not be processed when parsing class
			if (!Modifier.isStatic(f.getModifiers()) && obj == null) {
				continue;
			}

			if (f.isAnnotationPresent(Property.class)) {
				// Final fields should not be processed
				if (Modifier.isFinal(f.getModifiers())) {
					log.error("Attempt to proceed final field " + f.getName() + " of class " + clazz.getName());
					throw new RuntimeException();
				}
				processField(f, obj, props);
			}
		}
	}

	/**
	 * This method takes {@link Property} annotation and does sets value according to annotation property. For this reason
	 * {@link #getFieldValue(java.lang.reflect.Field, java.util.Properties[])} can be called, however if method sees that
	 * there is no need - field can remain with it's initial value.
	 * <p/>
	 * Also this method is capturing and logging all {@link Exception} that are thrown by underlying methods.
	 *
	 * @param f     field that is going to be processed
	 * @param obj   Object if any, null if parsing class (static fields only)
	 * @param props Properties with kyes\values
	 */
	private static void processField(Field f, Object obj, Properties[] props) {
		boolean oldAccessible = f.isAccessible();
		f.setAccessible(true);
		try {
			Property property = f.getAnnotation(Property.class);
			if (!Property.DEFAULT_VALUE.equals(property.defaultValue()) || isKeyPresent(property.key(), props)) {
				f.set(obj, getFieldValue(f, props));
			} else if (log.isDebugEnabled()) {
				log.debug("Field " + f.getName() + " of class " + f.getDeclaringClass().getName() + " wasn't modified");
			}
		} catch (Exception e) {
			log.error("Can't transform field " + f.getName() + " of class " + f.getDeclaringClass());
			throw new RuntimeException();
		}
		f.setAccessible(oldAccessible);
	}

	/**
	 * This method is responsible for receiving field value.<br>
	 * It tries to load property by key, if not found - it uses default value.<br>
	 * Transformation is done using {@link com.aionemu.commons.configuration.PropertyTransformerFactory}
	 *
	 * @param field field that has to be transformed
	 * @param props properties with key\values
	 * @return transformed object that will be used as field value
	 * @throws TransformationException if something goes wrong during transformation
	 */
	private static Object getFieldValue(Field field, Properties[] props) throws TransformationException {
		Property property = field.getAnnotation(Property.class);
		String defaultValue = property.defaultValue();
		String key = property.key();
		String value = null;

		if (key.isEmpty()) {
			log.warn("Property " + field.getName() + " of class " + field.getDeclaringClass().getName() + " has empty key");
		} else {
			value = findPropertyByKey(key, props);
		}

		if (value == null || value.trim().equals("")) {
			value = defaultValue;
			if (log.isDebugEnabled()) {
				log.debug("Using default value for field " + field.getName() + " of class "
						+ field.getDeclaringClass().getName());
			}
		}

		PropertyTransformer<?> pt = PropertyTransformerFactory.newTransformer(field.getType(),
				property.propertyTransformer());
		return pt.transform(value, field);
	}

	/**
	 * Finds value by key in properties
	 *
	 * @param key   value key
	 * @param props properties to loook for the key
	 * @return value if found, null otherwise
	 */
	private static String findPropertyByKey(String key, Properties[] props) {
		for (Properties p : props) {
			if (p.containsKey(key)) {
				return p.getProperty(key);
			}
		}

		return null;
	}

	/**
	 * Checks if key is present in the given properties
	 *
	 * @param key   key to check
	 * @param props prperties to look for key
	 * @return true if key present, false in other case
	 */
	private static boolean isKeyPresent(String key, Properties[] props) {
		return findPropertyByKey(key, props) != null;
	}
}
