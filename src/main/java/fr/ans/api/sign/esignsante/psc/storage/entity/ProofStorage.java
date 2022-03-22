/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.storage.entity;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(of = { "preferred_username", "timestamp" })
@ToString(of = { "id", "subjectOrganization", "preferred_username", "given_name", "family_name", "timestamp" })
@Getter

/*
 * Classe ProofStorage: Modèle pour stokage de la preuve dans MongoDB
 */
@Document(collection = "preuves")
public class ProofStorage {

	public ProofStorage() {
	}; // findAll

	public ProofStorage(String requestID, String subjectOrganization, String preferred_username, String given_name,
			String family_name, Date timestamp, String proof) {
		super();
		this.requestId = requestID;
		this.subjectOrganization = subjectOrganization;
		this.preferred_username = preferred_username;
		this.given_name = given_name;
		this.family_name = family_name;
		this.timestamp = timestamp;
		this.proof = proof;
	}

	public ProofStorage(String requestID, String subjectOrganization, String preferred_username, String given_name,
			String family_name, Date timestamp) {
		super();
		this.requestId = requestID;
		this.subjectOrganization = subjectOrganization;
		this.preferred_username = preferred_username;
		this.given_name = given_name;
		this.family_name = family_name;
		this.timestamp = timestamp;
	}

	public ProofStorage(ObjectId id,
			@NotNull(message = "Persistence: le champ  'requestId'ne doit pas être nul") String requestId,
			String subjectOrganization, String preferred_username, String given_name, String family_name,
			@NotNull(message = "Persistence: le 'timestamp' ne doit pas être nul") Date timestamp,
			@NotNull(message = "Persistence: la 'proof' ne doit pas être nulle") String proof) {
		super();
		this._id = id;
		this.requestId = requestId;
		this.subjectOrganization = subjectOrganization;
		this.preferred_username = preferred_username;
		this.given_name = given_name;
		this.family_name = family_name;
		this.timestamp = timestamp;
		this.proof = proof;

	}

	@Id
	private ObjectId _id;

	@Setter
	@NotNull(message = "Persistence: le champ  'requestId'ne doit pas être nul")
	private String requestId;

	@Setter
	private String subjectOrganization;

	@Setter
	private String preferred_username;

	@Setter
	private String given_name;

	@Setter
	private String family_name;

	@Setter
	@NotNull(message = "Persistence: le 'timestamp' ne doit pas être nul")
	private Date timestamp;

	@Setter
	@NotNull(message = "Persistence: la 'proof' ne doit pas être nulle")
	private String proof;

}
