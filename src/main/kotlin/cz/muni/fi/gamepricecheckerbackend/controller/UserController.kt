package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader
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
@CrossOrigin
@RequestMapping(value = ["/user"])
class UserController(val userService: UserService) {
    @Operation(summary = "Delete user", description = "Deletes user from system.")
    @DeleteMapping("/opt-out")
    fun deleteUser(
        @Parameter(description = "JWT of logged user", required = true) @RequestHeader(HttpHeaders.AUTHORIZATION) token: String
    ): ResponseEntity<ResponseWrapper<User?>> {
        val user = userService.deleteUser()

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


    // TODO add description
    @Operation(summary = "(Un)Favorite game", description = "Adding/removing game from user's favorites")
    @PostMapping("/favorite")
    fun addGameToUser(
        @Parameter(description = "Game Id", required = true) @RequestParam gameId: String
    ): Boolean {
        return userService.changeGameToUserRelation(gameId)
    }

    // TODO add description
    @Operation()
    @GetMapping("/favorites")
    fun getUserFavorites(
        @Parameter(description = "Page", required = false) @RequestParam page: Int?,
        @Parameter(description = "Page size", required = true) @RequestParam pageSize: Int
    ): ResponseEntity<ResponseWrapper<List<GameDTO>>> {
        return ResponseEntity.ok(ResponseWrapper("", userService.getUserFavorites(page ?: 0, pageSize)))
    }

    // TODO add description
    @Operation()
    @GetMapping("/favorites/count")
    fun getUserFavoritesCount(): ResponseEntity<ResponseWrapper<Int>> {
        return ResponseEntity.ok(ResponseWrapper("Success", userService.getUserFavoriteCount()))
    }

    // TODO implement
    @Operation
    @PostMapping("/logout")
    fun invalidateSession(
        @Parameter(description = "JWT of logged in user", required = true) @RequestHeader(HttpHeaders.AUTHORIZATION) jwtToken: String
    ): ResponseEntity<ResponseWrapper<Any?>> {
        return ResponseEntity.ok(ResponseWrapper("Not implemented yet", null))
    }
}
