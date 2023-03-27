package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface GameRepository: JpaRepository<Game, String>, PagingAndSortingRepository<Game, String> {
    fun findGameByName(name: String): Game?
    fun findGameById(id: String): Game?
    @Modifying
    @Query("update Game g set g.imageUrl = ?2 where g.id = ?1")
    fun changeImage(gameId: String, imageUrl: String)
    @Modifying
    @Query("update Game g set g.description = ?2 where g.id = ?1")
    fun changeDescription(gameId: String, description: String)
    @Modifying
    @Query("update Game g set g.releaseDate = ?2 where g.id = ?1")
    fun changeReleaseDate(gameId: String, releaseDate: String)
}