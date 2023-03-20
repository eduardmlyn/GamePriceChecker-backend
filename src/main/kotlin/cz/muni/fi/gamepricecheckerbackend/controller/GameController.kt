package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.client.SteamGameDetailClient
import cz.muni.fi.gamepricecheckerbackend.client.SteamGameListClient
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDetailDTO
import cz.muni.fi.gamepricecheckerbackend.model.steam.SteamAllGamesResponse
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import cz.muni.fi.gamepricecheckerbackend.util.ChromeDriverFactory
import cz.muni.fi.gamepricecheckerbackend.util.EAScrapper
import cz.muni.fi.gamepricecheckerbackend.util.HumbleBundleScrapper
import cz.muni.fi.gamepricecheckerbackend.model.wrapper.ResponseWrapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for getting game information.
 *
 * @author Eduard Stefan Mlynarik
 */
@Tag(name = "Game", description = "Provides Api for Games in the system")
@RestController
@RequestMapping(value = ["/game"])
class GameController(
    val gameService: GameService,
    val steamGameListClient: SteamGameListClient,
    val steamGameDetailClient: SteamGameDetailClient,
    val eaScrapper: EAScrapper,
    val humbleBundleScrapper: HumbleBundleScrapper,
    val chromeDriverFactory: ChromeDriverFactory
) {

    @Operation(summary = "Get game details", description = "Returns details of game.")
    @GetMapping
    fun getGame(
        @Parameter(description = "Game name", required = true) @RequestParam gameName: String
    ): ResponseEntity<ResponseWrapper<GameDetailDTO?>> {
        val game = gameService.getGameDetailsByName(gameName)
            ?: return ResponseEntity.status(404).body(ResponseWrapper("Game with corresponding name not found", null))
        return ResponseEntity.ok(ResponseWrapper("Successfully found game by its name", game))
    }

    @Operation(summary = "Get all games", description = "Returns games according to page.")
    @GetMapping("/all")
    fun getAllGames(
        @Parameter(description = "Page", required = false) @RequestParam page: Int?
    ): ResponseEntity<ResponseWrapper<List<GameDTO>>> {
        return ResponseEntity.ok(
            ResponseWrapper(
                "Successfully returned page number ${page ?: 0}",
                gameService.getGames(page ?: 0)
            )
        )
    }

    @Operation(summary = "Get page count", description = "Returns number of pages.")
    @GetMapping("/page-count")
    fun getPageCount(): ResponseEntity<ResponseWrapper<Long>> {
        return ResponseEntity.ok(ResponseWrapper("Success", gameService.getPageCount()))
    }

    //----------------------TESTING ENDPOINTS----------------------\\
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
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            eaScrapper.scrape(webDriver)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
        }
        return "success"
    }

    @GetMapping("/scrape/humble-bundle")
    fun getScrapedHumbleBundleGames(): Any {
        val webDriver = chromeDriverFactory.getChromeDriverInstance()
        try {
            humbleBundleScrapper.scrape(webDriver)
        } finally {
            chromeDriverFactory.destroyChromeDriverInstance(webDriver)
        }
        return "success"
    }
}
