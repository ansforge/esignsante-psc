/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.esignsantewebservices.call;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.ans.esignsante.ApiClient;
import fr.ans.esignsante.api.CaApi;
import fr.ans.esignsante.api.PadesApi;
import fr.ans.esignsante.api.XadesApi;
import fr.ans.esignsante.model.ESignSanteSignatureReportWithProof;
import fr.ans.esignsante.model.ESignSanteValidationReport;
import fr.ans.esignsante.model.OpenidToken;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
/*
 * Classe qui fait les appels à EsignsanteWebservices pour la signature des
 * documents et les contrôles de documents signés.
 */
public class EsignsanteCall {

	ApiClient esignWSApiClient = null;

	@Autowired
	EsignsanteConfigurationProperties esignConf;

	public ApiClient confApiClient() {
		esignWSApiClient = new ApiClient();
		esignWSApiClient.setBasePath(esignConf.getBasePath());

		log.info("Appel esignante Webservices pour basePath = " + esignConf.getBasePath());
		return esignWSApiClient;
	}

	public ESignSanteSignatureReportWithProof signatureXades(File fileToSign, List<String> signers, String requestId,
			List<OpenidToken> openidTokens) {

		esignWSApiClient = confApiClient();

		log.debug("appel esignsanteWebservices pour une demande de signature en xades ");
		log.debug("fileToSign: {}", fileToSign.getName());

		XadesApi api = new XadesApi(esignWSApiClient);
		log.debug("paramètres: basePath: {} \n idSignConf: {} \n proofConfId: {} \n", esignWSApiClient.getBasePath(),
				esignConf.getSignatureConfId(), esignConf.getProofConfId());
		ESignSanteSignatureReportWithProof report = null;

		report = api.signatureXadesWithProof(esignConf.getSecret(), esignConf.getSignatureConfId(), fileToSign, signers,
				esignConf.getProofConfId(), requestId, esignConf.getProofTag(), esignConf.getAppliantId(),
				openidTokens);

		log.debug("nbError: " + report.getErreurs().size());
		return report;
	}

	public ESignSanteSignatureReportWithProof signaturePades(File fileToSign, List<String> signers, String requestId,
			List<OpenidToken> openidTokens) {

		esignWSApiClient = confApiClient();

		log.debug("appel esignsanteWebservices pour une demande de signature en PADES ");
		log.debug("fileToSign: {}", fileToSign.getName());

		PadesApi api = new PadesApi(esignWSApiClient);
		log.debug("paramètres: basePath: {} \n idSignConf: {} \n proofConfId: {} \n", esignWSApiClient.getBasePath(),
				esignConf.getSignatureConfId(), esignConf.getProofConfId());
		ESignSanteSignatureReportWithProof report = null;

		report = api.signaturePadesWithProof(esignConf.getSecret(), esignConf.getSignatureConfId(), fileToSign, signers,
				esignConf.getProofConfId(), requestId, esignConf.getProofTag(), esignConf.getAppliantId(),
				openidTokens);

		log.debug("nbError: " + report.getErreurs().size());
		return report;
	}

	public ESignSanteValidationReport chekSignatureXades(File fileToCheck) {

		esignWSApiClient = confApiClient(); // juste le BasePath

		ESignSanteValidationReport report = null;

		long idCheckSign = esignConf.getCheckSignatureConfId();
		log.debug("appel esignsanteWebservices pour une VERIF de signature en xades");
		log.debug("paramètres: basePath: " + esignWSApiClient.getBasePath() + " id checkSign d esignsante: "
				+ idCheckSign + " file: " + fileToCheck.getName());
		log.debug("fileExist:" + fileToCheck.exists());

		XadesApi api = new XadesApi(esignWSApiClient);
		report = api.verifSignatureXades(idCheckSign, fileToCheck);
		return report;
	}

	public ESignSanteValidationReport chekSignaturePades(File fileToCheck) {

		esignWSApiClient = confApiClient(); // juste le BasePath

		ESignSanteValidationReport report = null;

		long idCheckSign = esignConf.getCheckSignatureConfId();

		log.debug("appel esignsanteWebservices pour une VERIF de signature en PADES");
		log.debug("paramètres: basePath: " + esignWSApiClient.getBasePath() + " id checkSign d esignsante: "
				+ idCheckSign + " file: " + fileToCheck.getName());
		log.debug("fileExist:" + fileToCheck.exists());

		PadesApi api = new PadesApi(esignWSApiClient);
		report = api.verifSignaturePades(idCheckSign, fileToCheck);
		return report;
	}

	public List<String> getCA() {
		List<String> results = null;
		esignWSApiClient = confApiClient();
		CaApi api = new CaApi(esignWSApiClient);
		results = api.getCA();
		return results;
	}

}
