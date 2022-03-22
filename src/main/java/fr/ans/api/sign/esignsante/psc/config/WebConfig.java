/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/*
 * WebConfig
 */
@Configuration
@Import(SwaggerConfiguration.class)
@EnableSwagger2
public class WebConfig {

	/** ESignSante Build Properties. */
	@Autowired
	private BuildProperties buildProperties;

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("eSignSante-PSC v" + buildProperties.getVersion())
				.description("Api de signature à partir d'un jeton ProSantéConnect")
				.license("Apache 2.0")
				.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
				.termsOfServiceUrl("https://esante.gouv.fr")
				.version("1.0")
				.contact(new Contact("ANS", "https://esante.gouv.fr", "esignsante@esante.gouv.fr"))
				.build();
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.useDefaultResponseMessages(false)
				.select()
				.apis(RequestHandlerSelectors.basePackage("fr.ans.api.sign.esignsante.psc.api"))
				.paths(PathSelectors.any())
				.build();

	}

}
