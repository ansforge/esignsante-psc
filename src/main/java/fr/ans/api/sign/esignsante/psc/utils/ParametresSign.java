/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.utils;

import java.io.File;
import java.util.Date;

import fr.ans.api.sign.esignsante.psc.model.UserInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParametresSign {
	
	private UserInfo userinfo = null;
	
	private String pscReponse = "reponse intropesction non connue";
		
	private String requestID = "";
	
	private File fileToSign = null;
	
	private Date date = null;


	public ParametresSign() {
		super();
	}
		
	
}
