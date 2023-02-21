package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.GamePriceCheckerBackendApplication
import cz.muni.fi.gamepricecheckerbackend.model.Role
import cz.muni.fi.gamepricecheckerbackend.model.User
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import cz.muni.fi.gamepricecheckerbackend.security.JwtService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@SpringBootTest(classes = [GamePriceCheckerBackendApplication::class])
internal class UserServiceTest {
    private val userRepository: UserRepository = mockk()
    private val jwtService: JwtService = mockk()
    private val userService: UserService = UserService(userRepository, jwtService)
    private val user: User = mockk()

    @Test
    fun `findByUsername non-existent user`() {
        every { userRepository.findUserByUserName("non-existent") } returns null
        val result = userService.findByUsername("non-existent")
        verify {userRepository.findUserByUserName("non-existent")}
        Assertions.assertNull(result)
    }

    @Test
    fun `findByUsername existing user`() {
        every { userRepository.findUserByUserName("testUser1") } returns user
        val result = userService.findByUsername("testUser1")
        verify { userRepository.findUserByUserName("testUser1") }
        Assertions.assertEquals(user, result)
    }

    @Test
    fun `createUser user exists`() {
        every { userRepository.existsUserByUserName("testUser") } returns true
        val result = userService.createUser("testUser", "testUser")
        verify { userRepository.existsUserByUserName("testUser") }
        Assertions.assertNull(result)
    }

    @Test
    fun `createUser user non-existent`() {
        every { userRepository.existsUserByUserName("testUser")} returns false
        every { userRepository.save(User("testUser", "testUser", Role.USER)) } returns user
        val result = userService.createUser("testUser", "testUser")
        verify { userRepository.existsUserByUserName("testUser") }
        verify { userRepository.save(User("testUser", "testUser", Role.USER)) }
        Assertions.assertEquals(user, result)
    }

    @Test
    fun `deleteUser user exists`() {
        every { userRepository.deleteUserByUserName("testUser") } returns user
        val result = userService.deleteUser("testUser")
        verify { userRepository.deleteUserByUserName("testUser") }
        Assertions.assertEquals(user, result)
    }

    @Test
    fun `deleteUser user non-existent`() {
        every { userRepository.deleteUserByUserName("testUser") } returns null
        val result = userService.deleteUser("testUser")
        verify { userRepository.deleteUserByUserName("testUser") }
        Assertions.assertNull(result)
    }

    @Test
    fun `editUsername user exists`() {
        every { jwtService.getUserName() } returns "testUser"
        every { userRepository.findUserByUserName("testUser") } returns user
        every { user.id } returns ""
        every { userRepository.changeUsername("newTestUser", "") } returns user // maybe mock other user?
        val result = userService.editUsername("newTestUser")
        verify { jwtService.getUserName() }
        verify { userRepository.findUserByUserName("testUser") }
        verify { user.id }
        verify { userRepository.changeUsername("newTestUser", "") }
        Assertions.assertEquals(user, result)
    }

    @Test
    fun `editUsername user non-existent`() {
        every { jwtService.getUserName() } returns "testUser"
        every { userRepository.findUserByUserName("testUser") } returns null
        val result = userService.editUsername( "newTestUser")
        verify { jwtService.getUserName() }
        verify { userRepository.findUserByUserName("testUser") }
        Assertions.assertNull(result)
    }
}