/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class })
@Service
/*
 * Connexion à MongoDb de production (ONline DBB)
 */
public class MongoDBConfig {

	@Value("${spring.data.mongodb.host}")
	private String mongoHost;

	@Value("${spring.data.mongodb.port}")
	private String /* int */ mongoPort;

	@Value("${spring.data.mongodb.database}")
	private String mongoDatabase;

	@Bean
	public MongoTemplate mongoTemplate() throws IOException {
		MongoClient mongoClient = MongoClients.create("mongodb://" + mongoHost + ":" + mongoPort);
		log.info("création d'une connexion à MongoDB avec   mongodb://" + mongoHost + ":" + mongoPort
				+ " mongoDatabase: " + mongoDatabase);

		return new MongoTemplate(mongoClient, mongoDatabase);
	}

}
