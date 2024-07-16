package com.volook.apiGateway.auth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.protobuf.util.JsonFormat;

import userManager.UserOuterClass.Role;

@Configuration
@EnableWebSecurity
public class JwtPolicy {
	@Autowired
	private JwtValidator jwtValidator;
	@Value("${allowed-cors-origin}")
	private String ALLOWED_CORS_ORIGIN;
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
		return authConfig.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.csrf((csrf) -> csrf.disable())
				.cors((cors) -> cors.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				/*.authorizeHttpRequests(authorize -> 
					authorize
					//PUBLIC
					.requestMatchers(HttpMethod.GET, "/flights").permitAll()
					.requestMatchers(HttpMethod.POST, "/user/login").permitAll()
					.requestMatchers(HttpMethod.POST, "/user/signin").permitAll()
					//LOYALTY
		            .requestMatchers("/loyaltyUser/*").hasRole(Role.LOYALTY_CUSTOMER.toString())
		            //ADMIN
		            .requestMatchers("/admin/*").hasRole(Role.ADMIN.toString())
		            //CUSTOMER
		            .anyRequest().authenticated())*/
				//.addFilter(new JwtValidator(authenticationManager), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtValidator, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	/*@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}*/
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins(ALLOWED_CORS_ORIGIN)
				.allowedMethods("GET","PUT","POST","DELETE","OPTIONS");
			}
		};
	}
	
	@Bean
	public ProtobufJsonFormatHttpMessageConverter protobufHttpMessageConverter() {
		JsonFormat.Printer printer = JsonFormat.printer().omittingInsignificantWhitespace();
		JsonFormat.Parser parser = JsonFormat.parser().ignoringUnknownFields();
		return new ProtobufJsonFormatHttpMessageConverter(parser, printer,null);
	}
}
