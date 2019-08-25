package org.bookmarks.controller;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.controller.helper.ISBNConvertor;
import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.Availablity;
import org.bookmarks.domain.Binding;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.ConcreteEntity;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.OrderLine;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.service.CategoryService;
import org.bookmarks.service.PublisherService;
import org.bookmarks.service.SupplierService;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class OrderLineController extends AbstractBookmarksController<OrderLine>{

}
