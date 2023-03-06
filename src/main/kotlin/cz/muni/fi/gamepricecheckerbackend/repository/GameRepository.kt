package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.Game
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Transactional
interface GameRepository: JpaRepository<Game, String> {
    fun findGameByName(name: String): Game? // not needed?
    fun findGameById(id: String): Game?
    @Modifying
    @Query("UPDATE Game g set g.description = ?2 where g.id = ?1")
    fun changeDescription(gameId: String, description: String): Game?
    @Modifying
    @Query("UPDATE Game g set g.imageUrl =?2 where g.id = ?1")
    fun changeImageUrl(gameId: String, imageUrl: String): Game?
}