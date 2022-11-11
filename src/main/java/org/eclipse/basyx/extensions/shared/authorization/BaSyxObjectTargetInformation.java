package org.eclipse.basyx.extensions.shared.authorization;

import java.util.HashMap;

/**
 * Implementation of {@link ITargetInformation} that uses the aasId/smId/smElIdShortPath tuple.
 *
 * @author wege
 */
public class BaSyxObjectTargetInformation extends HashMap<String, String> implements ITargetInformation {
  public String getAasId() {
    return get("aasId");
  }

  public String getSmId() {
    return get("smId");
  }

  public String getSmElIdShortPath() {
    return get("smElIdShortPath");
  }

  public BaSyxObjectTargetInformation(final String aasId, final String smId, final String smElIdShortPath) {
    put("aasId", aasId);
    put("smId", smId);
    put("smElIdShortPath", smElIdShortPath);
  }
}
