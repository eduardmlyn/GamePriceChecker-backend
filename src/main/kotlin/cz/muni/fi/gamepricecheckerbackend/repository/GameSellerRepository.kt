package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import org.springframework.data.jpa.repository.JpaRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface GameSellerRepository : JpaRepository<GameSeller, String> {
    fun findGameSellersByGameId(gameId: String): List<GameSeller>
    fun findGameSellerByGameIdAndSeller(gameId: String, seller: Seller): GameSeller?
}
