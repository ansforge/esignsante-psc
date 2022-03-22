/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.api.delegate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.MultipartFile;

import fr.ans.api.sign.esignsante.psc.api.exception.EsignPSCRequestException;
import fr.ans.api.sign.esignsante.psc.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.dynamic.scaffold.MethodRegistry.Handler.ForAbstractMethod;

/**
 * The Class ApiDelegate. Classe mère de tous les delegates. Traitement
 * générique des requêtes
 */
@Slf4j
public abstract class AbstractApiDelegate {

	protected String msgError = "";

	private static final Path TMP_PATH = Paths.get(System.getProperty("java.io.tmpdir"));

	/**
	 * Gets the request
	 *
	 * @return the request
	 */
	public Optional<NativeWebRequest> getRequest() {
		final ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes();

		Optional<NativeWebRequest> request = Optional.of(new ServletWebRequest(attrs.getRequest()));
		return request;
	}

	/**
	 * Gets the accept header.
	 *
	 * @return the accept header
	 */

	//renvoie une liste de tous les headers 'accept' de la requête
	public List<String> getAcceptHeaders() {
		List<String> acceptes = new ArrayList<>();
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes();
		Enumeration<String> acceptheaders = attrs.getRequest().getHeaders("accept");
		while (acceptheaders.hasMoreElements()) {
			List<String> tmp = Arrays.asList(acceptheaders.nextElement().trim().split(","));
			tmp.replaceAll(x -> x.trim());
			acceptes.addAll(tmp);
		}
		return acceptes;
	}

	public File multipartFileToFile(MultipartFile multipart) throws IOException {

		String unsafeFileName = multipart.getOriginalFilename();
		String pureFilename = (new File(unsafeFileName)).getName();

		Path tempFile = Files.createTempFile(TMP_PATH, pureFilename, null);
		log.debug(" multipart.getOriginalFilename: {}. Use {} as name", unsafeFileName, pureFilename);
		log.debug("tempFile {}", tempFile.getFileName().toString());
		multipart.transferTo(tempFile);
		return tempFile.toFile();

	}

	public File getMultiPartFile(MultipartFile file) {
		File fileToCheck = null;
		try {
			fileToCheck = multipartFileToFile(file);
			log.debug("fileToCheck isFile: {} \t name: {} \t length {}", fileToCheck.isFile(), fileToCheck.getName(),
					fileToCheck.length());
		} catch (IOException e) {
			throwExceptionRequestError(e, "Erreur dans la lecture du fichier reçu", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return fileToCheck;

	}

	protected Boolean isAcceptHeaderPresent(List<String> acceptheaders, String expectedAcceptHeader ) {	
		return ( acceptheaders.contains(expectedAcceptHeader)
				 || acceptheaders.contains(Helper.HEADER_TYPE_APP_WILDCARD)
				 || acceptheaders.contains(Helper.HEADER_TYPE_FULL_WILDCARD));
	}

	protected String checkTypeFile(File fichierAtester) {
		var tika = new Tika();
		var detectedType = "";
		try {
			detectedType = tika.detect(fichierAtester);
			log.debug("Verification du fichier {} type detecté: {}", fichierAtester.getName(), detectedType);
		} catch (IOException e) {
			log.debug(e.toString());
			throwExceptionRequestError(e, "Execption sur vérification du type de fichier transmis",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return detectedType;
	}

	protected void throwExceptionRequestError(Exception e, String msg, HttpStatus status) {
		log.debug("Requête en echec. Message retourné à l'utilisateur: {}", msg);
		log.debug("classe Exception: {}", e.getClass().getName());
		log.debug("cause de l'eException: {}", e.getCause());
		log.debug("message de l'exception {}", e.getMessage());
		throwExceptionRequestError(msg, status);
	}

	protected void throwExceptionRequestError(String msg, HttpStatus status) {
		var erreur = new fr.ans.api.sign.esignsante.psc.model.Error();
		erreur.setCode(status.toString());
		erreur.setMessage(msg);
		throw new EsignPSCRequestException(erreur, status);

	}

}
