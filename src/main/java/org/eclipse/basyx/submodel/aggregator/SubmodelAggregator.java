package org.eclipse.basyx.submodel.aggregator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.AuthorizedSubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for aggregating local submodels based on the ISubmodelAPI
 * 
 * @author espen, wege
 *
 */
public class SubmodelAggregator implements ISubmodelAggregator {
	private static final Logger logger = LoggerFactory.getLogger(SubmodelAggregator.class);

	protected Map<String, ISubmodelAPI> smApiMap = new HashMap<>();

	/**
	 * Store Submodel API Provider. By default, uses the VAB Submodel Provider
	 */
	protected ISubmodelAPIFactory smApiFactory;

	public SubmodelAggregator() {
		smApiFactory = new VABSubmodelAPIFactory();
	}

	public SubmodelAggregator(ISubmodelAPIFactory smApiFactory) {
		this.smApiFactory = smApiFactory;
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		return smApiMap.values().stream().map(smApi -> {
			try {
				return smApi.getSubmodel();
			} catch (final NotAuthorized e) {
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public ISubmodel getSubmodel(IIdentifier identifier) throws ResourceNotFoundException {
		ISubmodelAPI api = getSubmodelAPIById(identifier);
		return api.getSubmodel();
	}

	private String getIdShort(IIdentifier identifier) {
		for (ISubmodelAPI api : smApiMap.values()) {
			ISubmodel submodel = api.getSubmodel();
			String idValue = submodel.getIdentification().getId();
			if (idValue.equals(identifier.getId())) {
				return submodel.getIdShort();
			}
		}
		throw new ResourceNotFoundException("The submodel with id '" + identifier.getId() + "' could not be found");
	}

	@Override
	public void createSubmodel(Submodel submodel) {
		updateSubmodel(submodel);
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		ISubmodelAPI submodelAPI = smApiFactory.create(submodel);
		createSubmodel(submodelAPI);
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		smApiMap.put(submodelAPI.getSubmodel().getIdShort(), submodelAPI);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(String idShort) throws ResourceNotFoundException {
		ISubmodelAPI api = smApiMap.get(idShort);
		if (api == null) {
			throw new ResourceNotFoundException("The submodel with idShort '" + idShort + "' could not be found");
		} else {
			return api.getSubmodel();
		}
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier identifier) {
		try {
			String idShort = getIdShort(identifier);
			smApiMap.remove(idShort);
		} catch (ResourceNotFoundException exception) {
		}
	}

	@Override
	public void deleteSubmodelByIdShort(String idShort) {
		smApiMap.remove(idShort);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(IIdentifier identifier) throws ResourceNotFoundException {
		String idShort = getIdShort(identifier);
		return smApiMap.get(idShort);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException {
		ISubmodelAPI api = smApiMap.get(idShort);
		if (api == null) {
			throw new ResourceNotFoundException("The submodel with idShort '" + idShort + "' could not be found");
		}
		return api;
	}
}
