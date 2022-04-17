package org.bookmarks.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 * @author Jack
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-config.xml")
//@ActiveProfiles("jdbc")
//@ActiveProfiles({"dev", "integration"})
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ChipsTests extends AbstractBookmarkWebsiteTests {
    
//    @Test
	public void create() throws Exception {
    	this.mockMvc.perform(post("/chips/getOrders")
    			.param("firstname","firstname")
    			.param("branch.id","1")
    			.param("tradeUnion.id","1")
    			.param("isMember", "true")
    			.param("lastname","lastname"))
    			
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/isjIssue/displaySearch"));
//				.andExpect(model().size(1))
//				.andExpect(model().attributeExists("name"))
//				.andExpect(flash().attributeCount(1))
//				.andExpect(flash().attribute("message", "success!"));
	}    
    

	@Override
	protected String getEntityName() {
		return "ISJIssue";
	}

	@Override
	protected String getEntityViewName() {
		return "ISJ Issue";
	}    
}
