/*******************************************************************************
 * Copyright (c) 2009 Vlad Dumitrescu and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available
 * at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vlad Dumitrescu
 *******************************************************************************/
package erlang;

import java.util.Collection;
import java.util.Map;

import org.erlide.core.erlang.ErlangCore;
import org.erlide.jinterface.backend.Backend;
import org.erlide.jinterface.backend.BackendException;
import org.erlide.jinterface.rpc.RpcException;
import org.erlide.jinterface.util.TypeConverter;

import com.ericsson.otp.erlang.OtpErlangObject;

public class ErlideEdocExport {

	public static void files(final Collection<String> files,
			final Map<String, OtpErlangObject> options) throws RpcException,
			BackendException {
		final Backend b = ErlangCore.getBackendManager().getIdeBackend();
		final OtpErlangObject opts = TypeConverter.encodeMap(options);
		b.call(15000, "edoc", "files", "lsx", files, opts);
	}
}
