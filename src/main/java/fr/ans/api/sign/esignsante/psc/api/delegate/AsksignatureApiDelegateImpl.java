/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.api.delegate;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.ans.api.sign.esignsante.psc.api.AsksignatureApiDelegate;
import fr.ans.api.sign.esignsante.psc.esignsantewebservices.call.EsignsanteCall;
import fr.ans.api.sign.esignsante.psc.model.UserInfo;
import fr.ans.api.sign.esignsante.psc.prosantecall.ProsanteConnectCalls;
import fr.ans.api.sign.esignsante.psc.storage.entity.ProofStorage;
import fr.ans.api.sign.esignsante.psc.storage.repository.PreuveDAO;
import fr.ans.api.sign.esignsante.psc.utils.FileResource;
import fr.ans.api.sign.esignsante.psc.utils.Helper;
import fr.ans.api.sign.esignsante.psc.utils.ParametresSign;
import fr.ans.api.sign.esignsante.psc.utils.TYPE_SIGNATURE;
import fr.ans.esignsante.model.ESignSanteSignatureReportWithProof;
import fr.ans.esignsante.model.OpenidToken;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe DefaultApiDelegateImpl. Implementation des EndPoints /Asksignature/*
 */
@Service
@Slf4j
public class AsksignatureApiDelegateImpl extends AbstractApiDelegate implements AsksignatureApiDelegate {

	/*
	 * Appel esignSanteWebservices liste Cas, contrôles et demandes de signatures
	 */
	@Autowired
	EsignsanteCall esignWS;

	/*
	 * Acces à MOngoDB Archivage de la preuve sur une demande de signature
	 */
	@Autowired
	PreuveDAO dao;

	/*
	 * Appel ProSanteConnect pour vérifiacation de la validité de l'accessToken
	 * (Introspection)
	 */
	@Autowired
	ProsanteConnectCalls pscApi;

	private static final String SIGNER_XADES = "Délégataire de signature pour ";

	private static final String SIGNER_PADES = "Signé pour le compte de ";

	@Override
	public ResponseEntity<org.springframework.core.io.Resource> postAskSignaturePades(
			@ApiParam(value = "", required = true) @RequestHeader(value = "access_token", required = true) String accessToken,
			@ApiParam(value = "Objet contenant le fichier ) signer et le UserInfo") @Valid @RequestPart(value = "file", required = true) MultipartFile file,
			@ApiParam(value = "") @Valid @RequestPart(value = "userinfo", required = false) String userinfo) {

		log.debug("Réception d'une demande de signature Pades");

		List<String> acceptedHeaders = getAcceptHeaders();
		if (!isAcceptHeaderPresent(acceptedHeaders, Helper.APPLICATION_JSON)) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		if (!isAcceptHeaderPresent(acceptedHeaders, Helper.APPLICATION_PDF)) {
			throwExceptionRequestError("Le header 'accept' doit contenir  application/json et application/pdf",
					HttpStatus.NOT_ACCEPTABLE);
		}


		
		ESignSanteSignatureReportWithProof report = executeAskSignature(TYPE_SIGNATURE.PADES, accessToken, file,
				userinfo);

		String signedStringBase64 = report.getDocSigne();
		byte[] signedDoc = null;

		try {
			signedDoc = Helper.decodeBase64toByteArray(signedStringBase64);
		} catch (UnsupportedEncodingException | IllegalArgumentException e) {
			log.debug(e.toString());
			throwExceptionRequestError(e, "Exception Serveur (décodage base64 du fichier signé PADES)",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Resource resource = new FileResource(signedDoc, "SIGNED_" + file.getOriginalFilename());

		var httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_PDF);
		return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<String> postAsksignatureXades(
			@ApiParam(value = "", required = true) @RequestHeader(value = "access_token", required = true) String accessToken,
			@ApiParam(value = "Objet contenant le fichier ) signer et le UserInfo") @Valid @RequestPart(value = "file", required = true) MultipartFile file,
			@ApiParam(value = "") @Valid @RequestPart(value = "userinfo", required = false) String userinfo) {

		log.debug("Réception d'une demande de signature Xades");
        
		List<String> acceptedHeaders = getAcceptHeaders();
		if (!isAcceptHeaderPresent(acceptedHeaders, Helper.APPLICATION_JSON)) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		if (!isAcceptHeaderPresent(acceptedHeaders, Helper.APPLICATION_XML)) {
			throwExceptionRequestError("Le header 'accept' doit contenir  application/json et application/xml",
					HttpStatus.NOT_ACCEPTABLE);
		}
		ESignSanteSignatureReportWithProof report = executeAskSignature(TYPE_SIGNATURE.XADES, accessToken, file,
				userinfo);

		var signedStringBase64 = report.getDocSigne();

		String signedString = null;
		try {
			signedString = Helper.decodeBase64toString(signedStringBase64);
		} catch (UnsupportedEncodingException | IllegalArgumentException e) {
			log.debug(e.toString());
			throwExceptionRequestError(e, "Exception Serveur (décodage base64 du fichier signé XADES)",
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

		var httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_XML);
		return new ResponseEntity<>(signedString, httpHeaders, HttpStatus.OK);

	}

	private String checkPSCToken(String token) {
		var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		String msgError = "Exception sur vérfication de la validté du token PSC: " + token;
		var result = "Reponse ProSanteConnect inconnue";
		Boolean bypass = pscApi.bypassResultatPSC();
		try {
			result = pscApi.isTokenActive(token);
			httpStatus = Helper.parsePSCresponse(result);
			log.debug("Appel PSC intropesction: token= {}  reponse PSC = {} ", token, result);
			if (httpStatus == HttpStatus.UNAUTHORIZED) {
				msgError = "L'accessToken fourni dans la requête n'est pas reconnu comme un token actif par ProSanteConnect token: "
						+ token + " response PSC: " + result;

			}
		} catch (JsonProcessingException | UnsupportedEncodingException | URISyntaxException e1) {
			msgError = msgError.concat(" , JsonProcessingException");
			log.debug(e1.toString());
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		if (Boolean.FALSE.equals(bypass)) {
			if (!httpStatus.equals(HttpStatus.OK)) {
				throwExceptionRequestError(msgError, httpStatus);
			}
		} else {
			log.error("!!!!!!!!! ATTENTION BYPASS du resultat de l'introspection ProsanteConnect !!!!!!!!!");
		}

		return result;
	}

	private void archivagePreuve(ESignSanteSignatureReportWithProof report, String requestID, UserInfo userToPersit,
			Date now) {

		log.debug(" START archivage de la preuve en BDD pour request id: {}", requestID);
		ProofStorage proof = new ProofStorage(requestID, userToPersit.getSubjectOrganization(),
				userToPersit.getPreferredUsername(), userToPersit.getGivenName(), userToPersit.getFamilyName(), now,
				report.getPreuve());
		try {
			dao.archivePreuve(proof);
		} catch (Exception e) {
			log.debug(e.toString());
			throwExceptionRequestError(e,
					"Requête abandonnée pour problème d'archivage de la preuve en base de données. ",
					HttpStatus.SERVICE_UNAVAILABLE);
		}

		log.debug("Fin archivage de la preuve en BDD pour request id: {}", requestID);
	}

	private UserInfo extractUserInfoFromRequest(String jsonUserInfoBase64) {
		UserInfo userInfo = null;
		try {
			userInfo = Helper.jsonBase64StringToUserInfo(jsonUserInfoBase64);
		} catch (JsonProcessingException e) {
			throwExceptionRequestError(e, "Exception en parsant le userInfo reçu: " + jsonUserInfoBase64,
					HttpStatus.BAD_REQUEST);
		} catch (UnsupportedEncodingException | IllegalArgumentException e) {
			throwExceptionRequestError(e,
					"Le userinfo fourni ne semble pas codé en base 64. UserInfo reçu : " + jsonUserInfoBase64,
					HttpStatus.BAD_REQUEST);
		}
		return userInfo;
	}

	/*
	 * Methode qui vérifie les paramètres d'entrée de la requête (hors HEader
	 * Accept) et les formate pour esignWS et MongoDB
	 */
	private ParametresSign prepareAppelEsignWS(String accessToken, MultipartFile file, String userinfo) {
		ParametresSign resultat = new ParametresSign();

		resultat.setDate(new Date());

		// verification de l'accessToken
		String pscResult = checkPSCToken(accessToken);
		resultat.setPscReponse(pscResult);

		// fichier reçu
		var fileToSign = getMultiPartFile(file);
		resultat.setFileToSign(fileToSign);

		// extraction du userInfo
		var userToPersit = extractUserInfoFromRequest(userinfo);
		resultat.setUserinfo(userToPersit);

		// génération d'un UUDI
		resultat.setRequestID(Helper.generateRequestId());

		return resultat;

	}

	private ESignSanteSignatureReportWithProof executeAskSignature(TYPE_SIGNATURE typeSignature, String accessToken,
			MultipartFile file, String userinfo) {

		checkNotEmptyInputParameters(accessToken, file, userinfo);

		ParametresSign params = prepareAppelEsignWS(accessToken, file, userinfo);

		// verification du type de fichier
		String typeFile = checkTypeFile(params.getFileToSign());
		// contrôle uniquement des PDFs
		if ((typeSignature.equals(TYPE_SIGNATURE.PADES)) && (!typeFile.equals(Helper.APPLICATION_PDF))) {
				throwExceptionRequestError(
						"Requête rejetée car le fichier transmis semble ne pas être un PDF, type détecté  " + typeFile,
						HttpStatus.UNSUPPORTED_MEDIA_TYPE);
			}
		

		log.debug(
				"Demande de signature valide: verif AccessToken OK: {} \n, File OK {} \n, userInfo OK Organisation: {}",
				params.getPscReponse(), params.getFileToSign().getName(),
				params.getUserinfo().getSubjectOrganization());

		// construction du OpenIDToken
		var user = params.getUserinfo();
		List<OpenidToken> openidTokens = new ArrayList<>();
		var openidToken = new OpenidToken();
		openidToken.setAccessToken(accessToken);
		openidToken.setIntrospectionResponse(params.getPscReponse());
		openidToken.setUserInfo(userinfo);
		openidTokens.add(openidToken);
		log.debug("openidtoken[0] transmis à esignWS:");
		log.debug("\t userinfo (base64): {} ", openidTokens.get(0).getUserInfo());
		log.debug("\t accessToken: {} ", openidTokens.get(0).getAccessToken());
		log.debug("\t PSCResponse: {} ", openidTokens.get(0).getIntrospectionResponse());

		// liste des signataires
		List<String> signers = setSigners(typeSignature, user);

		// Appel esignsante
		log.debug("Appel esignWS pour une signature de type {}", typeSignature.getTypeSignature());
		ESignSanteSignatureReportWithProof report = null;
		try {
			if ((TYPE_SIGNATURE.PADES.toString()).equals(typeSignature.getTypeSignature())) {
				report = esignWS.signaturePades(params.getFileToSign(), signers, params.getRequestID(), openidTokens);
			} else {
				report = esignWS.signatureXades(params.getFileToSign(), signers, params.getRequestID(), openidTokens);
			}

		} catch (RestClientException e) {
			throwExceptionRequestError(e, "Exception sur appel esignWS. Service inaccessible",
					HttpStatus.SERVICE_UNAVAILABLE);
		}

		if (report == null) {
			throwExceptionRequestError("Exception technique sur appel esignWS", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		archivagePreuve(report, params.getRequestID(), user, params.getDate());

		return report;
	}

	private List<String> setSigners(TYPE_SIGNATURE typeSignature, UserInfo userinfo) {
		List<String> retour = new ArrayList<>();
		String signer = SIGNER_XADES;

		if (typeSignature.getTypeSignature().equals(TYPE_SIGNATURE.PADES.toString())) {
			signer = SIGNER_PADES;
		}
		signer = signer.concat(userinfo.getFamilyName()).concat(" (").concat(userinfo.getSubjectNameID()).concat(")");
		log.error("signer: " + signer);
		retour.add(signer);
		return retour;
	}

	public void checkNotEmptyInputParameters(String accessToken, MultipartFile file, String userinfo) {
		if ((accessToken == null) || (file == null) || (userinfo == null)) {
			throwExceptionRequestError(
					"Au moins un paramètre indispensable parmi {'file', 'userinfo', 'accessToken'} est nul ou non fourni",
					HttpStatus.BAD_REQUEST);
		}

		if ((accessToken.isEmpty()) || (accessToken.isBlank()) || (file.getSize() <= 0) || (userinfo.isEmpty())
				|| (userinfo.isBlank())) {
			throwExceptionRequestError("Au moins un paramètre parmi {'file', 'userinfo', 'accessToken'} est vide",
					HttpStatus.BAD_REQUEST);
		}
	}
}
