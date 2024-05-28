package com.swx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		//http.csrf(Customizer.withDefaults());
		http.csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
			.csrf((csrf) -> csrf.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()));
		http
			.authorizeHttpRequests((requests) -> requests
				//.requestMatchers("/", "/home").permitAll()
				//.requestMatchers("/item/**").hasRole("USER")
				//.requestMatchers(HttpMethod.POST, "/item/update").hasRole("USER")
				.anyRequest().authenticated()
			)
			.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
			)
			.logout(LogoutConfigurer::permitAll);

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		
		UserDetails user =
				 User.builder()
					.username("wsong")
					.password("{bcrypt}$2a$10$LDBTYbnFj4fdLXYOlOv01.XH7CeHRwQdmPUw3cGEBUOMFZpPZYsQu")
					.roles("USER", "ADMIN")
					.build();
		return new InMemoryUserDetailsManager(user);
	}

}