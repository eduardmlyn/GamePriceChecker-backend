package cz.muni.fi.gamepricecheckerbackend.util

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import cz.muni.fi.gamepricecheckerbackend.model.event.TaskDoneEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 *
 * @author Eduard Stefan Mlynarik
 */
@Component
class TaskDoneEventPublisher(private val applicationEventPublisher: ApplicationEventPublisher) {

    fun publishTaskDone(message: String, seller: Seller) {
        println("Task done with message: $message") // change to log?
        applicationEventPublisher.publishEvent(TaskDoneEvent(seller))
    }
}