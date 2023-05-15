package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.BaseIntegrationTest
import cz.muni.fi.gamepricecheckerbackend.GamePriceCheckerBackendApplication
import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationResponse
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.entity.Game
import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.model.enums.Order
import cz.muni.fi.gamepricecheckerbackend.model.enums.SortBy
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl

/**
 *
 * @author Eduard Stefan Mlynarik
 */
internal class UserServiceTest: BaseIntegrationTest() {
    private val userRepository: UserRepository = mockk()
    private val gameRepository: GameRepository = mockk()
    private val jwtService: JwtService = mockk()
    private val blackListService: BlackListService = mockk()
    private val userService: UserService = UserService(userRepository, gameRepository, jwtService, blackListService)
    private lateinit var user: User
    private lateinit var emptyUser: User
    private lateinit var game: Game
    private val username = "testUser"
    private val token = "testToken"

    @BeforeEach
    fun setUp() {
        user = mockk()
        emptyUser = mockk()
        game = mockk()
    }

    @Test
    fun `findByUsername non-existent user`() {
        every { userRepository.findUserByUserName("non-existent") } returns null
        val result = userService.findByUsername("non-existent")
        verify(exactly = 1) {userRepository.findUserByUserName("non-existent")}
        Assertions.assertNull(result)
    }

    @Test
    fun `findByUsername existing user`() {
        every { userRepository.findUserByUserName(username) } returns user
        val result = userService.findByUsername(username)
        verify(exactly = 1) { userRepository.findUserByUserName(username) }
        Assertions.assertEquals(user, result)
    }

    @Test
    fun deleteUser() {
        every { jwtService.getUserName() } returns username
        every { userRepository.findUserByUserName(username) } returns user
        every { user.favorites } returns mutableListOf()
        every { userRepository.save(user) } returns user
        every { userRepository.delete(user) } returns Unit
        val result = userService.deleteUser()
        verifySequence {
            jwtService.getUserName()
            userRepository.findUserByUserName(username)
            user.favorites
            userRepository.save(user)
            userRepository.delete(user)
        }
        Assertions.assertEquals(result, Unit)
    }

    @Test
    fun `editUsername empty username`() {
        every { jwtService.getUserName() } returns username
        every { userRepository.findUserByUserName(username) } returns user
        every { userRepository.findUserByUserName("") } returns null
        every { user.userName = "" } just Runs
        every { userRepository.save(user) } returns user
        every { blackListService.addToBlackList("") } returns Unit
        every { jwtService.generateToken(user) } returns token
        val result = userService.editUsername("", "")
        verifySequence {
            jwtService.getUserName()
            userRepository.findUserByUserName(username)
            userRepository.findUserByUserName("")
            userRepository.save(user)
            blackListService.addToBlackList("")
            jwtService.generateToken(user)
        }
        Assertions.assertEquals(result, AuthenticationResponse(token))
    }

    @Test
    fun `editUsername non-existent user`() {
        every { jwtService.getUserName() } returns username
        every { userRepository.findUserByUserName(username) } returns null
        val result = userService.editUsername("", "")
        verifySequence {
            jwtService.getUserName()
            userRepository.findUserByUserName(username)
        }
        Assertions.assertEquals(result, null)
    }

    @Test
    fun `add game to user`() {
        val gameId = "testId"
        every { jwtService.getUserName() } returns username
        every { userRepository.findUserByUserName(username) } returns user
        every { user.favorites } returns mutableListOf()
        every { gameRepository.findGameById(gameId) } returns game
        every { userRepository.save(user) } returns user

        val result = userService.changeGameToUserRelation(gameId)

        verifySequence {
            jwtService.getUserName()
            userRepository.findUserByUserName(username)
            user.favorites
            gameRepository.findGameById(gameId)
            user.favorites
            userRepository.save(user)
        }
        Assertions.assertTrue(result)
    }

    @Test
    fun `remove game from user`() {
        val gameId = "testId"
        val gameName = "testName"
        every { jwtService.getUserName() } returns username
        every { userRepository.findUserByUserName(username) } returns user
        every { user.favorites } returns mutableListOf(Game(gameId, gameName))
        every { userRepository.save(user) } returns user

        val result = userService.changeGameToUserRelation(gameId)

        verifySequence {
            jwtService.getUserName()
            userRepository.findUserByUserName(username)
            user.favorites
            user.favorites
            userRepository.save(user)
        }
        Assertions.assertTrue(result)
    }

    @Test
    fun `getUserFavorites valid params`() {
        val games = listOf(
            Game("Id A", "Game A"),
            Game("Id B", "Game B"),
            Game("Id C", "Game C")
        )
        every { jwtService.getUserName() } returns username
        every { userRepository.findUserByUserName(username) } returns user
        every { user.id } returns ""
        every { gameRepository.findUserGameFavorites("", user.id, any()) } returns PageImpl(games)

        val result = userService.getUserFavorites(0, 10, SortBy.RELEASE_DATE, Order.DESC, "")

        // add verify
        Assertions.assertEquals(listOf(
            GameDTO("Id A", "Game A", null, null),
            GameDTO("Id B", "Game B", null, null),
            GameDTO("Id C", "Game C", null, null)
        ), result)
    }

    @Test
    fun `getUserFavoriteCount empty filter`(){
        every { jwtService.getUserName() } returns username
        every { userRepository.findUserByUserName(username) } returns user
        every { user.id } returns ""
        every { gameRepository.favoriteCountFiltered("", user.id) } returns  2

        val result = userService.getUserFavoriteCount("")

        verifySequence {
            jwtService.getUserName()
            userRepository.findUserByUserName(username)
            gameRepository.favoriteCountFiltered("", user.id)
        }
        Assertions.assertEquals(result, 2)
    }

    @Test
    fun `getUserFavoriteCount non-existent user`(){
        every { jwtService.getUserName() } returns username
        every { userRepository.findUserByUserName(username) } returns null

        val result = userService.getUserFavoriteCount("")

        verifySequence {
            jwtService.getUserName()
            userRepository.findUserByUserName(username)
        }
        Assertions.assertEquals(result, 0)
    }
}