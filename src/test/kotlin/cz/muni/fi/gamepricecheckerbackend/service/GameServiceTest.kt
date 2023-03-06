package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.GamePriceCheckerBackendApplication
import cz.muni.fi.gamepricecheckerbackend.model.Game
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@SpringBootTest(classes = [GamePriceCheckerBackendApplication::class])
internal class GameServiceTest {
    private val gameRepository: GameRepository = mockk()
    private val gameService: GameService = GameService(gameRepository)
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