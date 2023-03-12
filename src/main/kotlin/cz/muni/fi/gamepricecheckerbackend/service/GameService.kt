package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.Game
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

const val PAGE_SIZE = 25

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
class GameService(private val gameRepository: GameRepository) {

    fun getGames(page: Int): List<Game> {
        // CAN BE SORTING AS 3rd PARAM
        return gameRepository.findAll(PageRequest.of(page, PAGE_SIZE)).content
    }

    fun getGame(gameId: String): Game? {
        return gameRepository.findGameById(gameId)
    }

    fun createGame(name: String): Game { // createGame with just name?
        return gameRepository.save(Game(name))
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