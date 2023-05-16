package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.GameSeller
import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.model.enums.Role
import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.model.enums.SortBy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.TestPropertySource

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@DataJpaTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
class GameRepositoryTest {
    @Autowired
    private lateinit var gameRepository: GameRepository

    @Autowired
    private lateinit var gameSellerRepository: GameSellerRepository

    @Autowired
    private lateinit var userRepository: UserRepository
    private lateinit var game1: Game
    private lateinit var game2: Game
    private lateinit var gameSeller1: GameSeller
    private lateinit var gameSeller2: GameSeller
    private lateinit var gameSeller3: GameSeller
    private lateinit var user: User
    private val pageAbleSize1Ascending = PageRequest.of(0, 1, Sort.by(SortBy.NAME.value).ascending())
    private val pageAbleSize1Descending = PageRequest.of(0, 1, Sort.by(SortBy.NAME.value).descending())
    private val pageAbleSize5Ascending = PageRequest.of(0, 5, Sort.by(SortBy.NAME.value).ascending())
    private val pageAbleSize5Descending = PageRequest.of(0, 5, Sort.by(SortBy.NAME.value).descending())

    @BeforeEach
    fun setup() {
        game1 = gameRepository.save(Game("Test Game 1"))
        game2 = gameRepository.save(Game("Test Game 2"))
        gameRepository.flush()

        gameSeller1 = gameSellerRepository.save(GameSeller(Seller.STEAM, game1))
        gameSeller2 = gameSellerRepository.save(GameSeller(Seller.STEAM, game2))
        gameSeller3 = gameSellerRepository.save(GameSeller(Seller.HUMBLE_BUNDLE, game2))
        gameSellerRepository.flush()

        user = userRepository.save(User("Test User", "Test User", Role.USER))
        user.favorites.addAll(listOf(game1, game2))
        userRepository.flush()
    }

    @Test
    fun `findGameByName lower case valid name`() {
        val result = gameRepository.findGameByName("test game 1")

        Assertions.assertNotNull(result)
        Assertions.assertEquals(game1, result)
    }

    @Test
    fun `findGameByName invalid name`() {
        val result = gameRepository.findGameByName("invalid game name")

        Assertions.assertNull(result)
    }

    @Test
    fun `findGameById valid id`() {
        val result = gameRepository.findGameById(game2.id)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(game2, result)
    }

    @Test
    fun `findGameById invalid id`() {
        val result = gameRepository.findGameByName("invalid id")

        Assertions.assertNull(result)
    }

    @Test
    fun `findByName empty name page size 1 ascending sort`() {
        val result = gameRepository.findByName("", pageAbleSize1Ascending)

        val expected: Page<Game> = PageImpl(mutableListOf(game1))

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun `findByName empty name page size 1 descending sort`() {
        val result = gameRepository.findByName("", pageAbleSize1Descending)

        val expected: Page<Game> = PageImpl(mutableListOf(game2))

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun `findByName empty name page size 5 ascending sort`() {
        val result = gameRepository.findByName("", pageAbleSize5Ascending)

        val expected: Page<Game> = PageImpl(mutableListOf(game1, game2))

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun `findByName valid name fragment page size 5 descending sort`() {
        val result = gameRepository.findByName("", pageAbleSize5Descending)

        val expected: Page<Game> = PageImpl(mutableListOf(game2, game1))

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun `findByName invalid name fragment`() {
        val result = gameRepository.findByName("invalid", pageAbleSize5Descending)

        val expected: Page<Game> = PageImpl(mutableListOf())

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun findAllGameIds() {
        val result = gameRepository.findAllGameIds().sorted()

        val expected = listOf(game1.id, game2.id).sorted()

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `findGamesBySeller seller steam`() {
        val result = gameRepository.findGamesBySeller(Seller.STEAM)

        val expected = listOf(Pair(game1, gameSeller1), Pair(game2, gameSeller2))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `findGamesBySeller seller humble bundle`() {
        val result = gameRepository.findGamesBySeller(Seller.HUMBLE_BUNDLE)

        val expected = listOf(Pair(game2, gameSeller3))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `findGamesBySeller seller ea games`() {
        val result = gameRepository.findGamesBySeller(Seller.EA_GAMES)

        Assertions.assertEquals(emptyList<Pair<Game, GameSeller>>(), result)
    }

    @Test
    fun `countFiltered without filter`() {
        val result = gameRepository.countFiltered("")

        Assertions.assertEquals(2L, result)
    }

    @Test
    fun `countFiltered with filter`() {
        val result = gameRepository.countFiltered("1")

        Assertions.assertEquals(1L, result)
    }

    @Test
    fun `findUserGameFavorites empty filter valid userId page size 1 sort ascending`() {
        val result = gameRepository.findUserGameFavorites("", user.id, pageAbleSize1Ascending)

        val expected: Page<Game> = PageImpl(mutableListOf(game1))

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun `findUserGameFavorites empty filter valid userId page size 1 sort descending`() {
        val result = gameRepository.findUserGameFavorites("", user.id, pageAbleSize1Descending)

        val expected: Page<Game> = PageImpl(mutableListOf(game2))

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun `findUserGameFavorites empty filter valid userId page size 5 sort ascending`() {
        val result = gameRepository.findUserGameFavorites("", user.id, pageAbleSize5Ascending)

        val expected: Page<Game> = PageImpl(mutableListOf(game1, game2))

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun `findUserGameFavorites empty filter valid userId page size 5 sort descending`() {
        val result = gameRepository.findUserGameFavorites("", user.id, pageAbleSize5Descending)

        val expected: Page<Game> = PageImpl(mutableListOf(game2, game1))

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun `findUserGameFavorites simple filter valid userId`() {
        val result = gameRepository.findUserGameFavorites("1", user.id, pageAbleSize5Ascending)

        val expected: Page<Game> = PageImpl(mutableListOf(game1))

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun `findUserGameFavorites empty filter invalid userId`() {
        val result = gameRepository.findUserGameFavorites("", "invalid id", pageAbleSize5Ascending)

        val expected: Page<Game> = PageImpl(mutableListOf())

        Assertions.assertEquals(expected.content, result.content)
    }

    @Test
    fun `favoriteCountFiltered empty filter valid userId`() {
        val result = gameRepository.favoriteCountFiltered("", user.id)

        Assertions.assertEquals(2L, result)
    }

    @Test
    fun `favoriteCountFiltered simple filter valid userId`() {
        val result = gameRepository.favoriteCountFiltered("1", user.id)

        Assertions.assertEquals(1L, result)
    }

    @Test
    fun `favoriteCountFiltered empty filter invalid userId`() {
        val result = gameRepository.favoriteCountFiltered("", "invalid id")

        Assertions.assertEquals(0L, result)
    }
}
