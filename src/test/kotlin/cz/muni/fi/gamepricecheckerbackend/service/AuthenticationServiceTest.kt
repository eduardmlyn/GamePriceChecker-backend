package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import cz.muni.fi.gamepricecheckerbackend.security.JwtService
import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder

/**
 *
 * @author Eduard Stefan Mlynarik
 */
internal class AuthenticationServiceTest {
    private val userRepository: UserRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val jwtService: JwtService = mockk()
    private val authenticationManager: AuthenticationManager = mockk()
    val authenticationService: AuthenticationService =
        AuthenticationService(userRepository, passwordEncoder, jwtService, authenticationManager)

    // TODO! should be easy ^ this is good way, look userservicetest
    @Test
    fun register() {
    }

    @Test
    fun authenticate() {
    }

    @Test
    fun getUserRepository() {
    }

    @Test
    fun getPasswordEncoder() {
    }

    @Test
    fun getJwtService() {
    }

    @Test
    fun getAuthenticationManager() {
    }
}