package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationResponse
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.enums.Order
import cz.muni.fi.gamepricecheckerbackend.model.enums.SortBy
import cz.muni.fi.gamepricecheckerbackend.model.wrapper.ResponseWrapper
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
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


/**
 * Controller for user interaction.
 *
 * @author Eduard Stefan Mlynarik
 */
@Tag(name = "User", description = "Provides API for User, additional access is needed")
@RestController
@CrossOrigin
@RequestMapping(value = ["/user"])
class UserController(private val userService: UserService) {
    @Operation(summary = "Delete user", description = "Deletes user from system.")
    @DeleteMapping("/opt-out")
    fun deleteUser(): ResponseEntity<ResponseWrapper<Boolean>> {
        userService.deleteUser() ?: return ResponseEntity.status(404).body(ResponseWrapper("User not found", false))

        return ResponseEntity.ok(ResponseWrapper("Success", true))
    }

    @Operation(summary = "Update user's username", description = "Updates user's username if valid.")
    @PutMapping("/edit-username")
    fun editUserUsername(
        @Parameter(description = "New username", required = true) @RequestParam username: String,
        @Parameter(
            description = "JWT of logged in user",
            required = true
        ) @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<ResponseWrapper<AuthenticationResponse?>> {
        val renewedToken = userService.editUsername(username, token)
            ?: return ResponseEntity.badRequest().body(ResponseWrapper("User not found/username already in use", null))

        return ResponseEntity.ok(ResponseWrapper("Success", renewedToken))
    }

    @Operation(summary = "(Un)Favorite game", description = "Adding/removing game from user's favorites")
    @PostMapping("/favorite")
    fun addGameToUser(
        @Parameter(description = "Game Id", required = true) @RequestParam gameId: String
    ): ResponseEntity<ResponseWrapper<Boolean>> {
        return ResponseEntity.ok(ResponseWrapper("Success", userService.changeGameToUserRelation(gameId)))
    }

    @Operation(summary = "Get favorite games", description = "Returns page of user's favorite games")
    @GetMapping("/favorites")
    fun getUserFavorites(
        @Parameter(description = "Page", required = false) @RequestParam page: Int = 0,
        @Parameter(description = "Page size", required = true) @RequestParam pageSize: Int,
        @Parameter(description = "Sort by", required = false) @RequestParam sortBy: SortBy = SortBy.NAME,
        @Parameter(description = "Order direction", required = false) @RequestParam order: Order = Order.ASC,
        @Parameter(description = "Name filter", required = false) @RequestParam filter: String = ""
    ): ResponseEntity<ResponseWrapper<List<GameDTO>>> {
        return ResponseEntity.ok(
            ResponseWrapper(
                "Successfully returned $page of user favorites", userService.getUserFavorites(
                    page,
                    pageSize,
                    sortBy,
                    order,
                    filter
                )
            )
        )
    }

    @Operation(summary = "Get favorite games count", description = "Returns the count of all user's favorite games")
    @GetMapping("/favorites/count")
    fun getUserFavoritesCount(
        @Parameter(description = "Name filter", required = false) @RequestParam filter: String = ""
    ): ResponseEntity<ResponseWrapper<Long>> {
        return ResponseEntity.ok(ResponseWrapper("Success", userService.getUserFavoriteCount(filter)))
    }

    @Operation(summary = "Log out user")
    @PostMapping("/logout")
    fun invalidateSession(
        @Parameter(
            description = "JWT of logged in user",
            required = true
        ) @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<ResponseWrapper<Boolean>> {
        userService.logout(token)
        return ResponseEntity.ok(ResponseWrapper("Successfully logged out", true))
    }
}
