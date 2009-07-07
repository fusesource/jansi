/**
 * Copyright (C) 2009, Progress Software Corporation and/or its 
 * subsidiaries or affiliates.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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