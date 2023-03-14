package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.GameLink
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Transactional
interface GameLinkRepository: JpaRepository<GameLink, String> {
    fun findGameLinkBySeller(seller: String): GameLink
}