/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * The Class SwaggerConfiguration.
 */
@Profile(value = { "swagger" })
@PropertySource("classpath:swagger.properties")
@Configuration
public class SwaggerConfiguration {

}
