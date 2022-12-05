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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Authentication type that indicates that execution was caused by internal code
 * and therefore should bypass access control to get the required information or
 * do the required changes.
 *
 * @author wege
 */
public class ElevatedCodeAuthentication extends AbstractAuthenticationToken {
	private static final ThreadLocal<Set<ElevatedCodeAuthenticationAreaHandler>> elevatedCodeAuthenticationAreaHandlers = ThreadLocal.withInitial(HashSet::new);
	private static final ThreadLocal<SecurityContext> previousSecurityContext = new ThreadLocal<>();

	public ElevatedCodeAuthentication() {
		super(Collections.emptyList());
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	public static boolean isCodeAuthentication() {
		return AuthenticationContextProvider.getAuthentication().map(ElevatedCodeAuthentication.class::isInstance).orElse(false);
	}

	public static class ElevatedCodeAuthenticationAreaHandler implements AutoCloseable {
		@Override
		public void close() {
			leaveElevatedCodeAuthenticationArea(this);
		}
	}

	private static void leaveElevatedCodeAuthenticationArea(final ElevatedCodeAuthenticationAreaHandler areaHandler) {
		final var areaHandlers = elevatedCodeAuthenticationAreaHandlers.get();
		if (!areaHandlers.contains(areaHandler)) {
			return;
		}

		areaHandlers.remove(areaHandler);
		if (areaHandlers.isEmpty()) {
			unsetElevatedCodeAuthentication();
		}
	}

	public static ElevatedCodeAuthenticationAreaHandler enterElevatedCodeAuthenticationArea() {
		final ElevatedCodeAuthenticationAreaHandler handler = new ElevatedCodeAuthenticationAreaHandler();
		elevatedCodeAuthenticationAreaHandlers.get().add(handler);
		setElevatedCodeAuthentication();
		return handler;
	}

	private static void setElevatedCodeAuthentication() {
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() instanceof ElevatedCodeAuthentication) {
			return;
		}

		previousSecurityContext.set(SecurityContextHolder.getContext());

		final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new ElevatedCodeAuthentication());
		SecurityContextHolder.setContext(securityContext);
	}

	private static void unsetElevatedCodeAuthentication() {
		SecurityContextHolder.clearContext();
		if (previousSecurityContext.get() != null) {
			SecurityContextHolder.setContext(previousSecurityContext.get());
			previousSecurityContext.remove();
		}
	}
}
