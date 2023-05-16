package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestPropertySource

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@DataJpaTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
class GameSellerRepositoryTest {
    @Autowired
    lateinit var gameRepository: GameRepository
    @Autowired
    lateinit var gameSellerRepository: GameSellerRepository
    private lateinit var gameId: String
    private lateinit var gameSeller1: GameSeller
    private lateinit var gameSeller2: GameSeller

    @BeforeEach
    fun setup() {
        var game = Game("Test Game")
        game = gameRepository.save(game)
        gameRepository.flush()
        gameId = game.id

        gameSeller1 = GameSeller(
            "https://example.com/seller1",
            19.99,
            Seller.STEAM,
            game
        )
        gameSeller2 = GameSeller(
            "https://example.com/seller2",
            24.99,
            Seller.EA_GAMES,
            game
        )

        gameSeller1 = gameSellerRepository.save(gameSeller1)
        gameSeller2 = gameSellerRepository.save(gameSeller2)
        gameSellerRepository.flush()
    }

    @Test
    fun `findGameSellersByGameId valid game id`() {
        val result = gameSellerRepository.findGameSellersByGameId(gameId)

        Assertions.assertNotEquals(0, result.size)
        Assertions.assertEquals(result, listOf(gameSeller1, gameSeller2))
    }

    @Test
    fun `findGameSellersByGameId invalid game id`() {
        val result = gameSellerRepository.findGameSellersByGameId("invalid id")

        Assertions.assertEquals(0, result.size)
    }

    @Test
    fun `findGameSellerByGameIdAndSeller valid seller and game id`() {
        val result = gameSellerRepository.findGameSellerByGameIdAndSeller(gameId, Seller.EA_GAMES)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(gameSeller2, result)
    }

    @Test
    fun `findGameSellerByGameIdAndSeller no seller for game and valid game id`() {
        val result = gameSellerRepository.findGameSellerByGameIdAndSeller(gameId, Seller.HUMBLE_BUNDLE)

        Assertions.assertNull(result)
    }
}
