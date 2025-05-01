package com.collegeelection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.collegeelection.repository.UserRepository;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/admin/**").hasRole("ADMIN")
	            .requestMatchers("/voter/**").hasRole("VOTER")
	            .anyRequest().permitAll()
	        )
	        .formLogin(form -> form
	            .loginPage("/login")
	            .successHandler((request, response, authentication) -> {
	                var authorities = authentication.getAuthorities();
	                String redirectURL = "/";

	                if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
	                    redirectURL = "/admin/admin_dashboard";
	                } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_VOTER"))) {
	                    redirectURL = "/voter/voter_dashboard";
	                }

	                response.sendRedirect(redirectURL);
	            })
	            .permitAll()
	        )
	        .logout(logout -> logout
	            .logoutUrl("/logout")
	            .logoutSuccessUrl("/login?logout")
	            .permitAll()
	        );

	    return http.build();
	}


	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
	    return username -> {
	        com.collegeelection.model.User user = userRepository.findByUsername(username);
	        if (user == null) throw new UsernameNotFoundException("User not found");

	        return org.springframework.security.core.userdetails.User
	                .withUsername(user.getUsername())
	                .password(user.getPassword())
	                .roles(user.getRole()) // assuming getRole() returns a string like "ADMIN"
	                .build();
	    };
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

}
