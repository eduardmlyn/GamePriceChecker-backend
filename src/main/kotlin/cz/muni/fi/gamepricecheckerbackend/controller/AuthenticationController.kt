package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationRequest
import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationResponse
import cz.muni.fi.gamepricecheckerbackend.model.wrapper.ResponseWrapper
import cz.muni.fi.gamepricecheckerbackend.service.AuthenticationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for authenticating user.
 *
 * @author Eduard Stefan Mlynarik
 */
@Tag(name = "Auth", description = "Provides API for getting access to the system's secured endpoints")
@RestController
@CrossOrigin
@RequestMapping(value = ["/auth"])
class AuthenticationController(private val authenticationService: AuthenticationService) {

    @Operation(
        summary = "Sign up new account",
        description = "Creates new account if there isn't a user with the given username."
    )
    @PostMapping("/register")
    fun register(
        @RequestBody request: AuthenticationRequest
    ): ResponseEntity<ResponseWrapper<AuthenticationResponse?>> {
        val userToken = authenticationService.register(request)
            ?: return ResponseEntity.badRequest().body(ResponseWrapper("Username already taken.", null))
        return ResponseEntity.status(201)
            .body(ResponseWrapper("Successfully signed up", userToken))
    }

    @Operation(summary = "Register user", description = "Registers user and returns user token.")
    @PostMapping("/login")
    fun authenticate(
        @RequestBody request: AuthenticationRequest
    ): ResponseEntity<ResponseWrapper<AuthenticationResponse?>> {
        val authStatus = authenticationService.authenticate(request)
        return if (authStatus == null) {
            ResponseEntity.status(401).body(ResponseWrapper("Bad credentials", null))
        } else {
            ResponseEntity.ok(ResponseWrapper("Successfully signed in", authStatus))
        }
    }
}
