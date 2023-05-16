package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.model.enums.Role
import org.junit.jupiter.api.Assertions
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
class UserRepositoryTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `findUserByUserName user with username present`() {
        val username = "userTest"
        val expectedUser = User(username, "password", Role.USER)

        userRepository.save(expectedUser)

        val result = userRepository.findUserByUserName(username)

        Assertions.assertAll(
            "User",
            { Assertions.assertNotNull(result) },
            { Assertions.assertEquals(expectedUser.userName, result?.userName) },
            { Assertions.assertEquals(expectedUser.password, result?.password) }
        )
    }

    @Test
    fun `findUserByUserName user with username not present`() {
        val username = "userTest"
        val expectedUser = User("differentUser", "password", Role.USER)

        userRepository.save(expectedUser)

        val result = userRepository.findUserByUserName(username)

        Assertions.assertNull(result)
    }
}
