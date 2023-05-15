package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.BaseIntegrationTest
import cz.muni.fi.gamepricecheckerbackend.GamePriceCheckerBackendApplication
import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationRequest
import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationResponse
import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.model.enums.Role
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder

/**
 *
 * @author Eduard Stefan Mlynarik
 */
internal class AuthenticationServiceTest: BaseIntegrationTest() {
    private val userRepository: UserRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val jwtService: JwtService = mockk()
    private val authenticationManager: AuthenticationManager = mockk()
    private val authenticationService: AuthenticationService = AuthenticationService(
        userRepository,
        passwordEncoder,
        jwtService,
        authenticationManager
    )
    private val user: User = mockk()
    private val authenticationRequest = AuthenticationRequest("test", "test")

    @Test
    fun `register taken username`() {
        every { userRepository.findUserByUserName("test") } returns user

        val result = authenticationService.register(authenticationRequest)

        Assertions.assertNull(result)
    }

    @Test
    fun `register valid not taken username`(){
        val newUser = User(authenticationRequest.username, "encoded test", Role.USER)

        every { userRepository.findUserByUserName("test") } returns null
        every { passwordEncoder.encode(authenticationRequest.password) } returns "encoded test"
        every { userRepository.save(newUser) } returns newUser
        every { jwtService.generateToken(newUser) } returns "token"

        val result = authenticationService.register(authenticationRequest)

        verifySequence {
            userRepository.findUserByUserName("test")
            passwordEncoder.encode(authenticationRequest.password)
            userRepository.save(newUser)
            jwtService.generateToken(newUser)
        }
        Assertions.assertEquals(result, AuthenticationResponse("token"))
    }

    @Test
    fun `authenticate invalid credentials`() {
        val authEx = mockk<AuthenticationException>()
        val authToken = UsernamePasswordAuthenticationToken(authenticationRequest.username, authenticationRequest.password)
        every { authenticationManager.authenticate(authToken) } throws authEx

        val result = authenticationService.authenticate(authenticationRequest)

        Assertions.assertNull(result)
    }

    @Test
    fun `authenticate valid credentials`() {
        val authToken = UsernamePasswordAuthenticationToken(authenticationRequest.username, authenticationRequest.password)
        val authentication = mockk<Authentication>()
        every { authenticationManager.authenticate(authToken) } returns authentication
        every { userRepository.findUserByUserName(authenticationRequest.username) } returns user
        every { jwtService.generateToken(user) } returns "token"

        val result = authenticationService.authenticate(authenticationRequest)

        verifySequence {
            authenticationManager.authenticate(authToken)
            userRepository.findUserByUserName(authenticationRequest.username)
            jwtService.generateToken(user)
        }
        Assertions.assertEquals(result, AuthenticationResponse("token"))
    }
}