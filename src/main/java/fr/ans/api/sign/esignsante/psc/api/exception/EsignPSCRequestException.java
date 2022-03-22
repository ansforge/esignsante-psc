/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.api.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

public class EsignPSCRequestException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	//transient pour Sonar. 'Error' est généré par OpneAPI (serializable). 
	//Cette exception est intercepté par un contrôleur Spring qui renverra une Http  en erreur
	//avec l'objet sérializé
	private transient fr.ans.api.sign.esignsante.psc.model.Error erreur;

	@Getter
	@Setter
	private HttpStatus statusARetourner = HttpStatus.INTERNAL_SERVER_ERROR;

	public EsignPSCRequestException(fr.ans.api.sign.esignsante.psc.model.Error erreur, HttpStatus statusARetourner) {
		super();
		this.erreur = erreur;
		this.statusARetourner = statusARetourner;
	}
}
