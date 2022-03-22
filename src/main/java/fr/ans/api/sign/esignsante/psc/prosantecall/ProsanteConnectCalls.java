/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.prosantecall;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.ans.api.sign.esignsante.psc.utils.Helper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProsanteConnectCalls {

	@Value("${psc.url}")
	public String pscUrl;

	@Value("${psc.clientID}")
	public String pscClientID;

	@Value("${psc.clientSecret}")
	public String pscSecret;

	@Value("#{new Boolean('${psc.bypass}')}")
	public Boolean pscBypass = false;

	private HttpHeaders getHeaders() throws UnsupportedEncodingException {
		String adminuserCredentials = pscClientID + ":" + pscSecret;
		String encodedCredentials = Helper.encodeBase64(adminuserCredentials);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Basic " + encodedCredentials);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return httpHeaders;
	}

	public String isTokenActive(String accessToken)
			throws URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		RestTemplate restTemplate = new RestTemplate();
		log.debug("uri connection à ProSanteConnect: {}", pscUrl);
		log.debug("ClientID pour proSanteConnect: {}", pscClientID);
		log.debug("Bypass resultat Introspection: {}", pscBypass);
		URI uri = new URI(pscUrl);

		// payload
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token", accessToken);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, getHeaders());
		ResponseEntity<String> result = restTemplate.postForEntity(uri, requestEntity, String.class);

		log.debug("Appel ProSanteConnect REsponse: StatusCode {} \n body: {}", result.getStatusCode(),
				result.getBody());
		return result.getBody();
	}

	public Boolean bypassResultatPSC() {
		log.debug(" Bypass resultat de PSC: {}", pscBypass);
		return pscBypass;
	}
}
