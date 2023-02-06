package cz.muni.fi.gamepricecheckerbackend.controllers

import cz.muni.fi.gamepricecheckerbackend.models.User
import cz.muni.fi.gamepricecheckerbackend.models.UserRequest
import cz.muni.fi.gamepricecheckerbackend.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Tag(name = "User", description = "Provides API for User")
@RestController
@RequestMapping(value = ["/user"])
class UserController(val userService: UserService) {

    @Operation(summary = "Login user", description = "Attempts to Log in a user.")
    @PostMapping("/sign-in")
    fun loginUser(
        @Parameter @RequestBody userRequest: UserRequest
        ): ResponseEntity<HttpStatus> {
        println("Logged in")
        println(userRequest)
        val user = userService.findByUsername(userRequest.username)
        if (user == null || userRequest.password != user.password) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @Operation(summary = "Logout user", description = "Logs out the user.")
    @PostMapping("/sign-out")
    fun logoutUser(
        @Parameter @RequestBody user: User
    ): ResponseEntity<HttpStatus> {
        println("Logged out")
        return ResponseEntity(HttpStatus.OK)
    }

    @Operation(summary = "Create user", description = "Creates a user if username is not already taken.")
    @PostMapping("/sign-up")
    fun createUser(
        @Parameter @RequestBody userRequest: UserRequest,
    ): ResponseEntity<User> {
        val user = userService.createUser(userRequest.username, userRequest.password)
        if (user != null) {
            return ResponseEntity.ok(user)
        }
        return ResponseEntity.badRequest().body("Username already in use")
    }

    @Operation(summary = "Delete user", description = "Deletes user from system.")
    @DeleteMapping("/opt-out")
    fun deleteUser(
        @Parameter @RequestBody userName: String
    ): ResponseEntity<User> {
        val user = userService.deleteUser(userName)

        return ResponseEntity.ok(user)
    }
}