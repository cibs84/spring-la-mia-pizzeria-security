package com.corsojava.pizzeria.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		// GENERA IN CONSOLE LA PAROLA HASHATA CHE GLI PASSO 
		System.out.println(passwordEncoder().encode("user"));
		
		http.authorizeHttpRequests()
			.requestMatchers("/pizze/create", "/pizze/edit/**").hasAuthority("ADMIN")	//per creare o modificare una pizza bisogna essere ADMIN
			.requestMatchers(HttpMethod.POST, "/pizze/delete/**").hasAuthority("ADMIN")	//per fare il POST su /pizze/delete/{id} (richiesto per eliminare una pizza) bisogna essere ADMIN
			.requestMatchers("/ingredienti", "/ingredienti/**").hasAuthority("ADMIN")	//per accedere agli ingredienti bisogna essere ADMIN
			.requestMatchers("/pizze", "/pizze/**").hasAnyAuthority("USER","ADMIN")		//per accedere all'elenco pizza (/pizze) o dettaglio pizza (/pizze/{id}) bisogna esser USER o ADMIN
			.requestMatchers("/**").permitAll()	//chiunque può accedere alla Home
			.and().formLogin()	//abilita il supporto al form login (auto generato)
	        .and().logout()		//abilita il supporto al form logout (auto generato)
	    	.and().exceptionHandling()
	    	.accessDeniedPage("/access-denied.html"); //pagina personalizzata in caso di accesso negato
		
		return http.build();	
	}
	
	@Bean
	DatabaseUserDetailsService userDetailsService() {
		return new DatabaseUserDetailsService();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
	    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}  
	  
	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	 
	    authProvider.setUserDetailsService(userDetailsService());
	    authProvider.setPasswordEncoder(passwordEncoder());
	 
	    return authProvider;
	}  
}
