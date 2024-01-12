package com.swx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/home").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
			)
			.logout((logout) -> logout.permitAll());

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		
		//String bcryptPwd = "{bcrypt}$2a$10$e9jdOl2QxHuacq1t2ij6X.jF5Xl7LBUTt8uaHHcQHOjR0M5n3C.2m";
		//String bcryptPwd = "{bcrypt}" + password;
		
		UserDetails user =
				 User.builder()
					.username("wsong")
					.password("{bcrypt}$2a$10$LDBTYbnFj4fdLXYOlOv01.XH7CeHRwQdmPUw3cGEBUOMFZpPZYsQu")
					.roles("USER", "ADMIN")
					.build();
		return new InMemoryUserDetailsManager(user);
	}
}
