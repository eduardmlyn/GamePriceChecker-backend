package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.client.SteamGameDetailClient
import cz.muni.fi.gamepricecheckerbackend.client.SteamGameListClient
import cz.muni.fi.gamepricecheckerbackend.facade.GameFacade
import cz.muni.fi.gamepricecheckerbackend.model.Game
import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamAllGamesResponse
import cz.muni.fi.gamepricecheckerbackend.util.Scrapper
import cz.muni.fi.gamepricecheckerbackend.wrapper.ResponseWrapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
class GameController(
    val gameFacade: GameFacade,
    val steamGameListClient: SteamGameListClient,
    val steamGameDetailClient: SteamGameDetailClient,
    val scrapper: Scrapper,
    val webDriver: ChromeDriver
) {

    // is this endpoint needed if get all games sends all game data?
    @Operation(summary = "Get game details", description = "Returns details of game.")
    @GetMapping
    fun getGame(
        @Parameter(description = "Game name", required = true) @RequestParam gameId: String
    ): ResponseEntity<ResponseWrapper<Game?>> {
        val game = gameFacade.getGameById(gameId)
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
        // implement authorization, allow only admin to add games, remove, only internal creation?
    }

    @Operation(summary = "Add link to game", description = "Adds new link to game.")
    @PutMapping("/add-link")
    fun addLinkToGame() {
        TODO()
    }

    @GetMapping("/test/all")
    fun getAllSteamGames(): SteamAllGamesResponse {
        return steamGameListClient.getAllGames()
    }

    @GetMapping("/test/detail/{appId}")
    fun getSteamGameDetail(
        @Parameter(description = "Application id", required = true) @PathVariable appId: Int
    ): Any {
        return steamGameDetailClient.getGameDetails(appId, "CZ")
    }

    @GetMapping("/scrape/ea-games")
    fun getScrapedEaGames(): Any {
        val data = scrapper.scrapeEa(webDriver)
//        webDriver.close()
        return data
    }
}
