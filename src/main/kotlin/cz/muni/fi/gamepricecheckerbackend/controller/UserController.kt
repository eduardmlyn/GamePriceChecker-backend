package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.service.UserService
import cz.muni.fi.gamepricecheckerbackend.model.wrapper.ResponseWrapper
import cz.muni.fi.gamepricecheckerbackend.security.JwtService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * Controller for user interaction.
 *
 * @author Eduard Stefan Mlynarik
 */
@Tag(name = "User", description = "Provides API for User")
@RestController
@CrossOrigin
@RequestMapping(value = ["/user"])
class UserController(val userService: UserService, val jwtService: JwtService) {
    @Operation(summary = "Delete user", description = "Deletes user from system.")
    @DeleteMapping("/opt-out")
    fun deleteUser(
        @Parameter(description = "JWT of logged user", required = true) @RequestHeader(HttpHeaders.AUTHORIZATION) token: String
    ): ResponseEntity<ResponseWrapper<User?>> {
        val userName = jwtService.extractUsername(token)
        val user = userService.deleteUser(userName)

        // TODO add exception check
        return ResponseEntity.ok(ResponseWrapper("Success", data = user))
    }

    // TODO rework
    @Operation(summary = "Update user's username", description = "Updates user's username if valid.")
    @PutMapping("/edit-username")
    fun editUserUsername(
        @Parameter(description = "JWT of logged user", required = true) username: String
    ): ResponseEntity<ResponseWrapper<Nothing?>> {
        // TODO redo
        userService.editUsername(username)
            ?: return ResponseEntity.badRequest().body(ResponseWrapper("User not found/username already in use", data = null))
        return ResponseEntity.ok(ResponseWrapper("Success", data = null))
    }

    // TODO add changing password? -> might be needed email and more complications

    // TODO implement
    @Operation
    @PostMapping("/logout")
    fun invalidateSession(
        @Parameter(description = "JWT of logged in user", required = true) @RequestHeader(HttpHeaders.AUTHORIZATION) jwtToken: String
    ): ResponseEntity<ResponseWrapper<Any?>> {
        return ResponseEntity.ok(ResponseWrapper("Not implemented yet", null))
    }
}
