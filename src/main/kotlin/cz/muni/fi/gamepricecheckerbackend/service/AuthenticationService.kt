package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationRequest
import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationResponse
import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.model.enums.Role
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import cz.muni.fi.gamepricecheckerbackend.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
@Transactional(readOnly = true)
class AuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun register(userRequest: AuthenticationRequest): AuthenticationResponse? {
        if (userRepository.findUserByUserName(userRequest.username) != null) {
            return null
        }
        val user = User(
            userName = userRequest.username,
            password = passwordEncoder.encode(userRequest.password),
            role = Role.USER
        )
        val savedUser = userRepository.save(user)
        val jwtToken = jwtService.generateToken(savedUser)
        return AuthenticationResponse(jwtToken)
    }

    fun authenticate(authenticationRequest: AuthenticationRequest): AuthenticationResponse? {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authenticationRequest.username,
                    authenticationRequest.password
                )
            )
        } catch (e: AuthenticationException) {
            return null
        }
        val user = userRepository.findUserByUserName(authenticationRequest.username) ?: return null
        val jwtToken = jwtService.generateToken(user)
        return AuthenticationResponse(jwtToken)
    }
}
