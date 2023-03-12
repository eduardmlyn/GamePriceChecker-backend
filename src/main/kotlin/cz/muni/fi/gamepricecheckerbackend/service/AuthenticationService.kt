package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.Role
import cz.muni.fi.gamepricecheckerbackend.model.User
import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationRequest
import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationResponse
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import cz.muni.fi.gamepricecheckerbackend.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
class AuthenticationService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val jwtService: JwtService,
    val authenticationManager: AuthenticationManager
) {

    fun register(userRequest: AuthenticationRequest): AuthenticationResponse? {
        if (userRepository.findUserByUserName(userRequest.username) != null) {
            return null
        }
        val user = User(
            userName = userRequest.username,
            password = passwordEncoder.encode(userRequest.password),
            role = Role.USER
        )
        userRepository.save(user)
        val jwtToken = jwtService.generateToken(user)
        return AuthenticationResponse(jwtToken)
    }

    fun authenticate(authenticationRequest: AuthenticationRequest): AuthenticationResponse? {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.username,
                authenticationRequest.password
            )
        )
        val user = userRepository.findUserByUserName(authenticationRequest.username)
        val jwtToken = user?.let { jwtService.generateToken(it) }
        return jwtToken?.let { AuthenticationResponse(it) }
    }

    fun invalidate(jwtToken: String) {

    }
}