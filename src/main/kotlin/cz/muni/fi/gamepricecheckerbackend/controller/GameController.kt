package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.client.SteamGameDetailClient
import cz.muni.fi.gamepricecheckerbackend.client.SteamGameListClient
import cz.muni.fi.gamepricecheckerbackend.model.Game
import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamAllGamesResponse
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import cz.muni.fi.gamepricecheckerbackend.util.EAScrapper
import cz.muni.fi.gamepricecheckerbackend.util.HumbleBundleScrapper
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
 * Controller for getting game information.
 *
 * @author Eduard Stefan Mlynarik
 */
@Tag(name = "Game", description = "Provides Api for Game")
@RestController
@RequestMapping(value = ["/game"])
class GameController(
    val gameService: GameService,
    val steamGameListClient: SteamGameListClient,
    val steamGameDetailClient: SteamGameDetailClient,
    val EAScrapper: EAScrapper,
    val humbleBundleScrapper: HumbleBundleScrapper,
    val webDriver: ChromeDriver
) {

    // TODO rework by getting details of price snapshots
    @Operation(summary = "Get game details", description = "Returns details of game.")
    @GetMapping
    fun getGame(
        @Parameter(description = "Game name", required = true) @RequestParam gameId: String
    ): ResponseEntity<ResponseWrapper<Game?>> {
        val game = gameService.getGame(gameId)
            ?: return ResponseEntity.status(404).body(ResponseWrapper("Game with corresponding name not found", null))
        return ResponseEntity.ok(ResponseWrapper("Successfully found game by its name", game))
    }

    // TODO return only base info - no binded pricesnapshot...
    @Operation(summary = "Get all games", description = "Returns games according to page.")
    @GetMapping("/all")
    fun getAllGames(
        @Parameter(description = "Page", required = false) @RequestParam page: Int?
    ): ResponseEntity<ResponseWrapper<List<Game>>> {
        return ResponseEntity.ok(
            ResponseWrapper(
                "Successfully returned page number ${page ?: 1}",
                gameService.getGames(page ?: 1)
            )
        )
    }

    @Operation(summary = "Get page count", description = "Returns number of pages.")
    @GetMapping("/page-count")
    fun getPageCount(): ResponseEntity<ResponseWrapper<Long>> {
        return ResponseEntity.ok(ResponseWrapper("Success", gameService.getPageCount()))
    }

    // TODO remove
    @Operation(summary = "Adds game", description = "Adds new game, allowed by admin only.")
    @PostMapping("/add")
    fun addGame() {
        TODO()
        // implement authorization, allow only admin to add games, remove, only internal creation?
    }

    // TODO remove
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
        return EAScrapper.scrape(webDriver)
    }

    @GetMapping("/scrape/humble-bundle")
    fun getScrapedHumbleBundleGames(): Any {
        return humbleBundleScrapper.scrape(webDriver)
    }
}
