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
public class CustomerTests extends AbstractBookmarkWebsiteTests {
    
    @Test
	public void displayAdd() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/customer/add"));
        actions.andDo(print()); // action is logged into the console
//        actions.andExpect(content().string(containsString("<title>Create " + getEntityViewName() + "</title>")));
        actions.andExpect(model().size(2));
        actions.andExpect(model().attributeExists("customer"));
        actions.andExpect(model().attributeExists("customerTypeList"));
        actions.andExpect(view().name("addCustomer"));
    }
    
    @Test
	public void add() throws Exception {
        ResultActions actions = this.mockMvc.perform(post("/customer/add")
        	.param("firstName", "firstName")
        	.param("lastName", "lastName")
        	.param("bookmarksAccount.accountHolder", "false")
        );
        actions.andDo(print()); // action is logged into the console
        actions.andExpect(model().size(1));
        actions.andExpect(model().attributeExists("customer"));
        actions.andExpect(view().name("redirectSearchCustomers"));
    }
    
    @Test
	public void displaySearch() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/customer/displaySearch"));
        actions.andDo(print()); // action is logged into the console
//        actions.andExpect(content().string(containsString("<title>Create " + getEntityViewName() + "</title>")));
        actions.andExpect(model().size(3));
        actions.andExpect(model().attributeExists("customerSearchBean"));
        actions.andExpect(model().attributeExists("customerTypeList"));
        actions.andExpect(view().name("searchCustomers"));
    }    
    

	@Override
	protected String getEntityName() {
		return "customer";
	}

	@Override
	protected String getEntityViewName() {
		return "Customers";
	}    
}
