package cz.muni.fi.gamepricecheckerbackend.repositories

import cz.muni.fi.gamepricecheckerbackend.models.User
import org.springframework.data.jpa.repository.JpaRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface UserRepository: JpaRepository<User, Long> {
    fun findUserByUserName(username: String): User?
    fun deleteUserByUserName(username: String): User?
    fun existsUserByUserName(username: String): Boolean
}