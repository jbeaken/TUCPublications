package org.bookmarks.service;

import java.io.IOException;

import org.bookmarks.bean.CreditNoteHolder;
import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.VTTransaction;
import org.bookmarks.repository.AccountRepository;
import org.bookmarks.repository.VTTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

@Service
public class TSBService {
	
	@Autowired
	private VTTransactionRepository vtTransactionRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Transactional
	public void saveAccountsFromTSB(CreditNoteHolder holder, Boolean credit) throws IOException {

		for (CreditNote creditNote : holder.getCreditNoteMap().values()) {

			if (creditNote.getStatus().equals("Unmatched")) {
				continue;
			}

			if (creditNote.getStatus().equals("Already Processed")) {
				continue;
			}

			if (creditNote.isClubAccount()) {

				VTTransaction transaction = new VTTransaction();

				transaction.setTotal(creditNote.getAmount().floatValue());
				transaction.setDate(creditNote.getDate());
				transaction.setPrimaryAccount("Club Account");

				vtTransactionRepository.save(transaction);
			}

			accountRepository.processCreditNote(creditNote, credit);
		}

	}
}
