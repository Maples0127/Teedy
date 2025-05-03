package com.sismics.docs.rest.resource;

//import com.sismics.docs.core.dao.UserRegistrationRequestDao;
//import com.sismics.docs.core.model.jpa.UserRegistrationRequest;

import com.sismics.docs.core.constant.Constants;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.dao.UserRegistrationDao;
import com.sismics.docs.core.dao.criteria.UserRegistrationCriteria;
import com.sismics.docs.core.dao.dto.UserRegistrationDto;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.model.jpa.UserRegistration;
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

import java.util.List;

@Path("/registration")
public class UserRegistrationResource extends BaseResource {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response submitRequest(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("email") String email,
            @FormParam("storage_quota") String storageQuotaStr) throws Exception {
        if (!authenticate() || !principal.isGuest()) {
            throw new ForbiddenClientException();
        }


        // Validate input data
        username = ValidationUtil.validateLength(username, "username", 3, 50);
        ValidationUtil.validateUsername(username, "username");
        password = ValidationUtil.validateLength(password, "password", 8, 50);
        email = ValidationUtil.validateLength(email, "email", 1, 100);
        Long storageQuota = ValidationUtil.validateLong(storageQuotaStr, "storage_quota");
        ValidationUtil.validateEmail(email, "email");

        // Create registration request
        UserRegistration registration = new UserRegistration();
        registration.setUsername(username);
        registration.setPassword(password);
        registration.setEmail(email);
        registration.setStorageQuota(storageQuota);

        UserRegistrationDao registrationDao = new UserRegistrationDao();
//        String registrationId = registrationDao.create(registration);
        try {
            registrationDao.create(registration);
        } catch (Exception e) {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingUsername", "Login already used", e);
            } else {
                throw new ServerException("UnknownError", "Unknown server error", e);
            }
        }


        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }

    @GET
    @Path("pending")
    public Response getPendingRequests() {
        if (!authenticate() || !hasBaseFunction(BaseFunction.ADMIN)) {
            throw new ForbiddenClientException();
        }

        JsonArrayBuilder userRequests = Json.createArrayBuilder();
//        SortCriteria sortCriteria = new SortCriteria(sortColumn, asc);

        UserRegistrationDao requestDao = new UserRegistrationDao();
        List<UserRegistrationDto> requests = requestDao.findByCriteria(new UserRegistrationCriteria(), null);


        for (UserRegistrationDto userRegistrationDto : requests) {
            userRequests.add(Json.createObjectBuilder()
                    .add("id", userRegistrationDto.getId())
                    .add("username", userRegistrationDto.getUsername())
                    .add("email", userRegistrationDto.getEmail())
//                    .add("totp_enabled", userRequestDto.getTotpKey() != null)
//                    .add("storage_quota", userRequestDto.getStorageQuota())
//                    .add("storage_current", userRequestDto.getStorageCurrent())
                    .add("create_date", userRegistrationDto.getCreateTimestamp())
                    .add("disabled", userRegistrationDto.getDisableTimestamp() != null));
        }

        // Build JSON response
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("userRequests", userRequests);
        return Response.ok().entity(response.build()).build();
    }




    @POST
    @Path("/approval/{username: [a-zA-Z0-9_@.-]+}")
    public Response processRequest(
            @PathParam("username") String userRegistrationName,
            @FormParam("password") String password,
            @FormParam("email") String email,
            @FormParam("storage_quota") String storageQuotaStr,
            @FormParam("approve") Boolean approve) {

        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);

        // Validate the input data
        userRegistrationName = ValidationUtil.validateLength(userRegistrationName, "username", 3, 50);
        ValidationUtil.validateUsername(userRegistrationName, "username");
        password = ValidationUtil.validateLength(password, "password", 8, 50);
        email = ValidationUtil.validateLength(email, "email", 1, 100);
        Long storageQuota = ValidationUtil.validateLong(storageQuotaStr, "storage_quota");
        ValidationUtil.validateEmail(email, "email");

        UserRegistrationDao registrationDao = new UserRegistrationDao();
        UserRegistration registration = registrationDao.getUserRegistrationByURN(userRegistrationName);
        if (registration == null) {
            throw new ClientException("RequestNotFound", "Registration request not found");
        }


        if (approve != null) {
            if (approve) {

                registration.setStatus(UserRegistration.Status.ACCEPTED);
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
                registration.setStatus(UserRegistration.Status.REJECTED);
            }
        }
        registration = registrationDao.update(registration,principal.getId());


        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }
}