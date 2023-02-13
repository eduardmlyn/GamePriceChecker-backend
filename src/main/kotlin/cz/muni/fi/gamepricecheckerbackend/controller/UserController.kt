package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.facade.UserFacade
import cz.muni.fi.gamepricecheckerbackend.model.User
import cz.muni.fi.gamepricecheckerbackend.model.UserRequest
import cz.muni.fi.gamepricecheckerbackend.wrapper.ResponseWrapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Tag(name = "User", description = "Provides API for User")
@RestController
@RequestMapping(value = ["/user"])
// TODO remove userService and implement userFacade
class UserController(val userFacade: UserFacade) {

    @Deprecated(message = "Use method from AuthController instead")
    @Operation(summary = "Login user", description = "Attempts to Log in a user.")
    @PostMapping("/sign-in")
    fun loginUser(
        @Parameter @RequestBody userRequest: UserRequest
        ): ResponseEntity<ResponseWrapper<User?>> {
        // TODO add jwt and cookie
        println("Logged in")
        println(userRequest)
        val user = userFacade.getUserByUsername(userRequest.userName)
        if (user == null || userRequest.password != user.password) {
            return ResponseEntity.status(401).body(ResponseWrapper("Bad combination of username/password", data = null))
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @Operation(summary = "Logout user", description = "Logs out the user.")
    @PostMapping("/sign-out")
    fun logoutUser(
        @Parameter @RequestBody user: User
    ): ResponseEntity<ResponseWrapper<Any?>> {
        // TODO cookie + jwt
        println("Logged out")
        return ResponseEntity.ok().body(ResponseWrapper("Success", data = null))
    }

    // TODO create a response model with message, status, data
    @Deprecated(message = "Use method from AuthController instead")
    @Operation(summary = "Create user", description = "Creates a user if username is not already taken.")
    @PostMapping("/sign-up")
    fun createUser(
        @Parameter @RequestBody userRequest: UserRequest,
    ): ResponseEntity<ResponseWrapper<User?>> {
        val user = userFacade.createUser(userRequest.userName, userRequest.password)
        if (user != null) {
            return ResponseEntity.ok(ResponseWrapper("Success", data = user))
        }
        return ResponseEntity.badRequest().body(ResponseWrapper("Username already in use", data = null))
    }

    @Operation(summary = "Delete user", description = "Deletes user from system.")
    @DeleteMapping("/opt-out")
    fun deleteUser(
        @Parameter @RequestBody userName: String
    ): ResponseEntity<ResponseWrapper<User?>> {
        // TODO work with cookie/jwt?
        val user = userFacade.deleteUser(userName)

        // TODO add exception check
        return ResponseEntity.ok(ResponseWrapper("Success", data = user))
    }

    @Operation(summary = "Update user's username", description = "Updates user's username if valid.")
    @PutMapping("/editUsername")
    fun editUserUsername(
        @RequestParam(required = true) username: String
    ): ResponseEntity<ResponseWrapper<User?>> {
        // TODO change this
        val user = userFacade.editUsername(username)
            ?: return ResponseEntity.badRequest().body(ResponseWrapper("User not found/username already in use", data = null))
        return ResponseEntity.ok(ResponseWrapper("Success", data = user))
    }

    // TODO add changing password? -> might be needed email and more complications

}