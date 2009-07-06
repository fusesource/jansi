/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package org.fusesource.jansi.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.fusesource.jansi.AnsiConsole.SystemOutInstaller;

import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

/**
 * A Sun JVM specific System.out installer. Uses Sun reflection hacks to get
 * around the final static declaration on the System.out variable.
 * 
 * @author chirino
 */
class SunSystemOutInstaller extends SystemOutInstaller {
	private static final ReflectionFactory reflection = ReflectionFactory.getReflectionFactory();

	private FieldAccessor getOutFieldAccessor()
			throws NoSuchFieldException, IllegalAccessException {
		Field field = getOutField();
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		int modifiers = modifiersField.getInt(field);
		// blank out the final bit in the modifiers int
		modifiers &= ~Modifier.FINAL;
		modifiersField.setInt(field, modifiers);
		FieldAccessor fa = reflection.newFieldAccessor(field, false);
		return fa;
	}

	public void install() throws Exception {
		getOutFieldAccessor().set(null, new_out);
	}

	public void uninstall() throws Exception {
		getOutFieldAccessor().set(null, old_out);
	}
}