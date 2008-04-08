/*******************************************************************************
 * Copyright (c) 2004 Vlad Dumitrescu and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution.
 *
 * Contributors:
 *     Vlad Dumitrescu
 *******************************************************************************/
package org.erlide.core.erlang;

public interface IErlMessage extends IErlMember {

	String getMessage();

	String getData();

	enum MessageKind {
		INFO, WARNING, ERROR
	};

	MessageKind getMessageKind();

}