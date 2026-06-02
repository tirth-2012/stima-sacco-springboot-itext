package com.rutusoft.flowable.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.rutusoft.flowable.security.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	static Logger logger = Logger.getLogger(JWTAuthenticationFilter.class.getName());

	private AuthenticationManager authenticationManager;
	private static final String USER_PERMISSIONS = "Permissions";
	private static final String USER_LOGIN = "UserLogin";
	private static final String FIRST_NAME = "FirstName";
	private static final String LAST_NAME = "LastName";

	@Autowired
	private IdentityService identityService;


	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, ApplicationContext ctx) {
		this.authenticationManager = authenticationManager;
	}



	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		Authentication authenticationObject = null;
		try {
			logger.info("attemptAuthentication called");
			User user = new ObjectMapper().readValue(req.getInputStream(),
					User.class);
			logger.info("\n\n\n Principal found :" + req.getUserPrincipal());
			authenticationObject = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword(), new ArrayList()));

			if (authenticationObject != null) {
				logger.info("Authentication successful - Details :  " + authenticationObject.getDetails());
			} else {
				logger.info("Authentication object is null after trying to authenticate");
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception while attemptAuthentication : {}", e);
		} catch (Exception e) {

			logger.log(Level.SEVERE, "Exception while attemptAuthentication : ", e);
			throw new AuthorizationServiceException("Unauthorized user, Incorrect credential provided");
		}
		return authenticationObject;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		logger.info("successfulAuthentication called");
		String token = JWT.create().withSubject(auth.getName())
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).sign(HMAC512(SECRET.getBytes()));
		logger.info("JWT token - ");
		logger.info(token);
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);

		Gson gson = new Gson();

		JsonArray userDetailsArray = new JsonArray();
		JsonObject userLogin = new JsonObject();
		JsonObject userPermissions = new JsonObject();
		String userId = (String) auth.getPrincipal();

		User user = identityService.createUserQuery().userId(userId).singleResult();

		userLogin.addProperty(USER_LOGIN, user.getId());
		userLogin.addProperty(FIRST_NAME, user.getFirstName());
		userLogin.addProperty(LAST_NAME, user.getLastName());

		List<Group> groups = identityService.createGroupQuery().groupMember(user.getId()).list();

		// Iterator<Role> rolesItr = user.getRoles().iterator();
		List<String> permissions = new ArrayList<>();
		/*
		 * while(rolesItr.hasNext()) { Role role = rolesItr.next(); Iterator<Permission>
		 * permissionItr =role.getPermissions().iterator();
		 * while(permissionItr.hasNext()) {
		 * permissions.add(permissionItr.next().getPermissionname()); } }
		 */

		permissions.add("READ_ONLY");
		permissions.add("WRITE_ONLY");
		permissions.add("READ_WRITE_ONLY");

		String permissionsList = StringUtils.join(permissions);
		JsonElement permissionslement = gson.toJsonTree(permissionsList);
		userPermissions.add(USER_PERMISSIONS, permissionslement);

		userDetailsArray.add(userLogin);
		userDetailsArray.add(userPermissions);

		res.getWriter().write(userDetailsArray.toString());

	}
}