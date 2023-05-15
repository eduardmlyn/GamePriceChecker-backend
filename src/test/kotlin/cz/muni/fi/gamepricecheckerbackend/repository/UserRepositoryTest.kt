package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.enums.Role
import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

/**
 *
 * @author Eduard Stefan Mlynarik
 */
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@AutoConfigureTestEntityManager
@DataJpaTest
class UserRepositoryTest() {
    @Autowired
    lateinit var entityManager: TestEntityManager
    @Autowired
    lateinit var userRepository: UserRepository

    // TODO change this
//    @BeforeEach
//    fun init() {
//        testUser = User("userTest", "userTest", Role.USER)
//        println(testUser)
//        every { entityManager.persist(any<User>()) } returns User("userTest", "userTest", Role.USER)
//        entityManager.persist(testUser)
//        every { entityManager.flush() } returns Unit
//        entityManager.flush()
//    }

    @Test
    fun `When findUserByUserName then return User`() {
        val username = "userTest"
        val expectedUser = User(username, "password", Role.USER)
        every { userRepository.findUserByUserName(username) } returns expectedUser

        // When
        val result = userRepository.findUserByUserName(username)

        // Then
        verify(exactly = 1) { userRepository.findUserByUserName(username) }
        assertEquals(expectedUser, result)
    }
}