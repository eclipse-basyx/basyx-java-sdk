package org.eclipse.basyx.extensions.aas.api.authorization;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.extensions.shared.authorization.SecurityContextAuthorizer;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Implementation variant for the AASAPI that authorizes each access to the API
 *
 * @author espen
 */
public class AuthorizedAASAPI implements IAASAPI {
	public static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAPIScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAPIScopes.WRITE_SCOPE;

	private SecurityContextAuthorizer authorizer = new SecurityContextAuthorizer();
	private IAASAPI authorizedAPI;

	public AuthorizedAASAPI(IAASAPI authorizedAPI) {
		this.authorizedAPI = authorizedAPI;
	}

	@Override
	public IAssetAdministrationShell getAAS() {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return authorizedAPI.getAAS();
	}

	@Override
	public void addSubmodel(IReference submodel) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		authorizedAPI.addSubmodel(submodel);
	}

	@Override
	public void removeSubmodel(String idShort) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		authorizedAPI.removeSubmodel(idShort);
	}
}
