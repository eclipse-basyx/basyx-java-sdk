package org.eclipse.basyx.extensions.submodel.authorization;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import org.eclipse.basyx.extensions.shared.authorization.SecurityContextAuthorizer;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

/**
 * Implementation variant for the SubmodelAggregator that authorized each access
 *
 * @author espen
 *
 */
public class AuthorizedSubmodelAPI implements ISubmodelAPI {
	private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAPIScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAPIScopes.WRITE_SCOPE;

	private final SecurityContextAuthorizer authorizer = new SecurityContextAuthorizer();
	private ISubmodelAPI decoratedSubmodelAPI;

	public AuthorizedSubmodelAPI(ISubmodelAPI decoratedSubmodelAPI) {
		this.decoratedSubmodelAPI = decoratedSubmodelAPI;
	}

	@Override
	public ISubmodel getSubmodel() {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAPI.getSubmodel();
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		decoratedSubmodelAPI.addSubmodelElement(elem);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		decoratedSubmodelAPI.addSubmodelElement(idShortPath, elem);
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		decoratedSubmodelAPI.deleteSubmodelElement(idShortPath);
	}

	@Override
	public Collection<IOperation> getOperations() {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAPI.getOperations();
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAPI.getSubmodelElements();
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		decoratedSubmodelAPI.updateSubmodelElement(idShortPath, newValue);
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAPI.getSubmodelElementValue(idShortPath);
	}

	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAPI.invokeOperation(idShortPath, params);
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAPI.invokeAsync(idShortPath, params);
	}

	@Override
	public Object getOperationResult(String idShort, String requestId) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAPI.getOperationResult(idShort, requestId);
	}

	@Override
	public File getSubmodelElementFile(String idShortPath) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return decoratedSubmodelAPI.getSubmodelElementFile(idShortPath);
	}

	@Override
	public void uploadSubmodelElementFile(String idShortPath, InputStream fileStream) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		decoratedSubmodelAPI.uploadSubmodelElementFile(idShortPath, fileStream);
	}
}
