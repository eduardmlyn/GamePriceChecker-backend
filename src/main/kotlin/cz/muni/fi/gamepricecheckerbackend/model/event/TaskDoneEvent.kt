package cz.muni.fi.gamepricecheckerbackend.model.event

import cz.muni.fi.gamepricecheckerbackend.model.enums.Seller
import org.springframework.context.ApplicationEvent

/**
 *
 * @author Eduard Stefan Mlynarik
 */
class TaskDoneEvent(val seller: Seller) : ApplicationEvent(seller)
