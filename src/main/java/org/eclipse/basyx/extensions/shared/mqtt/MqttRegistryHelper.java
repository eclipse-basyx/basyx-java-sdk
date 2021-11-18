package org.eclipse.basyx.extensions.shared.mqtt;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

public class MqttRegistryHelper {
	public static final String TOPIC_REGISTERAAS = "BaSyxRegistry_registeredAAS";
	public static final String TOPIC_UPDATEAAS = "BaSyxRegistry_updatedShell";
	public static final String TOPIC_REGISTERSUBMODEL = "BaSyxRegistry_registeredSubmodel";
	public static final String TOPIC_UPDATESUBMODEL = "BaSyxRegistry_updatedSubmodel";
	public static final String TOPIC_DELETEAAS = "BaSyxRegistry_deletedShell";
	public static final String TOPIC_DELETESUBMODEL = "BaSyxRegistry_deletedSubmodel";

	public static String concatShellSubmodelId(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) {
		return "(" + shellIdentifier.getId() + "," + submodelIdentifier.getId() + ")";
	}
}
