package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.entity.PriceSnapshot
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.model.event.TaskDoneEvent
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.Instant

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Component
class TaskDoneEventListener(val gameService: GameService) {
    private val sellers: MutableSet<Seller> = mutableSetOf()
    private val allSellers: Set<Seller> = mutableSetOf(Seller.STEAM, Seller.EA_GAMES, Seller.HUMBLE_BUNDLE)

    @EventListener
    fun handleTaskDone(event: TaskDoneEvent) {
        sellers.add(event.seller)
        if (sellers.containsAll(allSellers)) {
            sellers.clear()
            createPriceSnapshots()
        }
    }

    private fun createPriceSnapshots() {
        val gameIds = gameService.getAllGamesIds()
        gameIds.forEach { gameId ->
            val gameSellers = gameService.getSellersForGame(gameId)
            val game = gameSellers[0].game
            val prices = gameSellers.map { it.price }
            val snapshot = PriceSnapshot(game, prices.average(), prices.min(), Instant.now())
            gameService.saveSnapshot(snapshot)
        }
    }
}
