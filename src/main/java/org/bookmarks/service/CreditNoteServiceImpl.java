package org.bookmarks.service;


import java.math.BigDecimal;

import org.bookmarks.domain.CreditNote;
import org.bookmarks.repository.CreditNoteRepository;
import org.bookmarks.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditNoteServiceImpl extends AbstractService<CreditNote> implements CreditNoteService {

	
	@Autowired
	private CreditNoteRepository creditNoteRepository;


	@Override
	public Repository<CreditNote> getRepository() {
		return creditNoteRepository;
	}


	@Override
	public BigDecimal getOutgoings() {
		return creditNoteRepository.getOutgoings();
	}


	@Override
	public BigDecimal getIncomings() {
		return creditNoteRepository.getIncomings();
	}

}
