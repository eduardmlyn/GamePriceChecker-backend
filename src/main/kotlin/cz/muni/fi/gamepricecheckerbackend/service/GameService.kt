package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDetailDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameSellerDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.PriceSnapshotDTO
import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import cz.muni.fi.gamepricecheckerbackend.model.entity.PriceSnapshot
import cz.muni.fi.gamepricecheckerbackend.model.enums.Order
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.model.enums.SortBy
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import cz.muni.fi.gamepricecheckerbackend.repository.GameSellerRepository
import cz.muni.fi.gamepricecheckerbackend.repository.PriceSnapshotRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date


/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
@Transactional(readOnly = true)
class GameService(
    private val gameRepository: GameRepository,
    private val gameSellerRepository: GameSellerRepository,
    private val priceSnapshotRepository: PriceSnapshotRepository
) {

    fun getGames(page: Int, pageSize: Int, sortBy: SortBy, order: Order): List<GameDTO> {
        println(sortBy.value)
        return when (order) {
            Order.ASC -> {
                val games = gameRepository.findAll(PageRequest.of(page, pageSize, Sort.by(sortBy.value).ascending())).content
                games.map { GameDTO(it.id, it.name, it.imageUrl, it.releaseDate) }
            }
            Order.DESC -> {
                val games = gameRepository.findAll(PageRequest.of(page, pageSize, Sort.by(sortBy.value).descending())).content
                games.map { GameDTO(it.id, it.name, it.imageUrl, it.releaseDate) }
            }
        }
    }

    fun getGameDetailsById(gameId: String): GameDetailDTO? {
        val game = gameRepository.findGameById(gameId) ?: return null
        return GameDetailDTO(
            game.id,
            game.name,
            game.description,
            game.imageUrl,
            game.releaseDate,
            getSellerLinksForGame(game.id),
            getPriceSnapshotsForGame(game.id)
        )
    }

    // TODO remove?
    fun getGameDetailsByName(gameName: String): GameDetailDTO? {
        val game = gameRepository.findGameByName(gameName) ?: return null

        return GameDetailDTO(
            game.id,
            game.name,
            game.description,
            game.imageUrl,
            game.releaseDate,
            getSellerLinksForGame(game.id),
            getPriceSnapshotsForGame(game.id)
        )
    }

    fun getAllGamesIds(): List<String> {
        return gameRepository.findAllGameIds()
    }

    private fun getSellerLinksForGame(gameId: String): Set<GameSellerDTO> {
        return gameSellerRepository.findGameSellersByGameId(gameId)
            .map {
                GameSellerDTO(it.seller, it.link, it.price)
            }.toSet()
    }

    private fun getPriceSnapshotsForGame(gameId: String): List<PriceSnapshotDTO> {
        return priceSnapshotRepository.findPriceSnapshotsByGameId(gameId)
            .map {
                PriceSnapshotDTO(it.averagePrice, it.minimumPrice, it.date)
            }
    }

    fun getGamesCount(): Long {
        return gameRepository.count()
    }

    @Transactional
    fun saveGame(
        gameName: String,
        gamePrice: Double,
        gameDescription: String?,
        gameImage: String?,
        gameRelease: Date?,
        gameLink: String?,
        sellerName: Seller
    ) {
        val game = gameRepository.findGameByName(gameName)
        if (game == null) {
            val newGame = Game(gameName, gameDescription, gameImage, gameRelease)
            val savedGame = gameRepository.save(newGame)
            val newGameSeller = GameSeller(gameLink, gamePrice, sellerName, savedGame)
            gameSellerRepository.save(newGameSeller)
            return
        }
        updateGameInformation(game, gameDescription, gameImage, gameRelease)
        val gameSellers = gameSellerRepository.findGameSellersByGameId(game.id)
        if (gameSellers.all { it.seller != sellerName }) {
            val newGameSeller = GameSeller(gameLink, gamePrice, sellerName, game)
            gameSellerRepository.save(newGameSeller)
            return
        }

        val currentGameSeller = gameSellers.single { it.seller == sellerName }
        if (currentGameSeller.price != gamePrice) {
            currentGameSeller.price = gamePrice
        }
        if (currentGameSeller.link != gameLink) {
            currentGameSeller.link = gameLink
        }
        gameSellerRepository.save(currentGameSeller)
    }

    @Transactional
    fun saveGamePrice(
        gameName: String,
        gamePrice: Double,
        gameLink: String?,
        sellerName: Seller
    ) {
        var game = gameRepository.findGameByName(gameName)
        if (game == null) {
            game = Game(gameName)
            game = gameRepository.save(game)
        }
        val gameSeller = gameSellerRepository.findGameSellerByGameIdAndSeller(game.id, sellerName)
            ?: GameSeller(sellerName, game)
        gameSeller.price = gamePrice
        gameSeller.link = gameLink
        gameSellerRepository.save(gameSeller)
    }

    fun getUpdatableGamesForSeller(seller: Seller): List<Pair<Game, GameSeller>> {
        return gameRepository.findGamesBySeller(seller)
    }

    @Transactional
    fun updateGameInformation(game: Game, description: String?, imageUrl: String?, releaseDate: Date?) {
        if (game.imageUrl == null && imageUrl != null) {
            game.imageUrl = imageUrl
        }
        if (game.description == null && description != null) {
            game.description = description
        }
        if (game.releaseDate == null && releaseDate != null) {
            game.releaseDate = releaseDate
        }
        gameRepository.save(game)
    }

    fun getSellersForGame(gameId: String): List<GameSeller> {
        return gameSellerRepository.findGameSellersByGameId(gameId)
    }

    @Transactional
    fun saveSnapshot(snapshot: PriceSnapshot) {
        priceSnapshotRepository.save(snapshot)
    }
}
