package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface GameRepository : JpaRepository<Game, String>, PagingAndSortingRepository<Game, String> {
    @Query("select g from Game g where lower(g.name) = lower(:name) ")
    fun findGameByName(@Param("name") name: String): Game?
    fun findGameById(id: String): Game?

    @Query("select g from Game g where lower(g.name) like lower(concat('%', :name, '%'))")
    fun findByName(@Param("name") name: String, pageable: Pageable): Page<Game>

    @Query("select g.id from Game g")
    fun findAllGameIds(): List<String>

    @Query("select new kotlin.Pair(g, gs) from Game g join g.gameSellers gs where gs.seller = :seller")
    fun findGamesBySeller(@Param("seller") seller: Seller): List<Pair<Game, GameSeller>>

    @Query("select count(g) from Game g where lower(g.name) like lower(concat('%', :filter, '%'))")
    fun countFiltered(@Param("filter") filter: String): Long

    @Query(
        """
            select g.*
            from games g 
            join game_favorites gf on g.id = gf.game_id 
            where gf.user_id = :userId 
            and lower(g.name) like lower(concat('%', :filter, '%'))
        """, nativeQuery = true
    )
    fun findUserGameFavorites(
        @Param("filter") filter: String,
        @Param("userId") userId: String,
        pageable: Pageable
    ): Page<Game>

    @Query(
        """
            select count(g)
            from games g 
            join game_favorites gf on g.id = gf.game_id 
            where gf.user_id = :userId 
            and lower(g.name) like lower(concat('%', :filter, '%'))
        """, nativeQuery = true
    )
    fun favoriteCountFiltered(@Param("filter") filter: String, @Param("userId") userId: String): Long
}
