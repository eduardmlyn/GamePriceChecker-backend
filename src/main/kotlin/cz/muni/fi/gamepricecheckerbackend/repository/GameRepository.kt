package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface GameRepository: JpaRepository<Game, String>, PagingAndSortingRepository<Game, String> {
    @Query("select g from Game g where lower(g.name) = lower(?1) ")
    fun findGameByName(name: String): Game?
    fun findGameById(id: String): Game?
    @Query("select g.id from Game g")
    fun findAllGameIds(): List<String>
}
