/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import fr.ans.api.sign.esignsante.psc.api.exception.EsignPSCRequestException;

//https://www.baeldung.com/exception-handling-for-rest-with-spring

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	  @ExceptionHandler(value 
		      = { EsignPSCRequestException.class}) 
		    protected ResponseEntity<Object> handleConflict(
		      RuntimeException ex, WebRequest request) {	      
		        EsignPSCRequestException except = (EsignPSCRequestException)ex;
		        return handleExceptionInternal(ex, except.getErreur(), 
		          new HttpHeaders(), except.getStatusARetourner(), request);
		    }
}
 