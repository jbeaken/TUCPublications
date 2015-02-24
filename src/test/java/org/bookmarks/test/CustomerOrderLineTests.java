package org.bookmarks.test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
public class CustomerOrderLineTests extends AbstractBookmarkWebsiteTests {
   
    
    @Test
	public void searchOutOfStock() throws Exception {
        ResultActions actions = this.mockMvc.perform(post("/customerOrderLine/search")
            	.param("customerOrderLine.status", "OUT_OF_STOCK")
            );
        
        actions.andDo(print()); // action is logged into the console
        
        actions.andExpect(model().size(11));
        actions.andExpect(model().attributeExists("customerOrderStatusOptions"));
        actions.andExpect(model().attributeExists("customerOrderLineSearchBean"));
        actions.andExpect(model().attributeExists("customerOrderLineList"));
        actions.andExpect(model().attribute("searchResultCount", 1));
        
        actions.andExpect(view().name("searchCustomerOrderLines"));
    }  
    
    @Test
	public void searchByCustomerID() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/customerOrderLine/searchByCustomerID")
            	.param("id", "1")
            );
        
        actions.andDo(print()); // action is logged into the console
        
        actions.andExpect(model().size(10));
        actions.andExpect(model().attributeExists("customerOrderStatusOptions"));
        actions.andExpect(model().attributeExists("customerOrderLineSearchBean"));
        actions.andExpect(model().attributeExists("customerOrderLineList"));
        actions.andExpect(model().attribute("searchResultCount", 1));
        
        actions.andExpect(view().name("searchCustomerOrderLines"));
    }     
    
    
    @Test
	public void displaySearch() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/customerOrderLine/displaySearch"));
        actions.andDo(print()); // action is logged into the console
        
        actions.andExpect(model().size(2));
        actions.andExpect(model().attributeExists("customerOrderStatusOptions"));
        actions.andExpect(model().attributeExists("customerOrderLineSearchBean"));
        
        actions.andExpect(view().name("searchCustomerOrderLines"));
    }      
    

	@Override
	protected String getEntityName() {
		return "customerOrderLine";
	}

	@Override
	protected String getEntityViewName() {
		return "Invoice";
	}    
}
