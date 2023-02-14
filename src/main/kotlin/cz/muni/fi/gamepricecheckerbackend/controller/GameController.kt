package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.client.SteamClient
import cz.muni.fi.gamepricecheckerbackend.facade.GameFacade
import cz.muni.fi.gamepricecheckerbackend.model.Game
import cz.muni.fi.gamepricecheckerbackend.model.SteamAllGamesResponse
import cz.muni.fi.gamepricecheckerbackend.wrapper.ResponseWrapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Tag(name = "Game", description = "Provides Api for Game")
@RestController
@RequestMapping(value = ["/game"])
class GameController(val gameFacade: GameFacade, val steamClient: SteamClient) {

    // is this endpoint needed if get all games sends all game data?
    @Operation(summary = "Get game details", description = "Returns details of game.")
    @GetMapping
    fun getGame(
        @Parameter(description = "Game name", required = true) @RequestParam gameName: String
    ): ResponseEntity<ResponseWrapper<Game?>> {
        val game = gameFacade.getGameByName(gameName)
            ?: return ResponseEntity.status(404).body(ResponseWrapper("Game with corresponding name not found", null))
        return ResponseEntity.ok(ResponseWrapper("Successfully found game by its name", game))
    }

    // Change return type to only basic info about game?
    // TODO implement paging and change description
    @Operation(summary = "Get all games", description = "Returns games according to page.")
    @GetMapping("/all")
    fun getAllGames(
        @Parameter(description = "Page", required = false) @RequestParam page: Int?
    ): ResponseEntity<ResponseWrapper<List<Game>>> {
        return ResponseEntity.ok(
            ResponseWrapper(
                message = "Successfully returned page number ${page ?: 1}",
                gameFacade.getGames(page ?: 1)
            )
        )
    }

    // ADD ENDPOINT FOR GETTING THE NUMBER OF ALL GAMES/ OR DO GET PAGECOUNT

    @Operation(summary = "Adds game", description = "Adds new game, allowed by admin only.")
    @PostMapping("/add")
    fun addGame() {
        TODO()
        // implement authorization, allow only admin to add games
    }

    @Operation(summary = "Add link to game", description = "Adds new link to game.")
    @PutMapping("/add-link")
    fun addLinkToGame() {
        TODO()
    }

    @GetMapping("/test/all")
    fun getAllSteamGames(): SteamAllGamesResponse {
        return steamClient.getAllGames()
    }
}
