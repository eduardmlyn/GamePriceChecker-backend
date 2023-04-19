package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.model.authentication.AuthenticationResponse
import cz.muni.fi.gamepricecheckerbackend.model.dto.GameDTO
import cz.muni.fi.gamepricecheckerbackend.model.entity.User
import cz.muni.fi.gamepricecheckerbackend.model.enums.Order
import cz.muni.fi.gamepricecheckerbackend.model.enums.SortBy
import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import cz.muni.fi.gamepricecheckerbackend.repository.UserRepository
import cz.muni.fi.gamepricecheckerbackend.security.JwtService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
    private val jwtService: JwtService,
    private val blackListService: BlackListService
) {
    fun findByUsername(username: String): User? {
        return userRepository.findUserByUserName(username)
    }

    @Transactional
    fun deleteUser(): Unit? {
        val username = jwtService.getUserName()
        if (userRepository.deleteUserByUserName(username) > 0) return Unit
        return null
    }

    @Transactional
    fun editUsername(username: String, token: String): AuthenticationResponse? {
        val currentUserName = jwtService.getUserName()
        val user = userRepository.findUserByUserName(currentUserName) ?: return null
        if (userRepository.findUserByUserName(username) != null) return null
        user.userName = username
        userRepository.save(user)
        blackListService.addToBlackList(token)
        val jwtToken = jwtService.generateToken(user)
        return AuthenticationResponse(jwtToken)
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

    fun getUserFavorites(page: Int, pageSize: Int, sortBy: SortBy, order: Order, filter: String): List<GameDTO> {
        val username = jwtService.getUserName()
        val user = userRepository.findUserByUserName(username) ?: return emptyList()
        val sortValue = if (sortBy == SortBy.RELEASE_DATE) {
            "release_date"
        } else {
            sortBy.value
        }
        val sort = when (order) {
            Order.ASC -> {
                Sort.by(sortValue).ascending()
            }

            Order.DESC -> {
                Sort.by(sortValue).descending()
            }
        }
        val games = gameRepository.findUserGameFavorites(filter, user.id, PageRequest.of(page, pageSize, sort)).content
        return games.map { GameDTO(it.id, it.name, it.imageUrl, it.releaseDate) }
    }

    fun getUserFavoriteCount(filter: String): Long {
        val username = jwtService.getUserName()
        val user = userRepository.findUserByUserName(username) ?: return 0
        return gameRepository.favoriteCountFiltered(filter, user.id)
    }

    fun logout(token: String) {
        blackListService.addToBlackList(token)
    }
}
