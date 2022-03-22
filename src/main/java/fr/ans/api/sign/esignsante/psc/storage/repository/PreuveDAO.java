/**
 * (c) Copyright 1998-2021, ANS. All rights reserved.
 */
package fr.ans.api.sign.esignsante.psc.storage.repository;

import fr.ans.api.sign.esignsante.psc.storage.entity.ProofStorage;
import fr.ans.esignsante.model.ESignSanteSignatureReport;

public interface PreuveDAO {
	
	public boolean archivePreuve(ProofStorage proof );
}
