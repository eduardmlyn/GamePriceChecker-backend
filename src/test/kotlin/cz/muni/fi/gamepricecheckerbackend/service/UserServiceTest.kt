package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.Role
import cz.muni.fi.gamepricecheckerbackend.model.User
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

/**
 *
 * @author Eduard Stefan Mlynarik
 */
internal class UserServiceTest {
    private val userRepository: UserRepository = mockk()
    val userService: UserService = UserService(userRepository)

    @Test
    fun findByUsername() {
        every { userRepository.findUserByUserName("test") } returns User()
        val result = userService.findByUsername("test")
        verify(exactly = 1) {userRepository.findUserByUserName("test")}
        assertEquals(User(), result)
    }

    @Test
    fun createUser() {
        val user = User("creationTest", "creationTest", Role.USER)
        every { userRepository.save(user) } returns user

    }

    @Test
    fun deleteUser() {
    }

    @Test
    fun editUsername() {
    }
}