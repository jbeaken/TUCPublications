package org.bookmarks.service;

import org.bookmarks.domain.VTTransaction;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.VTTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VTTransactionServiceImpl extends AbstractService<VTTransaction> implements VTTransactionService {

	
	@Autowired
	private VTTransactionRepository VTTransactionRepository;


	@Override
	public Repository<VTTransaction> getRepository() {
		return VTTransactionRepository;
	}
}
