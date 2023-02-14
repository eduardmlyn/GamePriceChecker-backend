package cz.muni.fi.gamepricecheckerbackend.config

import cz.muni.fi.gamepricecheckerbackend.security.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import kotlin.jvm.Throws

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val authenticationProvider: AuthenticationProvider,
    val jwtTokenFilter: JwtFilter
) {

    @Throws(Exception::class)
    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .requestMatchers("/auth/**")
            .permitAll()
            .requestMatchers("/game")
            .permitAll()
            .requestMatchers("/game/all")
            .permitAll()
            .requestMatchers("/game/test/all")// TODO REMOVE, ONLY FOR TESTING FEIGN
            .permitAll()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        return httpSecurity.build()
    }

}