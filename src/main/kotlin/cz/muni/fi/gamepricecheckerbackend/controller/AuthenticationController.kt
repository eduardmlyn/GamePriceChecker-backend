package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationRequest
import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationResponse
import cz.muni.fi.gamepricecheckerbackend.model.UserRequest
import cz.muni.fi.gamepricecheckerbackend.service.AuthenticationService
import cz.muni.fi.gamepricecheckerbackend.wrapper.ResponseWrapper
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@RestController
@RequestMapping(value = ["/auth"])
class AuthenticationController(val authenticationService: AuthenticationService) {

    @Operation() // TODO swagger documentation
    @PostMapping("/sign-up")
    fun register(
        @RequestBody request: UserRequest
    ): ResponseEntity<ResponseWrapper<AuthenticationResponse>> {
        return ResponseEntity.ok(ResponseWrapper("Successfully signed up", authenticationService.register(request)))
    }

    @Operation
    @PostMapping("/sing-in")
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

    @Operation
    @PostMapping("/sign-out")
    fun invalidateSession(
        @RequestBody jwtToken: String
    ): ResponseEntity<ResponseWrapper<Any?>> {
        return ResponseEntity.ok(ResponseWrapper("Not implemented yet", null))
    }
}