package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.PriceSnapshot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestPropertySource
import java.time.Instant

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@DataJpaTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
class PriceSnapshotRepositoryTest {
    @Autowired
    lateinit var gameRepository: GameRepository
    @Autowired
    lateinit var priceSnapshotRepository: PriceSnapshotRepository
    private lateinit var gameId: String
    private lateinit var priceSnapshot1: PriceSnapshot
    private lateinit var priceSnapshot2: PriceSnapshot

    @BeforeEach
    fun setup() {
        var game = Game("Test Game")
        game = gameRepository.save(game)
        gameRepository.flush()
        gameId = game.id

        priceSnapshot1 = PriceSnapshot(
            game,
            19.99,
            9.99,
            Instant.now()
        )
        priceSnapshot2 = PriceSnapshot(
            game,
            24.99,
            14.99,
            Instant.now()
        )

        priceSnapshot1 = priceSnapshotRepository.save(priceSnapshot1)
        priceSnapshot2 = priceSnapshotRepository.save(priceSnapshot2)
        priceSnapshotRepository.flush()
    }

    @Test
    fun `findPriceSnapshotsByGameId with valid game id`() {
        val result = priceSnapshotRepository.findPriceSnapshotsByGameId(gameId)

        Assertions.assertNotEquals(0, result.size)
        Assertions.assertEquals(listOf(priceSnapshot1, priceSnapshot2), result)
    }

    @Test
    fun `findPriceSnapshotsByGameId with invalid game id`() {
        val result = priceSnapshotRepository.findPriceSnapshotsByGameId("invalid id")

        Assertions.assertEquals(0, result.size)
    }
}
