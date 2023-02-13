package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.Role
import cz.muni.fi.gamepricecheckerbackend.model.User
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
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
        return userRepository.save(User(username, password, Role.USER))
    }

    fun deleteUser(username: String): User? {
        return userRepository.deleteUserByUserName(username)
    }

    fun editUsername(username: String): User? {
        // TODO change to get user and save() + add username check
        val user = findByUsername(username) ?: return null
        return userRepository.changeUsername(username, user.id)
    }
}