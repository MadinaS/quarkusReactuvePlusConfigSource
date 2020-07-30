package org.acme.config.resources;


import org.acme.config.jwt.TokenUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;


@Path("auth")
//@RequestScoped
public class AuthResource {


    @ConfigProperty(name = "angular.server", defaultValue = "localhost")
    String angular_server;


    @POST
    @Path("login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(
            // @MultipartForm UserLoginForm userLoginForm,
//            @Context SecurityContext ctx
    ) throws Exception {
        //--- check user ---
        // ...
        String JWT = null;
        String chatJWT = null;
        try {
            JWT = TokenUtils.generateTokenString(
                    "user",
                    "userLogin",
                    "firstName",
                    "surname"
            );

            chatJWT = TokenUtils.generateChatTokenString("userNickname");

        } catch (Exception e) {
            System.out.println("=== *** >>>>>>>>>>>>>>>>>>>>>>>> Volume is not reachable <<<<<<<<<<<<<<<<<<<<< : ");
            return Response.serverError().status(500).entity(new HashMap<String, String>().put("error", "Error Message")).build();
        }

        NewCookie sidCookie = new NewCookie(
                "SESSIONID",
                JWT,
                "/",
                this.angular_server,
                1,
                "",
                999999,
                false);

        NewCookie chatCookie = new NewCookie(
                "chatToken",
                chatJWT,
                "/",
                this.angular_server,
                1,
                "",
                999999,
                false);

        return Response.ok("hello")
                .cookie(sidCookie)
                .cookie(chatCookie)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT)
                .build();

    }



    @POST
    @Path("logout")
    @RolesAllowed({"user"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response logoutUser(
//            @CookieParam("SESSIONID") Cookie cookie
    ) throws IOException, NullPointerException, NotSupportedException, InvalidKeySpecException, NoSuchAlgorithmException {
        return Response.ok("abgemeldet").build();
    }
}
