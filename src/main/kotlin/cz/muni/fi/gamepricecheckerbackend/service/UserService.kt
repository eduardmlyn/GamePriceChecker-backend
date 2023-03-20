package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.enums.Role
import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import cz.muni.fi.gamepricecheckerbackend.security.JwtService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
@Transactional
class UserService(private val userRepository: UserRepository, private val jwtService: JwtService) {
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
        val currentUserName = jwtService.getUserName()
        val user = findByUsername(currentUserName) ?: return null
        return userRepository.changeUsername(username, user.id)
    }
}