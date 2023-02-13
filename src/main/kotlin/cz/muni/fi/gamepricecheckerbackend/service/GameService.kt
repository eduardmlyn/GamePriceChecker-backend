package cz.muni.fi.gamepricecheckerbackend.service

import cz.muni.fi.gamepricecheckerbackend.repository.GameRepository
import org.springframework.stereotype.Service

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Service
class GameService(private val gameRepository: GameRepository) {

    fun test() {
        TODO()
    }
}