/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.esignsantewebservices.call;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/*
 * Classe EsignsanteConfigurationProperties
 * Configuration d'esignsante webservies( url - config.json)
 *  utilisée par esignsante-psc 
 * 
 */
@Getter
@Configuration

public class EsignsanteConfigurationProperties {

	@Value("${esignsante.webservices.signature.confId}")
	private long signatureConfId;

	@Value("${esignsante.webservices.signature.secret}")
	private String secret;

	@Value("${esignsante.webservices.proof.confId}")
	private long proofConfId;
	
	@Value("${esignsante.webservices.checksignature}")
	private long checkSignatureConfId;
	
	@Value("${esignsante.webservices.basepath}")
	private String basePath;
	
	@Value("${esignsante.webservices.appliantId}")
	private String appliantId;
	
	@Value("${esignsante.webservices.proofTag}")
	private String proofTag;

}
