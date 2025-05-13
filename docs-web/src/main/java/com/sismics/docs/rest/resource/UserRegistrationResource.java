package com.sismics.docs.rest.resource;

import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.dao.*;
import com.sismics.docs.core.dao.criteria.UserRegistrationCriteria;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.model.jpa.UserRegistration;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Path("/registration")
public class UserRegistrationResource extends BaseResource {

    /**
     * Create a user in guest model.
     *
     * @param username User's registration name
     * @param email    E-Mail
     * @return Response
     * @api {put} /registration
     */
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response submitRequest(
            @FormParam("username") String username,
            @FormParam("email") String email) {

        // 判断是否为guest创建
        if (!authenticate() || !principal.isGuest()) {
            throw new ForbiddenClientException();
        }

        // Validate input data
        username = ValidationUtil.validateLength(username, "username", 3, 50);
        ValidationUtil.validateUsername(username, "username");
        email = ValidationUtil.validateLength(email, "email", 1, 100);
        ValidationUtil.validateEmail(email, "email");

        // Create registration request
        UserRegistration registration = new UserRegistration();
        registration.setUsername(username);
        registration.setEmail(email);
        registration.setStatus("pending");

        UserRegistrationDao registrationDao = new UserRegistrationDao();
        try {
            registrationDao.create(registration);
        } catch (Exception e) {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingUsername", "Username already used", e);
            } else {
                throw new ServerException("UnknownError", "Unknown server error", e);
            }
        }

        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }


    /**
     * Get all registrations.
     *
     * @return Response
     * @api {get} /registration
     */
    @GET
    public Response getRegistrations() {
        if (!authenticate() || !hasBaseFunction(BaseFunction.ADMIN)) {
            throw new ForbiddenClientException();
        }

        JsonArrayBuilder userRequests = Json.createArrayBuilder();

        UserRegistrationDao registrationDao = new UserRegistrationDao();
        List<UserRegistration> requests = registrationDao.findByCriteria(new UserRegistrationCriteria(), new SortCriteria(null, null));
        for (UserRegistration userRegistration : requests) {
            userRequests.add(Json.createObjectBuilder()
                    .add("id", userRegistration.getId())
                    .add("username", userRegistration.getUsername())
                    .add("email", userRegistration.getEmail())
                    .add("status", userRegistration.getStatus()));
        }

        // Build JSON response
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("registrations", userRequests);
        return Response.ok().entity(response.build()).build();
    }


    @POST
    @Path("/approval/{username: [a-zA-Z0-9_@.-]+}")
    public Response processRequest(
            @PathParam("username") String userRegistrationName,
            @FormParam("approve") Boolean approve) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        // Validate the input data
        userRegistrationName = ValidationUtil.validateLength(userRegistrationName, "username", 3, 50);
        ValidationUtil.validateUsername(userRegistrationName, "username");

        UserRegistrationDao registrationDao = new UserRegistrationDao();
        UserRegistration registration = registrationDao.getUserRegistrationByURN(userRegistrationName);
        if (registration == null) {
            throw new ClientException("RequestNotFound", "Registration request not found");
        }
        String email = registration.getEmail();
        String password = "password";
        Long storageQuota = 10000L;

        if (registration.getStatus().equals("pending")) {
            if (approve != null) {
                if (approve) {

                    registration.setStatus("accept");
                    // Create the actual user
                    User user = new User();
                    user.setRoleId(Constants.DEFAULT_USER_ROLE);
                    user.setUsername(userRegistrationName);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.setStorageQuota(storageQuota);
                    user.setOnboarding(true);

                    UserDao userDao = new UserDao();
                    try {
                        userDao.create(user, principal.getId());
                    } catch (Exception e) {
                        throw new ClientException("RegistrationError", "Error creating user");
                    }
                } else {
                    registration.setStatus("reject");
                }
            }

        }

        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }
}