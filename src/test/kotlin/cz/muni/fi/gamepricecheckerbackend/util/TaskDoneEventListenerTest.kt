package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.service.GameService
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.slf4j.Logger

/**
 *
 * @author Eduard Stefan Mlynarik
 */
class TaskDoneEventListenerTest {
    private val gameService: GameService = mockk()
    private val logger: Logger = mockk()
    private val taskDoneEventListener = TaskDoneEventListener(gameService, logger)

    @Test
    fun test() {
        // TODO make function internal or try to access it with kotlin reflection
//        TaskDoneEventListener.javaClass.getDeclaredMethod("createPriceSnapshots")
    }

}