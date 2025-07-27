package com.gamingroom.gameauth.controller;
 
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.gamingroom.gameauth.representations.GameUserInfo;
 
@Produces(MediaType.TEXT_PLAIN)
@Path("/client/")
public class RESTClientController 
{
    private Client client;
 
    public RESTClientController(Client client) {
        this.client = client;
    }
     
 // FIXED:: Add annotation for GET and Path for gameusers 
 @GET // Get request allows the client to retrieve data from the server
 @Path("/gameusers") // Path is the URL of the resource
 public String getGameUsers() // getGameUsers returns a response object which is a list of game users
 {
        //Do not hard code in your application
        WebTarget webTarget = client.target("http://localhost:8080/gameusers");
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        @SuppressWarnings("rawtypes")
        ArrayList gameusers = response.readEntity(ArrayList.class);
        return gameusers.toString();
    }
     
    @GET
    @Path("/gameusers/{id}")
    public String getGameUserById(@PathParam("id") int id)
    {
        //Do not hard code in your application
        WebTarget webTarget = client.target("http://localhost:8080/gameusers/"+id);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        GameUserInfo gameUserInfo = response.readEntity(GameUserInfo.class);
        return gameUserInfo.toString();
    }
}