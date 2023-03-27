package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.service.UserService
import cz.muni.fi.gamepricecheckerbackend.model.wrapper.ResponseWrapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


/**
 * Controller for user interaction.
 *
 * @author Eduard Stefan Mlynarik
 */
@Tag(name = "User", description = "Provides API for User")
@RestController
@RequestMapping(value = ["/user"])
class UserController(val userService: UserService) {
    @Operation(summary = "Delete user", description = "Deletes user from system.")
    @DeleteMapping("/opt-out")
    fun deleteUser(
        @Parameter @RequestBody userName: String
    ): ResponseEntity<ResponseWrapper<User?>> {
        // TODO work with cookie/jwt?
        val user = userService.deleteUser(userName)

        // TODO add exception check
        return ResponseEntity.ok(ResponseWrapper("Success", data = user))
    }

    @Operation(summary = "Update user's username", description = "Updates user's username if valid.")
    @PutMapping("/editUsername")
    fun editUserUsername(
        @RequestParam(required = true) username: String
    ): ResponseEntity<ResponseWrapper<Nothing?>> {
        // TODO redo
        userService.editUsername(username)
            ?: return ResponseEntity.badRequest().body(ResponseWrapper("User not found/username already in use", data = null))
        return ResponseEntity.ok(ResponseWrapper("Success", data = null))
    }

    // TODO add changing password? -> might be needed email and more complications

}