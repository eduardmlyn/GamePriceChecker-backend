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
    fun test() {
        TODO()
    }
}