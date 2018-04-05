package org.bookmarks.service;

import org.bookmarks.domain.VTTransaction;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.VTTransactionRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VTTransactionService extends AbstractService<VTTransaction> {

	
	@Autowired
	private VTTransactionRepositoryImpl VTTransactionRepository;


	@Override
	public Repository<VTTransaction> getRepository() {
		return VTTransactionRepository;
	}


	

}
