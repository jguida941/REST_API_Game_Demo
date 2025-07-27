package com.gamingroom.gameauth.controller;
 
import io.dropwizard.auth.Auth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.gamingroom.gameauth.auth.GameUser;
import com.gamingroom.gameauth.dao.GameUserDB;
import com.gamingroom.gameauth.representations.GameUserInfo;
// FIXED: Add path annotation for gameusers 
@Path("/gameusers") // Path is the URL of the resource
@Produces(MediaType.APPLICATION_JSON) //JSON is the format of the response

public class GameUserRESTController {
 
    private final Validator validator;
 
    public GameUserRESTController(Validator validator) {
        this.validator = validator;
    }
 
    @PermitAll // All authenticated users can see the list
    @GET
    public Response getGameUsers(@Auth GameUser user) {
        return Response.ok(GameUserDB.getGameUsers()).build();
    }
 
    // FIXED: Add RolesAllowed annotation for USER and ADMIN only - GUEST and PLAYER get 403
    @RolesAllowed({"USER", "ADMIN"}) // Only USER and ADMIN roles can access individual user details
    @GET
    @Path("/{id}")
    public Response getGameUserById(@PathParam("id") Integer id,@Auth GameUser user) {
    	//Could validate here if user is watching his record
    	/*if(id != user.getId()){
    			//Not allowed
    	}*/
        GameUserInfo gameUserInfo = GameUserDB.getGameUser(id);
        if (gameUserInfo != null)
            return Response.ok(gameUserInfo).build();
        else
            return Response.status(Status.NOT_FOUND).build();
    }


    // FIXED: Add RolesAllowed annotation for ADMIN based on BasicAuth Security Example 
    @RolesAllowed("ADMIN") // Admin role is allowed to access the resource

    @POST
    public Response createGameUser(GameUserInfo gameUserInfo, @Auth GameUser user) throws URISyntaxException {
        // validation
        Set<ConstraintViolation<GameUserInfo>> violations = validator.validate(gameUserInfo);
        GameUserInfo e = GameUserDB.getGameUser(gameUserInfo.getId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<GameUserInfo> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }
            return Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
        }
        if (e != null) {
            GameUserDB.updateGameUser(gameUserInfo.getId(), gameUserInfo);
            return Response.created(new URI("/gameusers/" + gameUserInfo.getId()))
                    .build();
        } else
            return Response.status(Status.NOT_FOUND).build();
    }
 
    @RolesAllowed({"USER", "ADMIN"}) // Only USER and ADMIN can update users
    @PUT
    @Path("/{id}")
    public Response updateGameUserById(@PathParam("id") Integer id, GameUserInfo gameUserInfo, @Auth GameUser user) {
        // validation
        Set<ConstraintViolation<GameUserInfo>> violations = validator.validate(gameUserInfo);
        GameUserInfo e = GameUserDB.getGameUser(gameUserInfo.getId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<GameUserInfo> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }
            return Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
        }
        if (e != null) {
            gameUserInfo.setId(id);
            GameUserDB.updateGameUser(id, gameUserInfo);
            return Response.ok(gameUserInfo).build();
        } else
            return Response.status(Status.NOT_FOUND).build();
    }
 
    @RolesAllowed("ADMIN") // Only ADMIN can delete users
    @DELETE
    @Path("/{id}")
    public Response removeGameUserById(@PathParam("id") Integer id, @Auth GameUser user) {
        GameUserInfo gameUserInfo = GameUserDB.getGameUser(id);
        if (gameUserInfo != null) {
            GameUserDB.removeGameUser(id);
            return Response.ok().build();
        } else
            return Response.status(Status.NOT_FOUND).build();
    }
}