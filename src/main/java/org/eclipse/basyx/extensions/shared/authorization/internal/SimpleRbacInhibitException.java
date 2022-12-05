/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.shared.authorization.internal;

import java.util.Arrays;
import java.util.List;

/**
 * Specialization of {@link InhibitException} for RBAC access control scheme.
 *
 * @author wege
 */
public class SimpleRbacInhibitException extends InhibitException {
	private final List<String> roles;
	private final String action;
	private final TargetInformation targetInformation;

	public SimpleRbacInhibitException(final List<String> roles, final String action, final TargetInformation targetInformation) {
		this(createMessage(action, targetInformation, roles), roles, action, targetInformation);
	}

	private SimpleRbacInhibitException(final String message, final List<String> roles, final String action, final TargetInformation targetInformation) {
		super(message);

		this.roles = roles;
		this.action = action;
		this.targetInformation = targetInformation;
	}

	private static String createMessage(final String action, final TargetInformation targetInformation, final List<String> roles) {
		final String targetInformationString = targetInformation.toString();
		final String rolesString = Arrays.toString(roles.toArray());
		return String.format("no rule matching action=%s, targetInfo=%s role=(any of %s)", action, targetInformationString, rolesString);
	}

	@Override
	public InhibitException reduceSmIdToSmIdShortPath(final String smIdShortPath) {
		final TargetInformation messageTargetInformation = reduceSmIdToSmIdShortPath_convertTargetInformation(smIdShortPath);
		return new SimpleRbacInhibitException(createMessage(action, messageTargetInformation, roles), roles, action, targetInformation);
	}

	private TargetInformation reduceSmIdToSmIdShortPath_convertTargetInformation(final String smIdShortPath) {
		if (targetInformation instanceof BaSyxObjectTargetInformation) {
			return new BaSyxObjectTargetInformation(((BaSyxObjectTargetInformation) targetInformation).getAasId(), ((BaSyxObjectTargetInformation) targetInformation).getSmId(), String.format("(id of %s)", smIdShortPath));
		}

		return targetInformation;
	}
}
