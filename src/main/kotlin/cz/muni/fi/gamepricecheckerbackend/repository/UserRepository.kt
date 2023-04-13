package cz.muni.fi.gamepricecheckerbackend.repository

import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

/**
 *
 * @author Eduard Stefan Mlynarik
 */
interface UserRepository : JpaRepository<User, String> {
    fun findUserByUserName(username: String): User?
    fun deleteUserByUserName(username: String): User?

    @Modifying
    @Query("UPDATE User u set u.userName = :username where u.id = :id")
    fun changeUsername(@Param("username") username: String, @Param("id") id: String): User
}
