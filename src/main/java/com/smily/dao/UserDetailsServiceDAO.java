package com.smily.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.smily.bean.Users;
import com.smily.constants.Queries;
import com.smily.entity.User;
import com.smily.enums.ROLE;
import com.smily.mapper.Mapper;

@Component
public class UserDetailsServiceDAO implements UserDetailsService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private UsersMapper usersMapper;

	@Autowired
	private Mapper mapper;

	private BCryptPasswordEncoder bc = new BCryptPasswordEncoder(4);

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		User user = loadUserEntityByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username + " not found");
		}
		return new UserDetails() {
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				List<SimpleGrantedAuthority> auths = new ArrayList<>();
				auths.add(new SimpleGrantedAuthority(user.getRole()));
				return auths;
			}

			@Override
			public String getPassword() {
				return user.getPassword();
			}

			@Override
			public String getUsername() {
				return username;
			}

			@Override
			public boolean isAccountNonExpired() {
				return true;
			}

			@Override
			public boolean isAccountNonLocked() {
				return true;
			}

			@Override
			public boolean isCredentialsNonExpired() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return true;
			}
		};
	}

	public void saveUser(Users user) throws Exception {
		user.setPassword(bc.encode(user.getPassword()));
		user.setRole(ROLE.USER.toString());
		usersMapper.insert(user);

		// jdbcTemplate.update(Queries.INSERT_NEW_USER, user.getUsername(),
		// user.getPassword(), ROLE.USER.toString());
	}

	public void deleteUser(String username) throws Exception {
		jdbcTemplate.update(Queries.DELETE_USER_BY_USERNAME, username);
	}

	public User loadUserEntityByUsername(String username) {
		List<User> users = jdbcTemplate.query(Queries.LOAD_USER_BY_USERNAME, mapper::mapUser, username);
		if (users == null || users.size() < 1) {
			return null;
		} else {
			return users.get(0);
		}
	}

	public List<User> loadAllUsers() throws Exception {
		return jdbcTemplate.query(Queries.LOAD_ALL_USERS, mapper::mapUser);
	}

	public void updateUser(User user) throws Exception {
		jdbcTemplate.update(Queries.UPDATE_USER_BY_USERNAME, user.getPassword(), user.getRole(), user.getUsername());
	}

}
