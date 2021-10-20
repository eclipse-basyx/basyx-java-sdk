/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.coder.json.metaprotocol;

/**
 * 
 * @author pschorn
 *
 */
public enum MessageType {
	
		Unspecified(0),
        Debug(1),
        Information(2),
        Warning(3),
        Error(4),
        Fatal(5),
        Exception(6),
		Unknown(-1);
        
        private int id;
        
        MessageType(int id) {
			this.id = id;
		}
        
        public int getId() {
        	return id;
        }
        
        public static MessageType getById(int id) {
            for(MessageType t : values()) {
                if(t.getId() == id) return t;
            }
            return Unknown;
        }
}
