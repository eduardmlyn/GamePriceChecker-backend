package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.GamePriceCheckerBackendApplication
import cz.muni.fi.gamepricecheckerbackend.model.Role
import cz.muni.fi.gamepricecheckerbackend.model.User
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
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
    private val userService: UserService = UserService(userRepository)
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
        every { userRepository.save(User("testUser", "testUser", Role.USER)) } returns user
        every { userRepository.existsUserByUserName("testUser")} returns false
        val result = userService.createUser("testUser", "testUser")
        Assertions.assertEquals(user, result)
    }

    @Test
    fun deleteUser() {
    }

    @Test
    fun editUsername() {
    }
}