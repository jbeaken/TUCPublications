package org.bookmarks.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bookmarks.domain.Author;
import org.bookmarks.domain.StockItem;
import org.bookmarks.repository.AuthorRepository;
import org.bookmarks.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl extends AbstractService<Author> implements AuthorService {

	@Autowired
	private AuthorRepository authorRepository;
	
	@Override
	public Repository<Author> getRepository() {
		return authorRepository;
	}

	@Override
	public Author findByName(String name) {
		return authorRepository.findByName(name);
	}

	@Override
	public Collection<Author> findByNameLike(String name) {
		return authorRepository.findByNameLike(name);
	}

	@Override
	public List<Author> findByStockItem(StockItem stockItem) {
		return authorRepository.findByStockItem(stockItem);
	}

	@Override
	public void moveAndDelete(Author authorToDelete, Author authorToKeep) {
		authorRepository.moveAndDelete(authorToDelete, authorToKeep);
		
	}
}
