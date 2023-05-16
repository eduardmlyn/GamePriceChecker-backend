package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.entity.PriceSnapshot
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.model.event.TaskDoneEvent
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import org.slf4j.Logger
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Component
class TaskDoneEventListener(val gameService: GameService, val logger: Logger) {
    private val sellers: MutableSet<Seller> = mutableSetOf()
    private val allSellers: Set<Seller> = mutableSetOf(Seller.STEAM, Seller.EA_GAMES, Seller.HUMBLE_BUNDLE)
    private val formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy")
    private val zone = ZoneId.systemDefault()

    @EventListener
    fun handleTaskDone(event: TaskDoneEvent) {
        sellers.add(event.seller)
        if (sellers.containsAll(allSellers)) {
            sellers.clear()
            val date = formatter.format(Instant.now().atZone(zone))
            logger.info("Started creating price snapshots for day $date")
            createPriceSnapshots()
            logger.info("Finished creating price snapshots for day $date")
        }
    }

    private fun createPriceSnapshots() {
        val gameIds = gameService.getAllGamesIds()
        gameIds.forEach { gameId ->
            val gameSellers = gameService.getSellersForGame(gameId)
            val game = gameSellers[0].game
            val prices = gameSellers.map { it.price }
            val snapshot = PriceSnapshot(game, prices.average(), prices.min(), Instant.now())
            logger.info("Saving price snapshot($snapshot) for game: $game")
            gameService.saveSnapshot(snapshot)
        }
    }
}
