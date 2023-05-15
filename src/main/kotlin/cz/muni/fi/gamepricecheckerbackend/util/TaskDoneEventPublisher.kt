package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.model.event.TaskDoneEvent
import org.slf4j.Logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Component
class TaskDoneEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val logger: Logger
) {

    fun publishTaskDone(message: String, seller: Seller) {
        logger.info("Task done with message: $message")
        applicationEventPublisher.publishEvent(TaskDoneEvent(seller))
    }
}
