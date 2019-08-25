package org.bookmarks.service;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.StockItem;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends AbstractService<Category> implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Override
	public Repository<Category> getRepository() {
		return categoryRepository;
	}

	@Override
	public List<StockItem> getStickies(Long id) {
		return categoryRepository.getStickies(id);
	}

	@Override
	public void saveStickies(List<StockItem> stockItems, Category category) {
		//Reset stickies
		categoryRepository.resetStickies(category);
		
		//Go through stockitems, saving index as stickyCategory
		Long index = (long) stockItems.size();
		for(StockItem si : stockItems) {
			categoryRepository.saveSticky(si, index--);
		}
		
	}
}
