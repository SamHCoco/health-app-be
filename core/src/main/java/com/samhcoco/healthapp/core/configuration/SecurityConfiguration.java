//package com.samhcoco.healthapp.core.configuration;
//
//import lombok.val;
//import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
//import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
//import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
//import org.springframework.security.core.session.SessionRegistryImpl;
//import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
//import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
//
//@Configuration
//@EnableWebSecurity
//@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {
//
//    private static final String USER = "user";
//    private static final String ADMIN = "admin";
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) {
//        val grantedAuthorityMapper = new SimpleAuthorityMapper();
//        val authenticationProvider = keycloakAuthenticationProvider();
//        authenticationProvider.setGrantedAuthoritiesMapper(grantedAuthorityMapper);
//        auth.authenticationProvider(authenticationProvider);
//    }
//
//    @Bean
//    @Override
//    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
//        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
//    }
//
//    @Bean
//    public KeycloakSpringBootConfigResolver keycloakConfigResolver () {
//        return new KeycloakSpringBootConfigResolver();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
//
//        http.cors().and().csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/register**", "/register").permitAll()
//                .antMatchers("/health/**").hasAnyRole(ADMIN, USER)
//                .antMatchers("/user/**").hasAnyRole(ADMIN, USER)
//                .anyRequest().permitAll();
//
//        http.headers().frameOptions().disable();
//    }
//}
// todo - fix
