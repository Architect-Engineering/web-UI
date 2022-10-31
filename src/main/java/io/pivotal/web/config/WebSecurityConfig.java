package io.pivotal.web.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/**", "/registration").permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                //.anyRequest().fullyAuthenticated()
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/favicon.ico")
                .antMatchers("/webjars/**")
                .antMatchers("/images/**").antMatchers("/css/**").antMatchers("/js/**");
    }
}
