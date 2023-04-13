package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.model.wrapper.ResponseWrapper
import cz.muni.fi.gamepricecheckerbackend.service.BlackListService
import cz.muni.fi.gamepricecheckerbackend.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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
class UserController(private val userService: UserService) {
    @Operation(summary = "Delete user", description = "Deletes user from system.")
    @DeleteMapping("/opt-out")
    fun deleteUser(): ResponseEntity<ResponseWrapper<User?>> {
        val user = userService.deleteUser() ?: return ResponseEntity.status(404).body(ResponseWrapper("User not found", null))

        return ResponseEntity.ok(ResponseWrapper("Success", user))
    }

    @Operation(summary = "Update user's username", description = "Updates user's username if valid.")
    @PutMapping("/edit-username")
    fun editUserUsername(
        @Parameter(description = "New username", required = true) username: String
    ): ResponseEntity<ResponseWrapper<Boolean>> {
        // TODO redo
        userService.editUsername(username)
            ?: return ResponseEntity.badRequest().body(ResponseWrapper("User not found/username already in use", false))
        return ResponseEntity.ok(ResponseWrapper("Success", true))
    }

    @Operation(summary = "(Un)Favorite game", description = "Adding/removing game from user's favorites")
    @PostMapping("/favorite")
    fun addGameToUser(
        @Parameter(description = "Game Id", required = true) @RequestParam gameId: String
    ): Boolean {
        return userService.changeGameToUserRelation(gameId)
    }

    // TODO add description
    @Operation(summary = "Get favorite games", description = "Returns page of user's favorite games")
    @GetMapping("/favorites")
    fun getUserFavorites(
        @Parameter(description = "Page", required = false) @RequestParam page: Int?,
        @Parameter(description = "Page size", required = true) @RequestParam pageSize: Int
    ): ResponseEntity<ResponseWrapper<List<GameDTO>>> {
        return ResponseEntity.ok(ResponseWrapper("", userService.getUserFavorites(page ?: 0, pageSize)))
    }

    @Operation(summary = "Get favorite games count", description = "Returns the count of all user's favorite games")
    @GetMapping("/favorites/count")
    fun getUserFavoritesCount(): ResponseEntity<ResponseWrapper<Int>> {
        return ResponseEntity.ok(ResponseWrapper("Success", userService.getUserFavoriteCount()))
    }

    @Operation
    @PostMapping("/logout")
    fun invalidateSession(): ResponseEntity<ResponseWrapper<Boolean>> {
        blackListService.addToBlackList()
        return ResponseEntity.ok(ResponseWrapper("Successfully logged out", true))
    }
}
