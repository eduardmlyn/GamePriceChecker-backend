package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.Game
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Transactional
interface GameRepository: JpaRepository<Game, String>, PagingAndSortingRepository<Game, String> {
    fun findGameByName(name: String): Game? // not needed? -> needed for updating info and connecting different sellers to a game?
    fun findGameById(id: String): Game?
    @Modifying
    @Query("UPDATE Game g set g.description = ?2 where g.id = ?1")
    fun changeDescription(gameId: String, description: String): Game?
    @Modifying
    @Query("UPDATE Game g set g.imageUrl =?2 where g.id = ?1")
    fun changeImageUrl(gameId: String, imageUrl: String): Game?
}