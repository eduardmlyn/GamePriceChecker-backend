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

    fun deleteUser(username: String): User? {
        return userRepository.deleteUserByUserName(username)
    }

    fun editUsername(username: String) {
        val currentUserName = jwtService.getUserName()
        val user = findByUsername(currentUserName) ?: return
        userRepository.changeUsername(username, user.id)
    }
}