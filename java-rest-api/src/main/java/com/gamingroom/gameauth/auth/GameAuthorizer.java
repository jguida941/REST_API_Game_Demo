package com.gamingroom.gameauth.auth;
import io.dropwizard.auth.Authorizer;

/**
 * GameAuthorizer - Implements role-based authorization for the application.
 * 
 * This class works with Dropwizard's security framework to check if an authenticated
 * user has the required role to access specific endpoints.
 * 
 * The authorize method is called automatically by Dropwizard when a REST endpoint
 * is annotated with @RolesAllowed.
 */
public class GameAuthorizer implements Authorizer<GameUser> 
{
    /**
     * Checks if the authenticated user has the required role.
     * 
     * @param user The authenticated GameUser (never null when called by framework)
     * @param role The required role string (e.g., "USER", "ADMIN")
     * @return true if user has the role, false otherwise
     * 
     * Note: This method uses getRoles() (plural) which returns a Set<String>,
     * not getRole() (singular). The Set allows users to have multiple roles.
     */
    @Override
    public boolean authorize(GameUser user, String role) {
        // Check if user has roles and if the required role is in the user's role set
        // The null check prevents NullPointerException for users with no roles (like guest)
        return user.getRoles() != null && user.getRoles().contains(role);
    }
}