package cz.muni.fi.gamepricecheckerbackend.config

import cz.muni.fi.gamepricecheckerbackend.filter.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Configuration of security filtering.
 *
 * @author Eduard Stefan Mlynarik
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationProvider: AuthenticationProvider,
    private val jwtTokenFilter: JwtFilter
) {

    @Throws(Exception::class)
    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf()
            .disable()
            .cors()
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/swagger-ui/**")
            .permitAll()
            .requestMatchers("/v1/api-docs/**")
            .permitAll()
            .requestMatchers("/auth/**")
            .permitAll()
            .requestMatchers("/game")
            .permitAll()
            .requestMatchers("/game/all")
            .permitAll()
            .requestMatchers("/game/count")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        return httpSecurity.build()
    }

}
