/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.api.delegate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import fr.ans.api.sign.esignsante.psc.api.CaApiDelegate;
import fr.ans.api.sign.esignsante.psc.esignsantewebservices.call.EsignsanteCall;
import fr.ans.api.sign.esignsante.psc.utils.Helper;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class CaApiDelegateImpl. Implementation du EndPopint /ca
 */
@Service
@Slf4j
public class CaApiDelegateImpl extends AbstractApiDelegate implements CaApiDelegate {

	@Autowired
	EsignsanteCall esignWS;

	@Override
	public ResponseEntity<List<String>> getCa() {
		log.debug(" Réception d'une demande des CAs");

		if (!isAcceptHeaderPresent(getAcceptHeaders(), Helper.APPLICATION_JSON)) {
			throwExceptionRequestError("Header 'accept' non conforme: attendu application/json",
					HttpStatus.NOT_ACCEPTABLE);
		}

		List<String> results = null;
		try {
			results = esignWS.getCA();
		} catch (ResourceAccessException e) {
			throwExceptionRequestError(e, "Exception sur appel esignWS. Service inaccessible",
					HttpStatus.SERVICE_UNAVAILABLE);
		}
		log.debug(" Demande des CAs traitée avec succès");
		return new ResponseEntity<>(results, HttpStatus.OK);
	}
}
