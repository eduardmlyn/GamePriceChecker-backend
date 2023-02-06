package cz.muni.fi.gamepricecheckerbackend

import cz.muni.fi.gamepricecheckerbackend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GamePriceCheckerBackendApplication {
    @Autowired
    lateinit var userService: UserService
}

fun main(args: Array<String>) {
    runApplication<GamePriceCheckerBackendApplication>(*args)
}
