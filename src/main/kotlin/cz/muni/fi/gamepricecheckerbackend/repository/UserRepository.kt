package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface UserRepository : JpaRepository<User, String> {
    fun findUserByUserName(username: String): User?
}
