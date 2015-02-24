package org.bookmarks.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class AbstractSpringBooter {
	protected static ApplicationContext getApplicationContext() {
		System.setProperty("spring.profiles.active", "beans");
		return new ClassPathXmlApplicationContext("/spring/business-config.xml");
	}
}
