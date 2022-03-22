/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.api.delegate;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import fr.ans.api.sign.esignsante.psc.api.ChecksignatureApiDelegate;
import fr.ans.api.sign.esignsante.psc.esignsantewebservices.call.EsignsanteCall;
import fr.ans.api.sign.esignsante.psc.model.Error;
import fr.ans.api.sign.esignsante.psc.model.Result;
import fr.ans.api.sign.esignsante.psc.utils.Helper;
import fr.ans.api.sign.esignsante.psc.utils.TYPE_SIGNATURE;
import fr.ans.esignsante.model.ESignSanteValidationReport;
import fr.ans.esignsante.model.Erreur;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe DefaultApiDelegateImpl. Implementation des EndPoints /checksignature/*
 */

@Service
@Slf4j
public class ChecksignatureApiDelegateImpl extends AbstractApiDelegate implements ChecksignatureApiDelegate {

	@Autowired
	EsignsanteCall esignWS;

	@Override

	public ResponseEntity<Result> postChecksignaturePades(
			@ApiParam(value = "") @Valid @RequestPart(value = "file", required = true) MultipartFile file) {

		log.debug("Demande Checksignature Pades reçue");

		return execute(TYPE_SIGNATURE.PADES, file);
	}

	@Override
	public ResponseEntity<Result> postChecksignatureXades(
			@ApiParam(value = "") @Valid @RequestPart(value = "file", required = false) MultipartFile file) {

		log.debug("Réception d'une demande Checksignature Pades.");

		return execute(TYPE_SIGNATURE.XADES, file);
	}

	private HttpStatus interceptErrorCheckSignature(Exception e) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		log.debug("message {} \n", e.getMessage());
		log.debug("toString {} \n", e.toString());
		log.debug("class Name {} \n", e.getClass().getName());
		log.debug("class Canonicalname {} \n", e.getClass().getCanonicalName());

		switch (e.getClass().getCanonicalName()) {

		case "org.springframework.web.client.HttpServerErrorException.NotImplemented":
			// 501: fichier non xm par exemple
			log.error("Echec de l'appel à esignsanteWS => HttpServerErrorException.NotImplemented");
			throwExceptionRequestError(
					"Echec de l'appel à esignsanteWS. Cause possible: erreur sur le type de fichier fourni (par exemple non xml pour Xades, non PDF pour PADES) ",
					HttpStatus.NOT_IMPLEMENTED);
			break;

		case "org.springframework.web.client.ResourceAccessException":
			// esignWS OFF par exemple
			log.error("Exception sur appel esignWS. Service inaccessible");
			throwExceptionRequestError("Exception sur appel esignWS. Service inaccessible",
					HttpStatus.SERVICE_UNAVAILABLE);

			break;

		case "org.springframework.web.client.HttpClientErrorException.NotFound":
			// 404: erreur sur l'id de la conf d'esignsWS
			log.error("Echec de l'appel à esignsanteWS => HttpClientErrorException.NotFound");
			log.error("\t cause possible: identifiant de configuration esignWS invalid.");
			throwExceptionRequestError(
					"Echec de l'appel à esignsanteWS. Cause possible: identifiant de configuration esignWS invalid.) ",
					HttpStatus.INTERNAL_SERVER_ERROR);
			break;

		default:
			// comprend erreur 400: requête mal formée, 404 id conf introuvable.
			throwExceptionRequestError("Echec de l'appel à esignsanteWS: Exception non gérée",
					HttpStatus.INTERNAL_SERVER_ERROR);
			break;

		}
		return status;
	}

	private Result esignsanteReportToResult(ESignSanteValidationReport report) {
		log.debug("retour appel esignsante OK pour un contrôle de signature - formatage de la réponse");
		log.debug("report {}", report.isValide());

		List<Erreur> erreurs = report.getErreurs();
		List<Error> errors = Helper.mapListErreurToListError(erreurs);

		var result = new Result();
		result.setValid(report.isValide());
		result.setErrors(errors);

		return result;
	}

	private ResponseEntity<Result> execute(TYPE_SIGNATURE typeSignature, MultipartFile file) {
	
		// controle du acceptheader
		if (!isAcceptHeaderPresent(getAcceptHeaders(), Helper.APPLICATION_JSON)) {
			throwExceptionRequestError(
					"Requête rejetée car le header 'accept=application/json' n'a pas été trouvé dans la requête",
					HttpStatus.NOT_ACCEPTABLE);
		}

				
		if  ((file == null) || (file.getSize() <= 0))  {
			throwExceptionRequestError(
					"Le fichier signé à contrôler n'a pas été transmis ou est vide(MultiPartFile 'file')",
					HttpStatus.BAD_REQUEST);
		}
		
		var fileToCheck = getMultiPartFile(file);
		if (fileToCheck == null) {
			throwExceptionRequestError("Le fichier signé à contrôler n'a pas pu être lu",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		ESignSanteValidationReport report = null;

		try {
			if (TYPE_SIGNATURE.XADES.equals(typeSignature)) {
				report = esignWS.chekSignatureXades(fileToCheck);
			} else {
				report = esignWS.chekSignaturePades(fileToCheck);
			}
	

		} catch (Exception e) {
			//lève une Exception http avec un status dépendant de la classe de l'exception
			interceptErrorCheckSignature(e);
		}
		
		if (report == null) {
			throwExceptionRequestError("Exception technique inattendue",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
			
		Result result = esignsanteReportToResult(report);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}