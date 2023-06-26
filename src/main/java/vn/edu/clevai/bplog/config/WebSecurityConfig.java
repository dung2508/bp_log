package vn.edu.clevai.bplog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import vn.edu.clevai.common.api.filter.CloudGatewayTokenRequestFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final long MAX_AGE_SECS = 3600;

	private final CloudGatewayTokenRequestFilter cloudGatewayFilter;

	public WebSecurityConfig(CloudGatewayTokenRequestFilter cloudGatewayFilter) {
		this.cloudGatewayFilter = cloudGatewayFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable()
				.addFilterBefore(cloudGatewayFilter, BasicAuthenticationFilter.class)
				.authorizeRequests()
				.antMatchers("/unauthenticated/**").permitAll()
				.antMatchers("/checking/**").permitAll()
				.antMatchers("/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		httpSecurity.cors();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
				"/configuration  /security", "/swagger-ui.html", "/webjars/**");

	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Collections.singletonList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "OPTIONS", "DELETE"));
		configuration.setAllowedHeaders(
				Arrays.asList("Access-Control-Request-Method", "Origin", "Content-Type", "Authorization"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(MAX_AGE_SECS);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
