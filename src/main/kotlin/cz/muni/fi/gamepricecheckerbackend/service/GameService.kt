package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDetailDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameSellerDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.PriceSnapshotDTO
import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.repository.GameSellerRepository
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import cz.muni.fi.gamepricecheckerbackend.repository.PriceSnapshotRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

const val PAGE_SIZE = 25

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
@Transactional
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

    private fun getSellerLinksForGame(gameId: String): Set<GameSellerDTO> {
        return gameSellerRepository.findGameSellerByGameId(gameId)
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

    fun addDescription(gameId: String, description: String): Game? {
        return gameRepository.changeDescription(gameId, description)
    }

    fun addImageUrl(gameId: String, imageUrl: String): Game? {
        return gameRepository.changeImageUrl(gameId, imageUrl)
    }

    fun getPageCount(): Long {
        return gameRepository.count() / PAGE_SIZE
    }
}