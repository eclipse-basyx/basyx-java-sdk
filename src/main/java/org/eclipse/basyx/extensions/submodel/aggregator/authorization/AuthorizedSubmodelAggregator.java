package org.eclipse.basyx.extensions.submodel.aggregator.authorization;

import java.util.Collection;

import org.eclipse.basyx.extensions.shared.authorization.internal.SecurityContextAuthorizer;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

/**
 * Implementation variant for the SubmodelAggregator that authorized each access
 *
 * @author espen
 *
 */
public class AuthorizedSubmodelAggregator implements ISubmodelAggregator {
	private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAggregatorScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAggregatorScopes.WRITE_SCOPE;

	private final SecurityContextAuthorizer authorizer = new SecurityContextAuthorizer();
	private ISubmodelAggregator decoratedSubmodelAggregator;

	public AuthorizedSubmodelAggregator(ISubmodelAggregator decoratedSubmodelAggregator) {
		this.decoratedSubmodelAggregator = decoratedSubmodelAggregator;
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAggregator.getSubmodelList();
	}

	@Override
	public ISubmodel getSubmodel(IIdentifier identifier) throws ResourceNotFoundException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAggregator.getSubmodel(identifier);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(String idShort) throws ResourceNotFoundException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAggregator.getSubmodelbyIdShort(idShort);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(IIdentifier identifier) throws ResourceNotFoundException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAggregator.getSubmodelAPIById(identifier);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAggregator.getSubmodelAPIByIdShort(idShort);
	}

	@Override
	public void createSubmodel(Submodel submodel) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		decoratedSubmodelAggregator.createSubmodel(submodel);
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		decoratedSubmodelAggregator.createSubmodel(submodelAPI);
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		decoratedSubmodelAggregator.updateSubmodel(submodel);
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier identifier) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		decoratedSubmodelAggregator.deleteSubmodelByIdentifier(identifier);
	}

	@Override
	public void deleteSubmodelByIdShort(String idShort) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		decoratedSubmodelAggregator.deleteSubmodelByIdShort(idShort);
	}
}
