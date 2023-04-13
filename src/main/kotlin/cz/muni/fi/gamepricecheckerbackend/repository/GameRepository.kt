package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface GameRepository : JpaRepository<Game, String>, PagingAndSortingRepository<Game, String> {
    @Query("select g from Game g where lower(g.name) = lower(?1) ")
    fun findGameByName(name: String): Game?
    fun findGameById(id: String): Game?


    @Query("select g.id from Game g")
    fun findAllGameIds(): List<String>

    @Query("select new kotlin.Pair(g, gs) from Game g join g.gameSellers gs where gs.seller = :seller")
    fun findGamesBySeller(@Param("seller") seller: Seller): List<Pair<Game, GameSeller>>

//    fun getGamesByUsersId(userId: String)

//    fun findGameByIdAndUsersId(gameId: String, userId: String): Game?
}
