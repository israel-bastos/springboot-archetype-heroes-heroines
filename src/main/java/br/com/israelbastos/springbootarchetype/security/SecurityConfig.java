package br.com.israelbastos.springbootarchetype.security;

import br.com.israelbastos.springbootarchetype.service.SystemUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String PASSWORD_TEST = "groot";

    private final SystemUserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // disabling for convenience... the other implementation is the one for real applications.
                .csrf().disable()
                .authorizeHttpRequests()
                .antMatchers("/characters/admin/**").hasRole("ADMIN")
                .antMatchers("/characters/**").hasRole("USER")
                .and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String password = passwordEncoder.encode(PASSWORD_TEST);

        // it's not a regular implementation, but in this case we are using like this, and you will
        // need to copy this encrypts password in the password column, past in the database passing
        // the ROLE too, passing the name and the username.
        System.out.println(password + " |final");

        // we can have different sources to access the authentication information. Here we are
        // using both ways.
        auth.inMemoryAuthentication()
                .withUser("tony.stark")
                .password(password)
                .roles("USER", "ADMIN")
                .and()
                .withUser("peter.parker")
                .password(password)
                .roles("USER");

        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }
}