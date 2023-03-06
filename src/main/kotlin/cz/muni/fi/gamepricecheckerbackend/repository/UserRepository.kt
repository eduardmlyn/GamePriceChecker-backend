package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.User
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Transactional
interface UserRepository: JpaRepository<User, String> {
    fun findUserByUserName(username: String): User?
    fun deleteUserByUserName(username: String): User?
    fun existsUserByUserName(username: String): Boolean
    @Modifying
    @Query("UPDATE User u set u.userName = ?1 where u.id = ?2")
    fun changeUsername(username: String, id: String): User?
}