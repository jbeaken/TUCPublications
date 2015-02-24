package org.bookmarks.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.bookmarks.controller.StockItemController;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.controller.bean.MonthlySaleReportBean;
import org.bookmarks.controller.bean.ReorderReviewBean;
import org.bookmarks.controller.bean.ReorderReviewStockItemBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.domain.Author;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.SupplierOrderLineType;
import org.bookmarks.domain.SupplierOrderLineStatus;
import org.bookmarks.domain.SupplierOrderStatus;
import org.bookmarks.repository.ReorderReviewRepository;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.SupplierOrderRepository;
import org.bookmarks.service.SaleReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReorderReviewServiceImpl extends AbstractService<SupplierOrder> implements ReorderReviewService {

	@Autowired
	private SupplierOrderLineService supplierOrderLineService;	
	
	@Autowired
	private SaleReportService saleReportService;	
	
	@Autowired
	private ReorderReviewRepository reorderReviewRepository;	
	
	@Autowired
	private StockItemService stockItemService;
	
	@Autowired
	private AuthorService authorService;
	
	private Logger logger = LoggerFactory.getLogger(ReorderReviewServiceImpl.class);
	
	@Override
	public void populate(ReorderReviewStockItemBean reorderReviewStockItemBean) {
		if(reorderReviewStockItemBean.isPopulated() == true) {
			return;
		}
		StockItem stockItem = reorderReviewStockItemBean.getStockItem();
		
		SupplierOrderLine sol = supplierOrderLineService.getByStockItemId(stockItem.getId(), SupplierOrderLineType.USER);
		if(sol == null) {
			sol = new SupplierOrderLine();
			sol.setStockItem(stockItem);
			sol.setSupplier(stockItem.getPreferredSupplier());
		}
		
		//Get authors
		List<Author> authors = authorService.findByStockItem(stockItem);
		stockItem.setAuthors(new HashSet<Author>(authors));
		
		//Get Category
		
		
		reorderReviewStockItemBean.setSupplierOrderLine(sol);
		reorderReviewStockItemBean.setOriginalSupplier(sol.getSupplier());
		
		//Get the sales figures for this stock, represented by MonthlySaleReportBean
		MonthlySaleReportBean monthlySaleReportBean = saleReportService.getMonthlySaleReportBean(stockItem);
		
		//Update reorder review bean, for display on reorderReviewItem.jsp
		reorderReviewStockItemBean.setPopulated(true);
		reorderReviewStockItemBean.setMonthlySaleReportBean(monthlySaleReportBean);
	}

	@Override
	@Transactional
	public void save(List<ReorderReviewStockItemBean> reorderReviewStockItemBeans) {

		for(ReorderReviewStockItemBean bean : reorderReviewStockItemBeans) {
			try {
				if(bean.isProcessed() == false ) continue; //Don't update lastReorderReviewDate
				
				if(bean.getSupplierOrderLine().getAmount() > 0) {
					SupplierOrderLine sol = bean.getSupplierOrderLine();	
				
					if(sol.getId() == null) { //New
						sol.setType(SupplierOrderLineType.USER);
					} 
					supplierOrderLineService.saveOrUpdate(sol);
				}  else {
					//Amount is 0 or less, remove any supplierorderlines
					supplierOrderLineService.removeSupplierOrderLine(bean.getStockItem(), SupplierOrderLineType.USER);
				}
				stockItemService.updateForReorderReview(bean.getStockItem());
				supplierOrderLineService.reconcileKeepInStock(bean.getStockItem(), false);
			} catch(NullPointerException e) {
				logger.error("Cannot process reorder review item", e);
				//this is a hack because invalid amounts, quantityinstock etc can have been entered so causing npe
				//Need validation TO-DO
				bean.setProcessed(false);
			} catch(Exception e) {
				logger.error("Cannot process reorder review item", e);
				//this is a hack because invalid amounts, quantityinstock etc can have been entered so causing npe
				//Need validation TO-DO
				bean.setProcessed(false);
			} 			
		}
	}

//	public Date getEndOfMonth(int month) {
//		Calendar startCalendar = new GregorianCalendar();
//		startCalendar.set(Calendar.YEAR, 2012);
//		startCalendar.set(Calendar.MONTH, month);
//		startCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
//		startCalendar.set(Calendar.HOUR_OF_DAY, 23);
//		startCalendar.set(Calendar.MINUTE, 59);
//		startCalendar.set(Calendar.SECOND, 59);
//		return startCalendar.getTime();
//	}
//	
//	
//	public Date getStartOfMonth(int month) {
//		Calendar startCalendar = new GregorianCalendar();
//		startCalendar.set(Calendar.YEAR, 2012);
//		startCalendar.set(Calendar.MONTH, month);
//		startCalendar.set(Calendar.DAY_OF_MONTH, 1);
//		startCalendar.set(Calendar.HOUR_OF_DAY, 0);
//		startCalendar.set(Calendar.MINUTE, 0);
//		startCalendar.set(Calendar.SECOND, 0);
//		return startCalendar.getTime();
//	}	
	
	
	@Override
	public Repository<SupplierOrder> getRepository() {
		return null;
	}

	@Override
	public List<ReorderReviewStockItemBean> getReorderReview() {
		Collection<StockItem> stockItems = reorderReviewRepository.getReorderReview();
		return getReorderReview(stockItems, new StockItemSearchBean());
	}
	
	/**
	 * Used by button Get Reorder Review on search Screen
	 */
	@Override
	public List<ReorderReviewStockItemBean> getReorderReview(Collection<StockItem> stockItems, StockItemSearchBean stockItemSearchBean) {
		//Set preferred supplier as this is the marker of which supplier to use when creating an order
		List<ReorderReviewStockItemBean> lines = new ArrayList<ReorderReviewStockItemBean>(stockItems.size());
		for(StockItem s : stockItems) {
			//Remove bookmarks publications
//			if(s.getPublisher().getId() == 725) continue; //Now done in sirimpl
			
			
//			if(stockItemSearchBean.getMarxismStatus() != null) {
//				if(stockItemSearchBean.getMarxismStatus() != 3) { //3 is not for marxism
//					if(s.getQuantityForMarxism() != null && s.getQuantityForMarxism() == -1) continue;
//				} else {
//					if(s.getQuantityForMarxism() != null && s.getQuantityForMarxism() != -1) continue;
//				}
//			}
			
			ReorderReviewStockItemBean rrsib = new ReorderReviewStockItemBean();
			rrsib.setStockItem(s);
			lines.add(rrsib);
		}
		return lines;
	}	
	
	

	@Override
	public void process(ReorderReviewStockItemBean originalBean, ReorderReviewStockItemBean processedBean) {
		originalBean.setSupplierOrderLine(processedBean.getSupplierOrderLine());
		originalBean.getSupplierOrderLine().setPriority(processedBean.getStockItem().getKeepInStockLevel());
		
		originalBean.getStockItem().setQuantityInStock(processedBean.getStockItem().getQuantityInStock());
		originalBean.getStockItem().setCategory(processedBean.getStockItem().getCategory());
		originalBean.getStockItem().setPutOnWebsite(processedBean.getStockItem().getPutOnWebsite());
		originalBean.getStockItem().setPutImageOnWebsite(processedBean.getStockItem().getPutImageOnWebsite());
		originalBean.getStockItem().setQuantityToKeepInStock(processedBean.getStockItem().getQuantityToKeepInStock());
		originalBean.getStockItem().setQuantityOnOrder(processedBean.getStockItem().getQuantityOnOrder());
		originalBean.getStockItem().setQuantityForMarxism(processedBean.getStockItem().getQuantityForMarxism());
		originalBean.getStockItem().setKeepInStockLevel(processedBean.getStockItem().getKeepInStockLevel());
		
		//Mark as processed
		originalBean.setProcessed(true);
	}
}
