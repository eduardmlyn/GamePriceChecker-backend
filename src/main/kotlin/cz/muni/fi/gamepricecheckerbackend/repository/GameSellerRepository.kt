package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import org.springframework.data.jpa.repository.JpaRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface GameSellerRepository: JpaRepository<GameSeller, String> {
    fun findGameSellerByGameId(gameId: String): Set<GameSeller>
}