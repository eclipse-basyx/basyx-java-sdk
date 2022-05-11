package org.eclipse.basyx.extensions.shared.authorization;

import java.util.List;
import org.eclipse.basyx.vab.coder.json.metaprotocol.Message;
import org.eclipse.basyx.vab.exception.provider.ProviderException;

public class NotAuthorized extends ProviderException {
  /**
   * Version information for serialized instances
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor
   */
  public NotAuthorized(String msg) {
    super(msg);
  }

  public NotAuthorized(Exception e) {
    super(e);
  }

  public NotAuthorized(List<Message> msgs) {
    super(msgs);
  }

  public NotAuthorized() {
    this("not authorized");
  }
}
