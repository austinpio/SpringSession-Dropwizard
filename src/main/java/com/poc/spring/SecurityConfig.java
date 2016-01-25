package com.poc.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
		authenticationManagerBuilder.inMemoryAuthentication()
		.withUser("user")
		.password("password")
		.roles("ADMIN");
		
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception{
		httpSecurity
		.httpBasic()
		.and()
		.authorizeRequests()
		.antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
		.antMatchers(HttpMethod.GET,"/hello-world").hasRole("ADMIN")
		.and()
		.csrf().disable();
	}
	
}
