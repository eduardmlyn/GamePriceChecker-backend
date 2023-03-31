package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDetailDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameSellerDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.PriceSnapshotDTO
import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import cz.muni.fi.gamepricecheckerbackend.model.entity.PriceSnapshot
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.repository.GameSellerRepository
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import cz.muni.fi.gamepricecheckerbackend.repository.PriceSnapshotRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

const val PAGE_SIZE = 25

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

    fun getGames(page: Int): List<GameDTO> {
        // CAN BE SORTING AS 3rd PARAM
        val games = gameRepository.findAll(PageRequest.of(page, PAGE_SIZE)).content
        return games.map { GameDTO(it.id, it.name, it.imageUrl, it.releaseDate) }
    }

    // TODO remove?
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

    fun getPageCount(): Long {
        return gameRepository.count() / PAGE_SIZE
    }

    @Transactional
    fun saveGame(
        gameName: String,
        gamePrice: Double,
        gameDescription: String?,
        gameImage: String?,
        gameRelease: String?,
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

    fun getUpdatableGamesForSeller(seller: Seller): List<GameSeller> {
        return gameSellerRepository.findGameSellersBySeller(seller).filter {
            val game = it.game
            game.description == null || game.imageUrl == null
        }
    }

    @Transactional
    fun updateGameInformation(game: Game, description: String?, imageUrl: String?, releaseDate: String?) {
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
