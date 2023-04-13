package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import cz.muni.fi.gamepricecheckerbackend.security.JwtService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val jwtService: JwtService
) {
    fun findByUsername(username: String): User? {
        return userRepository.findUserByUserName(username)
    }

    @Transactional
    fun deleteUser(): User? {
        val username = jwtService.getUserName()
        return userRepository.deleteUserByUserName(username)
    }

    @Transactional
    fun editUsername(username: String) {
        val currentUserName = jwtService.getUserName()
        val user = findByUsername(currentUserName) ?: return
        userRepository.changeUsername(username, user.id)
    }


    @Transactional
    fun changeGameToUserRelation(gameId: String): Boolean {
        val username = jwtService.getUserName()
        val user = userRepository.findUserByUserName(username) ?: return false
        if (gameId in user.favorites.map { it.id }) {
            user.favorites.removeIf { it.id == gameId }
        } else {
            val game = gameRepository.findGameById(gameId) ?: return false
            user.favorites.add(game)
        }
        userRepository.save(user)
        return true
    }

    fun getUserFavorites(page: Int, pageSize: Int): List<GameDTO> {
        val username = jwtService.getUserName()
        val user = userRepository.findUserByUserName(username) ?: return emptyList()
        val botIndex = pageSize * page
        val topIndex = if (pageSize * (page + 1) < user.favorites.size) pageSize * (page + 1) else user.favorites.size
        val games = user.favorites.subList(botIndex, topIndex)
        return games.map { GameDTO(it.id, it.name, it.imageUrl, it.releaseDate) }
    }

    fun getUserFavoriteCount(): Int {
        val username = jwtService.getUserName()
        val user = userRepository.findUserByUserName(username) ?: return 0
        return user.favorites.size
    }
}
