package cz.muni.fi.gamepricecheckerbackend.facade

import cz.muni.fi.gamepricecheckerbackend.model.Game
import cz.muni.fi.gamepricecheckerbackend.model.GameLink
import cz.muni.fi.gamepricecheckerbackend.service.GameService
import org.springframework.stereotype.Component

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface GameFacade {
    fun getGameById(id: String): Game?
    fun getGames(page: Int): List<Game>
    fun createGame(name: String): Game
    fun addExternalLink(extLink: GameLink): Boolean
}

@Component
class GameFacadeImpl(val gameService: GameService): GameFacade {
    override fun getGameById(id: String): Game? {
        return gameService.getGame(id)
    }

    override fun getGames(page: Int): List<Game> {
        return gameService.getGames(page)
    }

    // only used internally?
    override fun createGame(name: String): Game {
        return gameService.createGame(name)
    }

    override fun addExternalLink(extLink: GameLink): Boolean {
        TODO("Not yet implemented")
    }

}