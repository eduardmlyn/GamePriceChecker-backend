package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.BaseIntegrationTest
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDetailDTO
import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import cz.muni.fi.gamepricecheckerbackend.model.enums.Order
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.model.enums.SortBy
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import cz.muni.fi.gamepricecheckerbackend.repository.GameSellerRepository
import cz.muni.fi.gamepricecheckerbackend.repository.PriceSnapshotRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page

/**
 *
 * @author Eduard Stefan Mlynarik
 */
internal class GameServiceTest : BaseIntegrationTest() {
    private val gameRepository: GameRepository = mockk()
    private val gameSellerRepository: GameSellerRepository = mockk()
    private val priceSnapshotRepository: PriceSnapshotRepository = mockk()
    private val gameService: GameService = GameService(gameRepository, gameSellerRepository, priceSnapshotRepository)
    private val game: Game = mockk()

    @Test
    fun getGames() {
        val page = mockk<Page<Game>>()
        every { gameRepository.findByName("", any()) } returns page
        every { page.content } returns listOf(game)
        every { game.id } returns "id"
        every { game.name } returns "name"
        every { game.imageUrl } returns "image"
        every { game.releaseDate } returns null
        val result = gameService.getGames(1, 10, SortBy.NAME, Order.ASC, "")

        Assertions.assertEquals(result, listOf(GameDTO("id", "name", "image", null)))
    }

    @Test
    fun `getGameDetailsById invalid id`() {
        every { gameRepository.findGameById("") } returns null

        val result = gameService.getGameDetailsById("")

        Assertions.assertNull(result)
    }

    @Test
    fun `getGameDetailsById valid id`() {
        val testGame = Game("", "", "", "", null, emptySet(), emptyList())
        every { gameRepository.findGameById("") } returns testGame
        every { gameSellerRepository.findGameSellersByGameId("") } returns emptyList()
        every { priceSnapshotRepository.findPriceSnapshotsByGameId("") } returns emptyList()

        val result = gameService.getGameDetailsById("")

        verifySequence {
            gameRepository.findGameById("")
            gameSellerRepository.findGameSellersByGameId("")
            priceSnapshotRepository.findPriceSnapshotsByGameId("")
        }
        Assertions.assertEquals(result, GameDetailDTO("", "", "", "", null, emptySet(), emptyList()))
    }

    @Test
    fun `saveGame valid params non-existent game`() {
        val seller = mockk<GameSeller>()
        every { gameRepository.findGameByName("") } returns null
        every { gameRepository.save(any()) } returns game
        every { gameSellerRepository.save(any()) } returns seller

        gameService.saveGame("", 1.0, null, null, null, null, Seller.EA_GAMES)

        verifySequence {
            gameRepository.findGameByName("")
            gameRepository.save(any())
            gameSellerRepository.save(any())
        }
    }

    @Test
    fun `saveGame valid params existent game`() {
        val seller = GameSeller(Seller.STEAM, game)
        val sellerList = listOf(seller)
        every { gameRepository.findGameByName("") } returns game
        every { game.id } returns "id"
        every { game.imageUrl } returns null
        every { game.description } returns null
        every { game.releaseDate } returns null
        every { gameRepository.save(any()) } returns game
        every { gameSellerRepository.findGameSellersByGameId("id") } returns sellerList

        every { gameSellerRepository.save(any()) } returns seller

        gameService.saveGame("", 0.0, null, null, null, null, Seller.STEAM)

        verifySequence {
            gameRepository.findGameByName("")
            game.imageUrl
            game.description
            game.releaseDate
            gameRepository.save(any())
            game.id
            gameSellerRepository.findGameSellersByGameId("id")
            gameSellerRepository.save(any())
        }
    }

    @Test
    fun `saveGamePrice valid params non-existent game`() {
        val seller = mockk<GameSeller>()
        every { gameRepository.findGameByName("") } returns null
        every { gameRepository.save(any()) } returns game
        every { game.id } returns "id"
        every { gameSellerRepository.findGameSellerByGameIdAndSeller("id", Seller.STEAM) } returns seller
        every { seller.price } returns 0.0
        every { seller.price = 1.0 } just Runs
        every { seller.link } returns "link"
        every { seller.link = null } just Runs
        every { gameSellerRepository.save(any()) } returns seller

        gameService.saveGamePrice("", 1.0, null, Seller.STEAM)

        verifySequence {
            gameRepository.findGameByName("")
            gameRepository.save(any())
            game.id
            gameSellerRepository.findGameSellerByGameIdAndSeller("id", Seller.STEAM)
            seller.price = 1.0
            seller.link = null
            gameSellerRepository.save(any())
        }
    }

    @Test
    fun `saveGamePrice valid params existent game`() {
        val seller = mockk<GameSeller>()
        every { gameRepository.findGameByName("") } returns game
        every { game.id } returns "id"
        every { gameSellerRepository.findGameSellerByGameIdAndSeller("id", Seller.STEAM) } returns seller
        every { seller.price } returns 0.0
        every { seller.price = 1.0 } just Runs
        every { seller.link } returns "link"
        every { seller.link = null } just Runs
        every { gameSellerRepository.save(any()) } returns seller

        gameService.saveGamePrice("", 1.0, null, Seller.STEAM)

        verifySequence {
            gameRepository.findGameByName("")
            game.id
            gameSellerRepository.findGameSellerByGameIdAndSeller("id", Seller.STEAM)
            seller.price = 1.0
            seller.link = null
            gameSellerRepository.save(any())
        }
    }
}