package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.BaseIntegrationTest
import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import cz.muni.fi.gamepricecheckerbackend.repository.GameSellerRepository
import cz.muni.fi.gamepricecheckerbackend.repository.PriceSnapshotRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.springframework.data.domain.PageRequest

/**
 *
 * @author Eduard Stefan Mlynarik
 */
// TODO implement tests
//@SpringBootTest(classes = [GamePriceCheckerBackendApplication::class])
//@TestPropertySource(locations = ["classpath:application.properties","classpath:application-test.properties"])
internal class GameServiceTest : BaseIntegrationTest() {
    private val gameRepository: GameRepository = mockk()
    private val gameSellerRepository: GameSellerRepository = mockk()
    private val priceSnapshotRepository: PriceSnapshotRepository = mockk()
    private val gameService: GameService = GameService(gameRepository, gameSellerRepository, priceSnapshotRepository)
    private val game: Game = mockk()

    @Test
    fun `getGames negative page number`() {
        every { gameRepository.findAll(PageRequest.of(-1, PAGE_SIZE)).content } answers {throw Exception("")}
    }

    @Test
    fun getGame() {
    }

    @Test
    fun createGame() {
    }

    @Test
    fun addDescription() {
    }

    @Test
    fun addImageUrl() {
    }
}