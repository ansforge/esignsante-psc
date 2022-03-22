/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.storage.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import fr.ans.api.sign.esignsante.psc.storage.entity.ProofStorage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PreuveDAOImpl implements PreuveDAO {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	ProofMongoRepository repo;

	public boolean archivePreuve(String idMongoDB, ProofStorage proof) {
		log.debug("demande persistence de la preuve d'une demande de signature: \n" + "idMongoDB: " + proof.get_id()
				+ "\n" + "SubjectOrganization: " + proof.getSubjectOrganization() + "\n" + "Preferred_username(): "
				+ proof.getPreferred_username() + "\n" + "Given_name: " + proof.getGiven_name() + "\n" + "Family_name: "
				+ proof.getFamily_name() + "\n" + "Timestamp (acceptation de la reqête): " + proof.getTimestamp() + "\n"
				+ "proof " + proof);
		Query query = Query.query(Criteria.where("id").is(idMongoDB));
		Update update = new Update();
		update.set("proof", proof);
		mongoTemplate.updateFirst(query, update, ProofStorage.class);
		return false;
	}

	public boolean archivePreuve(ProofStorage proof) {
		log.debug(
				"demande persistence de la preuve d'une demande de signature pour: \r\n "
						+ " SubjectOrganization: {} \r\n  Preferred_username: {} \r\n Given_name: {} \r\n"
						+ "Family_name:{}  \r\n Timestamp (acceptation de la reqête): {}   ",
				proof.getSubjectOrganization(), proof.getPreferred_username(), proof.getGiven_name(),
				proof.getFamily_name(), proof.getTimestamp());
		mongoTemplate.save(proof);

		return true;
	}

}
