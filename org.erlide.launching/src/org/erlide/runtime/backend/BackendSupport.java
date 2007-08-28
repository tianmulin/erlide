/*******************************************************************************
 * Copyright (c) 2006 Vlad Dumitrescu and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Vlad Dumitrescu
 *******************************************************************************/
package org.erlide.runtime.backend;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.erlide.runtime.ErlangLaunchPlugin;

public class BackendSupport {

	private BackendSupport() {
	}

	private static volatile BackendType[] fTypes = null;

	public static BackendType[] getTypes() {
		if (fTypes != null) {
			return fTypes;
		}

		final IExtension[] extensions = Platform.getExtensionRegistry()
				.getExtensionPoint(ErlangLaunchPlugin.PLUGIN_ID, "backends")
				.getExtensions();

		final List<BackendType> types = new ArrayList<BackendType>(10);

		for (final IExtension extension : extensions) {
			final IConfigurationElement[] elements = extension
					.getConfigurationElements();
			for (final IConfigurationElement element : elements) {
				if ("backend".equals(element.getName())) {
					final String name = element.getAttribute("name");
					final String id = element.getAttribute("id");
					final String cls = element.getAttribute("class");
					final BackendType type = new BackendType(name, id, cls,
							element);

					types.add(type);
				}
			}
		}

		fTypes = types.toArray(new BackendType[types.size()]);
		return fTypes;
	}

	public static BackendType getType(String id) {
		final BackendType[] types = getTypes();
		for (final BackendType bt : types) {
			if (bt.getID().equals(id)) {
				return bt;
			}
		}
		return null;
	}

}