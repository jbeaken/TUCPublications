package org.bookmarks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }


     @Override
     @Profile("dev")
     protected void configure(HttpSecurity http) throws Exception {
         http
             .authorizeRequests()
                 .antMatchers("/**").hasRole("USER")
                 .and()
             .formLogin()
                 .permitAll()
                 .and()
              // Example Remember Me Configuration
             .rememberMe().key("bookmarks343dfd!45%%");
     }
}
