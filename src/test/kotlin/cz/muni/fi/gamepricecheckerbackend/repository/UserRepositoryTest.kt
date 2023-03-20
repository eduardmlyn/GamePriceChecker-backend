package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.enums.Role
import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestEntityManager
@DataJpaTest
class UserRepositoryTest(
) {
    private val entityManager: TestEntityManager = mockk()
    private val userRepository: UserRepository = mockk()
    private var testUser = User()

    @BeforeEach
    fun init() {
//        every { User("",Role.USER, "userTest", "user") } answers { User() }
        testUser = User("testId", Role.USER, "userTest", "user", emptyList())
        println(testUser)
        entityManager.persist(testUser)
        entityManager.flush()
    }

    @Test
    fun `When findUserByUserName then return User`() {
        every { userRepository.findUserByUserName("userTest") } returns User()
        assertEquals(userRepository.findUserByUserName("userTest"), testUser)
    }

    @Test
    fun `When existsUserByUserName then return true`() {
        every { userRepository.existsUserByUserName("userTest") } answers { true }
        assert(userRepository.existsUserByUserName("userTest"))
    }

    @Test
    fun `When existsUserByUserName return false`() {
        val exists = userRepository.existsUserByUserName("noValidUser")
        assert(exists)
    }
}