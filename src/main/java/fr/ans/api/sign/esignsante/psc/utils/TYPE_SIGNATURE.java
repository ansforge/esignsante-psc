/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.utils;

public enum TYPE_SIGNATURE {
    XADES ("XADES"),
	
    PADES ("PADES");
    	
	private final String typeSignature;
	
	TYPE_SIGNATURE(final String typeSignature) {
	        this.typeSignature = typeSignature;
	    }
	
	public String getTypeSignature() {
        return typeSignature;
    }
}

