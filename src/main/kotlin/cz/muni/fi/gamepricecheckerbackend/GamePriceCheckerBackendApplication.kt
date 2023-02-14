package cz.muni.fi.gamepricecheckerbackend

import cz.muni.fi.gamepricecheckerbackend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignAutoConfiguration

@SpringBootApplication
@EnableFeignClients
@ImportAutoConfiguration(FeignAutoConfiguration::class)
class GamePriceCheckerBackendApplication {
    @Autowired
    lateinit var userService: UserService
}

fun main(args: Array<String>) {
    runApplication<GamePriceCheckerBackendApplication>(*args)
}
