package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.Game
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Transactional
interface GameRepository: JpaRepository<Game, String> {
    fun findGameByName(name: String): Game?
}