package cz.muni.fi.gamepricecheckerbackend.controller

import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDetailDTO
import cz.muni.fi.gamepricecheckerbackend.model.enums.SortBy
import cz.muni.fi.gamepricecheckerbackend.model.enums.Order
import cz.muni.fi.gamepricecheckerbackend.model.wrapper.ResponseWrapper
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import cz.muni.fi.gamepricecheckerbackend.util.ScheduledGameUpdate
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
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
@CrossOrigin
@RequestMapping(value = ["/game"])
class GameController(
    val gameService: GameService,
    val scheduledGameUpdate: ScheduledGameUpdate
) {

    @Operation(summary = "Get game details", description = "Returns details of game.")
    @GetMapping
    fun getGame(
        @Parameter(description = "Game Id", required = true) @RequestParam gameId: String
    ): ResponseEntity<ResponseWrapper<GameDetailDTO?>> {
        val game = gameService.getGameDetailsById(gameId)
            ?: return ResponseEntity.status(404).body(ResponseWrapper("Game with corresponding name not found", null))
        return ResponseEntity.ok(ResponseWrapper("Successfully found game by its name", game))
    }

    @Operation(summary = "Get all games", description = "Returns games according to page.")
    @GetMapping("/all")
    fun getAllGames(
        @Parameter(description = "Page", required = false) @RequestParam page: Int?,
        @Parameter(description = "Page size", required = true) @RequestParam pageSize: Int,
        @Parameter(description = "Sort by", required = false) @RequestParam sortBy: SortBy?,
        @Parameter(description = "Order direction", required = false) @RequestParam order: Order?
    ): ResponseEntity<ResponseWrapper<List<GameDTO>>> {
        return ResponseEntity.ok(
            ResponseWrapper(
                "Successfully returned page number ${page ?: 0}",
                gameService.getGames(page ?: 0, pageSize, sortBy ?: SortBy.NAME, order ?: Order.ASC)
            )
        )
    }

    @Operation(summary = "Get page count", description = "Returns number of pages.")
    @GetMapping("/count")
    fun getGamesCount(): ResponseEntity<ResponseWrapper<Long>> {
        return ResponseEntity.ok(ResponseWrapper("Success", gameService.getGamesCount()))
    }

    //----------------------TESTING ENDPOINTS----------------------\\
    @GetMapping("/init")
    fun init() {
        scheduledGameUpdate.init()
    }
}
