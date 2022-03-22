/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.api;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class SwaggerUiController.
 */
@Profile("!swagger")
@RestController
public class SwaggerUiController {

	/**
	 * Gets the swagger UI.
	 *
	 * @param httpResponse the http response
	 */
	@GetMapping
	public void getSwagger(final HttpServletResponse httpResponse) {
		httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
	}
}
