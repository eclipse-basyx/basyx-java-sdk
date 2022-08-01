package org.eclipse.basyx.extensions.shared.authorization;

/**
 * Interface for a generic subject information used for authorization.
 * @param <T> The type to be provided.
 */
public interface ISubjectInformationProvider<T> {
  T get();
}
