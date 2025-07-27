package com.gamingroom.gameauth;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamingroom.gameauth.auth.GameAuthenticator;
import com.gamingroom.gameauth.auth.GameAuthorizer;
import com.gamingroom.gameauth.auth.GameUser;

import com.gamingroom.gameauth.controller.GameUserRESTController;
import com.gamingroom.gameauth.controller.RESTClientController;

// Import Halo game components
import com.gamingroom.gameauth.halo.controller.HaloGameResource;
import com.gamingroom.gameauth.halo.service.HaloGameService;

import com.gamingroom.gameauth.healthcheck.AppHealthCheck;
import com.gamingroom.gameauth.healthcheck.HealthCheckController;



public class GameAuthApplication extends Application<Configuration> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GameAuthApplication.class);

	@Override
	public void initialize(Bootstrap<Configuration> b) {
	}

	@Override
	public void run(Configuration c, Environment e) throws Exception 
	{
		
		LOGGER.info("Registering REST resources");
 
		// FIXED register GameUserRESTController (based on BasicAuth Security Example)
		// FIXED: Create io.dropwizard.client.JerseyClientBuilder instance and give it io.dropwizard.setup.Environment reference (based on BasicAuth Security Example)
		

		//This uses Rest API to get the data from the server
		//JerseyClientBuilder is a class that builds a client to connect to the server
		
		Client client = new JerseyClientBuilder(e).build("REST Client"); //This uses Rest API to get the data from the server
		//Now I need to give it to io.dropwizard.setup.Environment reference
		
		
		e.jersey().register(new RESTClientController(client)); //RESTClientController is a class that uses the client to get the data from the server
		e.jersey().register(new GameUserRESTController(e.getValidator())); //GameUserRESTController is a class that uses the validator to validate the data from the server

		// Register Halo game endpoints
		// TODO: In production, pass a real DataSource from database configuration
		// For now, using null which means in-memory storage only
		HaloGameService haloService = new HaloGameService(null);
		e.jersey().register(new HaloGameResource(haloService));
		LOGGER.info("Registered Halo game endpoints at /halo/*");

		// Application health check
		e.healthChecks().register("APIHealthCheck", new AppHealthCheck(client));

		// Run multiple health checks
		e.jersey().register(new HealthCheckController(e.healthChecks()));
		
		//Setup Basic Security
		e.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<GameUser>()
                .setAuthenticator(new GameAuthenticator())
                .setAuthorizer(new GameAuthorizer())
                .setRealm("App Security")
                .buildAuthFilter()));
        e.jersey().register(new AuthValueFactoryProvider.Binder<>(GameUser.class));
        e.jersey().register(RolesAllowedDynamicFeature.class);
	}

	public static void main(String[] args) throws Exception {
		new GameAuthApplication().run(args);
	}
}