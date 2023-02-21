package cz.muni.fi.gamepricecheckerbackend.facade

import cz.muni.fi.gamepricecheckerbackend.model.User
import cz.muni.fi.gamepricecheckerbackend.service.UserService
import org.springframework.stereotype.Component

/**
 *
 * @author Eduard Stefan Mlynarik
 */

interface UserFacade {
    fun getUserByUsername(username: String): User?
    fun createUser(username: String, password: String): User?
    fun deleteUser(username: String): User?
    fun editUsername(username: String): User?
}

@Component
class UserFacadeImpl(val userService: UserService): UserFacade {
    // TODO move logic here
    override fun getUserByUsername(username: String): User? {
        return userService.findByUsername(username)
    }

    override fun createUser(username: String, password: String): User? {
        return userService.createUser(username, password)
    }

    override fun deleteUser(username: String): User? {
        return userService.deleteUser(username)
    }

    override fun editUsername(username: String): User? {
        // TODO jwt invalidation + refresh(frontend?)
        return userService.editUsername(username)
    }
}