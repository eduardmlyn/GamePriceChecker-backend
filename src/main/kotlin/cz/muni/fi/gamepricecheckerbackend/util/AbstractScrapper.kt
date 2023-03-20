package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.repository.GameSellerRepository
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import jakarta.transaction.Transactional

/**
 * Abstract scrapper class.
 *
 * @author Eduard Stefan Mlynarik
 */
@Transactional
abstract class AbstractScrapper(
    private val gameRepository: GameRepository,
    private val gameSellerRepository: GameSellerRepository
) : Scrapper {
    protected fun saveScrappedGame(
        gameName: String,
        gamePrice: Double,
        gameDescription: String,
        gameImage: String,
        gameRelease: String?,
        gameLink: String,
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
        val gameSellers = gameSellerRepository.findGameSellerByGameId(game.id)
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
}
