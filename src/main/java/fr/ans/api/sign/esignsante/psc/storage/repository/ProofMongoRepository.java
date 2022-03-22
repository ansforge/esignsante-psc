/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.storage.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import fr.ans.api.sign.esignsante.psc.storage.entity.ProofStorage;


public interface ProofMongoRepository extends MongoRepository<ProofStorage, ObjectId>{

	@Query("{ 'subjectOrganization' : ?0 }")
	List<ProofStorage> findBySubjectOrganization(String subjectOrganization);
	
	@Query(value = "{'subjectOrganization' : ?0}", fields = "{'bsonProof' : 0}")
	List<ProofStorage> findBySubjectOrganizationWithOutBSON(String subjectOrganization);
	
	@Query("{ 'subjectOrganization' : ?0 , 'timestamp' : { $gt: ?afterDate }}, fields = {'bsonProof' : 0}")
	List<ProofStorage> findByAllFamilyNameAFterDate(String family_name, Date afterDate);
	
	@Query("{ '_id' : ?0 }")
    ProofStorage findOneById(ObjectId objectId);
}
