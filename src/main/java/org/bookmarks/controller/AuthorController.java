package org.bookmarks.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;

import org.bookmarks.domain.Author;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.AZLookupServiceImpl;
import org.bookmarks.service.AuthorService;
import org.bookmarks.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/author")
public class AuthorController extends AbstractBookmarksController<Author> {

	@Autowired
	private AuthorService authorService;
	
	private Logger logger = LoggerFactory.getLogger(AuthorController.class);
	
	@ResponseBody
    @RequestMapping(value="/autoCompleteName", method=RequestMethod.GET)
    public String autoCompleteSurname(String term, Model modelMap) {
            Collection<Author> authors = authorService.findByNameLike("%" + term + "%");
            StringBuffer buffer = new StringBuffer("[ ");
            for(Author b : authors) {
                    buffer.append(" { \"label\": \"" + b.getName() + "\", \"value\": \"" + b.getId() + "\" }");
                    buffer.append(", ");
            }
            String json = buffer.toString();
            json = json.substring(0, json.length() - 2) + "  ]";
            return json;
    }	
	
	@ResponseBody
	@RequestMapping(value="/addAuthor", method=RequestMethod.GET)
	public String addAuthor(String name, HttpSession session) {
		logger.debug("Adding " + name + " to stock item as new author");
		
		StockItem stockItem = (StockItem) session.getAttribute("sessionStockItem");
		
		if(stockItem == null) {
			//Session timeout
			return "failure"; 
		}
		 
		Author exists = authorService.findByName(name);
		if(exists != null) {
			logger.debug("Author already exists");
			return "Author already exists";
		}
		
		Author author = new Author();
		author.setName(name);
//		authorService.save(author);
		
		stockItem.getAuthors().add(author);
		logger.debug("Have added author " + author.getName() + " to " + stockItem.getTitle());
		
		return getAuthorTable(stockItem);
	}		
	
	@ResponseBody
	@RequestMapping(value="/removeAuthor", method=RequestMethod.GET)
	public String removeAuthor(String name, HttpSession session, ModelMap modelMap) {

		StockItem stockItem = (StockItem) session.getAttribute("sessionStockItem");
		
		if(stockItem == null) {
			//Session timeout
			return "failure";  
		}
		
		Author toRemove = null;
		for(Author a : stockItem.getAuthors()) {
			if(a.getName().equals(name)) {
				toRemove = a;
			}
		}
		stockItem.getAuthors().remove(toRemove);
		
		return getAuthorTable(stockItem);
	}	
	
	@ResponseBody
    @RequestMapping(value="/addAuthorToStock", method=RequestMethod.GET)
	public String addAuthorToStock(Long authorId, HttpSession session) {
		StockItem stockItem = (StockItem) session.getAttribute("sessionStockItem");
		if(stockItem == null) {
			//Session timeout
			return "failure";
		}
		
		Author author = authorService.get(authorId);
		stockItem.getAuthors().add(author);

		return getAuthorTable(stockItem);
	}
	
	@ResponseBody
    @RequestMapping(value="/loadAuthorTable", method=RequestMethod.GET)
	public String loadAuthorTable(HttpSession session) {
		StockItem stockItem = (StockItem) session.getAttribute("sessionStockItem");
		if(stockItem == null) {
			//Session timeout
			return "failure";
		}
		
		return getAuthorTable(stockItem);
	}	
	
	private String getAuthorTable(StockItem stockItem) {
		String table = "<table><th>Author</th><th>Actions</th>";
		for(Author a : stockItem.getAuthors()) {
			table = table + "<tr><td>" + a.getName() + "</td><td><a onclick=\"javascript:removeAuthor('" + a.getName() + "')\"> Delete</a></td></tr>";
		}
		table = table + "</table>";  
		
		return table;
	}

	@RequestMapping(value="/search")
	public String search(AuthorSearchBean authorSearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		if(authorSearchBean.isFromSession() == false) { //Pagination etc. already set
			setPaginationFromRequest(authorSearchBean, request);
		} else {
			//
		}

		Collection<Author> categories = authorService.search(authorSearchBean);

		//Don't like, fix for shitty export
		setPageSize(authorSearchBean, modelMap, categories.size());

		//Add to session for later search
		session.setAttribute("authorSearchBean", authorSearchBean);

		modelMap.addAttribute("authorList", categories);
		modelMap.addAttribute("searchResultCount", authorSearchBean.getSearchResultCount());
		
		return "searchAuthors";
	}

	@RequestMapping(value="/moveAndDelete/{id}", method=RequestMethod.GET)
	public String moveAndDelete(@PathVariable("id") Long id, ModelMap modelMap) {	
		
		modelMap.addAttribute("authorToDeleteId", id);
		
		return "moveAndDelete";
	}

	@RequestMapping(value="/moveAndDelete", method=RequestMethod.POST)
	public String moveAndDelete(Long authorToDeleteId, Long authorToKeepId, HttpSession session, ModelMap modelMap) {
		
		Author authorToDelete = authorService.get(authorToDeleteId);
		Author authorToKeep = authorService.get(authorToKeepId);
		
		authorService.moveAndDelete(authorToDelete, authorToKeep);
		
//		modelMap.addAttribute(author);
		
		return "moveAndDelete";
	}		

	

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		AuthorSearchBean authorSearchBean = new AuthorSearchBean();
		modelMap.addAttribute(authorSearchBean);
		return search(authorSearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(Long id, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		Author author = authorService.get(id);
		try {
			authorService.delete(author);
			redirectAttributes.addFlashAttribute("success", author.getName() + " has been deleted");
		} catch (Exception e) {
			//Most likely due to this invoice being referenced from col
			addError("Cannot delete! Perhaps this author has authored", modelMap);
			return searchFromSession(session, request, modelMap);
		}		
		
		
		return "redirect:searchFromSession";
	}	

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(@Valid Author author, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		
		author.setName(author.getName().trim());
		
		Author exists = authorService.findByName(author.getName());
		
		if(exists != null) {
			addError(author.getName() + " already exists", modelMap);
			return "addAuthor";
		}
		if(bindingResult.hasErrors()){
			addError("Cannot save author", modelMap);
			return "addAuthor";
		}
		
		authorService.save(author);
		
		AuthorSearchBean authorSearchBean = new AuthorSearchBean();
		authorSearchBean.setAuthor(author);
		
		session.setAttribute("authorSearchBean", authorSearchBean);		
		
		redirectAttributes.addFlashAttribute("success", "Have added author " + author.getName());
		
		return "redirect:searchFromSession";
	}
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String displayAdd(ModelMap modelMap) {
		modelMap.addAttribute(new Author());
		return "addAuthor";
	}

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid Author author, BindingResult bindingResult, String flow, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			modelMap.addAttribute("flow", flow);
			return "editAuthor";
		}
		
		Author dbAuthor = authorService.get(author.getId());
		dbAuthor.setName(author.getName());

		authorService.update(dbAuthor);

		AuthorSearchBean authorSearchBean = new AuthorSearchBean();
		authorSearchBean.setAuthor(author);
		session.setAttribute("authorSearchBean", authorSearchBean);				

		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String displayEdit(Long id, String flow, ModelMap modelMap) {
		Author author = authorService.get(id);
		modelMap.addAttribute(author);
		modelMap.addAttribute("flow", flow);
		return "editAuthor";
	}


	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		AuthorSearchBean authorSearchBean = (AuthorSearchBean) session.getAttribute("authorSearchBean");
		
		if(authorSearchBean == null) {
			authorSearchBean = new AuthorSearchBean();
		}
		
		authorSearchBean.isFromSession(true);
		
		modelMap.addAttribute(authorSearchBean);
		
		return search(authorSearchBean, request, session, modelMap);
	}

	@Override
	public Service getService() {
		return authorService;
	}
}

