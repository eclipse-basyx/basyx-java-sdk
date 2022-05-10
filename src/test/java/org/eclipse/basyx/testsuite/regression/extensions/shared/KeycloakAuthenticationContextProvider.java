package org.eclipse.basyx.testsuite.regression.extensions.shared;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Can set various types of SecurityContexts for test purposes for testing
 * invalid, read or write access.
 *
 * @author wege
 */
public class KeycloakAuthenticationContextProvider {
	public KeycloakAuthenticationContextProvider() {}

	private final String ID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImp0aSI6ImQzNWRmMTRkLTA5ZjYtNDhmZi04YTkzLTdjNmYwMzM5MzE1OSIsImlhdCI6MTU0MTk3MTU4MywiZXhwIjoxNTQxOTc1MTgzfQ.QaQOarmV8xEUYV7yvWzX3cUE_4W1luMcWCwproqqUrg";

	public void setSecurityContextWithRoles(String... roles) {
		final SecurityContext context = SecurityContextHolder.createEmptyContext();
		final Authentication authentication = new JwtAuthenticationToken(Jwt.withTokenValue(ID_TOKEN)
				.header("roles", Arrays.asList(roles))
				.claim("realm_access", Collections.singletonMap("roles", Arrays.asList(roles)))
				.build()
		);
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	public void setSecurityContextWithoutRoles() {
		setSecurityContextWithRoles();
	}

	public void setEmptySecurityContext() {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		SecurityContextHolder.setContext(context);
	}

	public void clearContext() {
		SecurityContextHolder.clearContext();
	}
}
