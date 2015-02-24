package org.bookmarks.test;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public abstract class AbstractBookmarkWebsiteTests {
    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;
    
    protected MockHttpSession mockSession;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
			.webAppContextSetup(this.webApplicationContext)
			//.alwaysExpect(status().isOk())
			.build();
        this.mockSession = new MockHttpSession(webApplicationContext.getServletContext(), UUID.randomUUID().toString());
    }
    
    protected abstract String getEntityName();

	protected abstract String getEntityViewName();
	/*
    @Test
    public void displaySearch() throws Exception {
        this.mockMvc.perform(get("/" + getEntityName() + "/search"))
        	.andExpect(content().string(containsString("<title>Search " + getEntityViewName() + "</title>")));
    }
    
    @Test
    public void displayCreate() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/" + getEntityName() + "/create").accept(MediaType.ALL));
        actions.andDo(print()); // action is logged into the console
        actions.andExpect(content().string(containsString("<title>Create " + getEntityViewName() + "</title>")));
    }  
    
    @Test
    public void displayEdit() throws Exception {
    	this.mockMvc.perform(get("/" + getEntityName() + "/edit/1"))
		.andExpect(model().attributeExists(getEntityName()))
    	.andExpect(content().string(containsString("<title>Edit " + getEntityViewName() + "</title>")));
    }  
    
    @Test
    public void displayView() throws Exception {
    	this.mockMvc.perform(get("/" + getEntityName() + "/view/1"))
    		.andExpect(model().attributeExists(getEntityName()))
        	.andExpect(content().string(containsString("<title>View " + getEntityViewName() + "</title>")));
    }  
    */
}
