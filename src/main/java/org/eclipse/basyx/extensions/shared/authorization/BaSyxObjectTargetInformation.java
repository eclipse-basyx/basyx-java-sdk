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
package org.eclipse.basyx.extensions.shared.authorization;

/**
 * Specialization of {@link TargetInformation} that uses the aasId/smId/smElIdShortPath tuple.
 *
 * @author wege
 */
public class BaSyxObjectTargetInformation extends TargetInformation {
  public static final String AAS_ID_KEY = "aasId";
  public static final String SM_ID_KEY = "smId";
  public static final String SM_EL_ID_SHORT_PATH_KEY = "smElIdShortPath";

  public String getAasId() {
    return get(AAS_ID_KEY);
  }

  public String getSmId() {
    return get(SM_ID_KEY);
  }

  public String getSmElIdShortPath() {
    return get(SM_EL_ID_SHORT_PATH_KEY);
  }

  public BaSyxObjectTargetInformation(final String aasId, final String smId, final String smElIdShortPath) {
    put(AAS_ID_KEY, aasId);
    put(SM_ID_KEY, smId);
    put(SM_EL_ID_SHORT_PATH_KEY, smElIdShortPath);
  }
}
