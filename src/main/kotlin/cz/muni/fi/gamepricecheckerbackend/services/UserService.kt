package cz.muni.fi.gamepricecheckerbackend.services

import cz.muni.fi.gamepricecheckerbackend.models.User
import cz.muni.fi.gamepricecheckerbackend.repositories.UserRepository
import org.springframework.stereotype.Service

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
class UserService(private val userRepository: UserRepository) {
    fun findByUsername(username: String): User? {
        return userRepository.findUserByUserName(username)
    }

    fun createUser(username: String, password: String): User? {
        if (userRepository.existsUserByUserName(username)) {
            return null
        }
        return userRepository.save(User(username, password))
    }

    fun deleteUser(username: String): User? {
        return userRepository.deleteUserByUserName(username)
    }
}