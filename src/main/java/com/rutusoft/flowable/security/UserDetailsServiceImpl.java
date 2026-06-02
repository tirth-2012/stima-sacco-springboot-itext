package com.rutusoft.flowable.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static Logger logger = Logger.getLogger(UserDetailsServiceImpl.class.getName());

	@Autowired
	private IdentityService identityService;

	@Override
	public UserDetails loadUserByUsername(String loginid) {
		
		User user = identityService.createUserQuery().userId(loginid).singleResult();

		if (user == null) {
			throw new UsernameNotFoundException(loginid);
		}


		List<Group> groups = identityService.createGroupQuery().groupMember(loginid).list();
		logger.debug("groups for user : " + groups);

		return new org.springframework.security.core.userdetails.User(loginid, user.getPassword(), getAuthorities(groups));
	}

	
	private void getCamundaUserPrevilizes(List<Group> groups) {
		for(Group group : groups) {
		}
	}
	
	// Get the granted authorities for the
	/*
	private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
		return getGrantedAuthorities(getPermissions(roles));
	}
	*/
	
	private Collection<? extends GrantedAuthority> getAuthorities(List<Group> groups) {
		return getGrantedAuthorities(getPermissions(groups));
	}
	
	private List<String> getPermissions(List<Group> groups) {
		
		for(Group group: groups) {
		}
		
		return null;
	}


	private List<GrantedAuthority> getGrantedAuthorities(List<String> permissions) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (String permission : permissions) {
			authorities.add(new SimpleGrantedAuthority(permission));
		}
		return authorities;
	}

	/*
	private List<String> getPermissions(Collection<Role> roles) {

		List<String> permissions = new ArrayList<>();
		for (Role role : roles) {
			List<Permission> permissionsList = (List<Permission>) role.getPermissions();
			for (Permission permission : permissionsList) {
				permissions.add(permission.getPermissionname());
			}
		}

		logger.debug("===Permissions for user===" + permissions);
		return permissions;
	}
	*/
}