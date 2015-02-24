package org.bookmarks.test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;


/**
 * @author Jack
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-config.xml")
@ActiveProfiles("test")
//@ActiveProfiles({"dev", "integration"})
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class InvoiceTests extends AbstractBookmarkWebsiteTests {
    

    
    @Test
	public void initNotAccountHolder() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/invoice/init")
        	.param("customerId", "1")
        );
        actions.andDo(print()); // action is logged into the console
        actions.andExpect(model().size(5));
        actions.andExpect(model().attributeExists("invoice"));
        actions.andExpect(model().attributeExists("newBalance"));
        actions.andExpect(model().attribute("info", "Customer is not an account holder, either edit customer and check account box, or 'set at paid' if you are certain customer has paid"));
        actions.andExpect(view().name("createInvoice"));
    }
    
    @Test
	public void initAccountHolder() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/invoice/init")
        	.param("customerId", "2")
        );
        actions.andDo(print()); // action is logged into the console
        actions.andExpect(model().size(4));
        actions.andExpect(model().attributeExists("invoice"));
        actions.andExpect(model().attribute("newBalance", new BigDecimal(23.00)));
        actions.andExpect(view().name("createInvoice"));
    }    
    
    @Test
	public void displaySearch() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/invoice/displaySearch"));
        actions.andDo(print()); // action is logged into the console
//        actions.andExpect(content().string(containsString("<title>Create " + getEntityViewName() + "</title>")));
        actions.andExpect(model().size(5));
        actions.andExpect(model().attributeExists("invoiceSearchBean"));
        actions.andExpect(model().attributeExists("deliveryTypeList"));
        actions.andExpect(model().attributeExists("invoiceList"));
        actions.andExpect(model().attributeExists("customerOrderStatusOptions"));
        actions.andExpect(model().attributeExists("searchResultCount"));
        
        actions.andExpect(view().name("searchInvoices"));
    }    
    

	@Override
	protected String getEntityName() {
		return "invoice";
	}

	@Override
	protected String getEntityViewName() {
		return "Invoice";
	}    
}
