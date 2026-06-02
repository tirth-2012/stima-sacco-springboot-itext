package com.rutusoft.flowable.security;

import java.util.ArrayList;
import java.util.List;

import com.rutusoft.flowable.exception.UserAuthenticationException;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger =
			LoggerFactory.getLogger(CustomAuthenticationProvider.class);

	private final IdentityService identityService;

	public CustomAuthenticationProvider(IdentityService identityService) {
		this.identityService = identityService;
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			{

		String userId = authentication.getName();
		String password = authentication.getCredentials().toString();

		logger.info("Authenticating user : {}", userId);

		User user = identityService.createUserQuery()
				.userId(userId)
				.singleResult();

		if (user == null) {
			throw new UserAuthenticationException(
					"Userid or password incorrect"
			);
		}

		boolean authenticated =
				identityService.checkPassword(userId, password);

		if (!authenticated) {
			throw new UserAuthenticationException(
					"Incorrect username or password"
			);
		}

		// ✅ Map Flowable groups to Spring authorities
		List<SimpleGrantedAuthority> authorities =
				identityService.createGroupQuery()
						.groupMember(userId)
						.list()
						.stream()
						.map(group ->
								new SimpleGrantedAuthority(
										"ROLE_" + group.getId().toUpperCase()
								)
						)
						.toList();

		logger.info("User {} authenticated with roles {}",
				userId, authorities);

		return new UsernamePasswordAuthenticationToken(
				userId,
				null,          // ✅ credentials cleared
				authorities
		);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class
				.isAssignableFrom(authentication);
	}
}