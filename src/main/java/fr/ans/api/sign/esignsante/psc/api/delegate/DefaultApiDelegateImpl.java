/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.api.delegate;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fr.ans.api.sign.esignsante.psc.api.DefaultApiDelegate;
import fr.ans.api.sign.esignsante.psc.esignsantewebservices.call.EsignsanteCall;
import fr.ans.api.sign.esignsante.psc.model.Operation;
import fr.ans.api.sign.esignsante.psc.utils.Helper;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DefaultApiDelegateImpl. Implementation du EndPopint racine
 */

@Service
@Slf4j
public class DefaultApiDelegateImpl extends AbstractApiDelegate implements DefaultApiDelegate {

	final String ACCEPT_HEADER_JSON = "accept:"+Helper.APPLICATION_JSON;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	EsignsanteCall esignWS;

	/**
	 * The log.
	 */

	/**
	 * Gets the operations.
	 *
	 * @return the operations
	 */
	@Override
	public ResponseEntity<Set<Operation>> getOperations() {

		log.debug("Réception d'une demande des opérations disponibles");

		// vérification du Headers accept
		if (!isAcceptHeaderPresent(getAcceptHeaders(), Helper.APPLICATION_JSON)) {
			throwExceptionRequestError("Header 'accept' non conforme: attendu application/json",
					HttpStatus.NOT_ACCEPTABLE);
		}

		final Set<Operation> setOperations = new HashSet<Operation>();
		setOperations.add(setOperation("/", "Liste des opérations disponibles",
				"", ACCEPT_HEADER_JSON));
		

		setOperations.add(setOperation("/ca",
				"Opération qui permet au client de prendre connaissance des Autorités de Certification de confiance utilisées par la plateforme.",
				"", 
				ACCEPT_HEADER_JSON));

		setOperations.add(setOperation("/asksignature/xades",
				"Opération qui permet au client de demander de signer un document au format XADES Baseline-B.",
				"[\"file: docXMLToSign\", \"userToken:userToken\"]",
				"[\"accept:application/json\", \"accept:application/json\", \"access_token: PSCVAlidAccessToken\"]"));

		setOperations.add(setOperation("/asksignature/pades",
				"Opération qui permet au client de demander de signer un document au format PADES Baseline-B.", 
				"[\"file: docXMLToSign\", \"userToken:userToken\"]",
				"[\"accept:application/json\", \"accept:application/json\", \"access_token: PSCVAlidAccessToken\"]"));

		setOperations.add(setOperation("/checksignature/xades",
				"Opération qui permet de vérifier un document signé au format XADES Baseline-B.", 
				"file:sigendXadesDoc", ACCEPT_HEADER_JSON));

		setOperations.add(setOperation("/checksignature/xades",
				"Opération qui permet de vérifier un document signé au format PADES Baseline-B.", 
				"file:sigendPadesDoc",ACCEPT_HEADER_JSON ));

		return new ResponseEntity<Set<Operation>>(setOperations, HttpStatus.OK);
	}

	private Operation setOperation(String path, String description, String payload, String requiredHeaders) {
		Operation op = new Operation();
		op.setPath(path);
		op.setDescription(description);
		op.setRequiredHeaders(requiredHeaders);
		op.setPayload(payload);
		return op;
	}

}
