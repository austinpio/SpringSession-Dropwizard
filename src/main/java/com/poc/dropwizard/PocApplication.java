package com.poc.dropwizard;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.eclipse.jetty.server.session.SessionHandler;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.poc.spring.SecurityConfig;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class PocApplication extends Application<PocConfiguration>{
	
	public static void main(String args[]) throws Exception{
		new PocApplication().run(args);
	}
	
	@Override
	public String getName(){
		return "hello-world";
	}
	
	@Override
	public void initialize(Bootstrap<PocConfiguration> bootstrap) {
		
	};

	@Override
	public void run(PocConfiguration configuration, Environment environment) throws Exception {
		
		//initialize spring context
		AnnotationConfigWebApplicationContext parent=new AnnotationConfigWebApplicationContext();
		AnnotationConfigWebApplicationContext ctx=new AnnotationConfigWebApplicationContext();
		
		parent.refresh();
		parent.getBeanFactory().registerSingleton("configuration", configuration);
		parent.registerShutdownHook();
		parent.start();
		
		//real main app context has to link to parent context
		ctx.setParent(parent);
		ctx.register(SecurityConfig.class);
		
		//scan all APIS
		ctx.scan("com.poc.dropwizard");
		ctx.scan("com.poc.spring");
		
		ctx.refresh();
		ctx.registerShutdownHook();
		ctx.start();
		
		//link spring to embedded jetty
		environment.servlets().addServletListeners(new ContextLoaderListener(ctx));
		
		//activate spring security filter
		Dynamic filterRegistration=environment.servlets().addFilter("springSecurityFilterChain",DelegatingFilterProxy.class);
		filterRegistration.setInitParameter("listener-class", ContextLoaderListener.class.toString());
		filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

		//link spring-session to embedded jetty
		//TODO - link spring session 
		environment.servlets().setSessionHandler(new SessionHandler());
		
		final PocResource pocResource=new PocResource(configuration.getTemplate(), configuration.getDefaultName());
		environment.jersey().register(pocResource);
	}

}
