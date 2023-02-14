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
    fun getGameByName(name: String): Game?
    fun getGames(page: Int): List<Game>
    fun createGame(name: String): Game
    fun addExternalLink(extLink: GameLink): Boolean
}

@Component
class GameFacadeImpl(val gameService: GameService): GameFacade {
    override fun getGameByName(name: String): Game? {
        TODO("Not yet implemented")
    }

    override fun getGames(page: Int): List<Game> {
        // TODO
        return gameService.getGames(page)
    }

    override fun createGame(name: String): Game {
        TODO("Not yet implemented")
    }

    override fun addExternalLink(extLink: GameLink): Boolean {
        TODO("Not yet implemented")
    }

}